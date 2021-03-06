package com.constellio.model.services.records.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.cache.RecordsCacheImplRuntimeException.RecordsCacheImplRuntimeException_InvalidSchemaTypeCode;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQuerySignature;
import com.constellio.model.services.search.query.logical.condition.DataStoreFilters;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.model.services.search.query.logical.condition.SchemaFilters;

public class RecordsCacheImpl implements RecordsCache {

	SchemaUtils schemaUtils = new SchemaUtils();

	Map<String, RecordHolder> cacheById = new HashMap<>();

	Map<String, RecordByMetadataCache> recordByMetadataCache = new HashMap<>();
	Map<String, VolatileCache> volatileCaches = new HashMap<>();
	Map<String, PermanentCache> permanentCaches = new HashMap<>();

	Map<String, CacheConfig> cachedTypes = new HashMap<>();

	public boolean isCached(String id) {
		RecordHolder holder = cacheById.get(id);
		return holder != null && holder.getCopy() != null;
	}

	@Override
	public Record get(String id) {
		RecordHolder holder = cacheById.get(id);

		Record copy = null;
		if (holder != null) {
			copy = holder.getCopy();

			if (copy != null) {
				CacheConfig config = getCacheConfigOf(copy.getSchemaCode());
				if (config.isVolatile()) {
					VolatileCache cache = volatileCaches.get(config.getSchemaType());
					synchronized (this) {
						cache.hit(holder);
					}
				}

			}

		}

		return copy;
	}

	public synchronized void insert(List<Record> records) {
		if (records != null) {
			for (Record record : records) {
				insert(record);
			}
		}
	}

	@Override
	public void insertQueryResults(LogicalSearchQuery query, List<Record> records) {

		PermanentCache cache = getCacheFor(query);
		if (cache != null) {
			LogicalSearchQuerySignature signature = LogicalSearchQuerySignature.signature(query);

			List<String> recordIds = new ArrayList<>();
			for (Record record : records) {
				recordIds.add(record.getId());
				insert(record);
			}

			cache.queryResults.put(signature.toStringSignature(), recordIds);
		}
	}

	PermanentCache getCacheFor(LogicalSearchQuery query) {
		LogicalSearchCondition condition = query.getCondition();
		DataStoreFilters filters = condition.getFilters();
		if (filters instanceof SchemaFilters) {
			SchemaFilters schemaFilters = (SchemaFilters) filters;

			if (schemaFilters.getSchemaTypeFilter() != null
					&& hasNoUnsupportedFeatureOrFilter(query)) {
				CacheConfig cacheConfig = getCacheConfigOf(schemaFilters.getSchemaTypeFilter().getCode());
				if (cacheConfig != null && cacheConfig.isPermanent()) {
					return permanentCaches.get(cacheConfig.getSchemaType());
				}
			}

		}
		return null;
	}

	private boolean hasNoUnsupportedFeatureOrFilter(LogicalSearchQuery query) {
		return query.getFacetFilters().toSolrFilterQueries().isEmpty()
				&& query.getFieldBoosts().isEmpty()
				&& query.getQueryBoosts().isEmpty()
				&& query.getStartRow() == 0
				&& query.getNumberOfRows() == 10000000
				&& query.getStatisticFields().isEmpty()
				&& !query.isPreferAnalyzedFields()
				&& query.getResultsProjection() == null
				&& query.getFieldFacets().isEmpty()
				&& query.getQueryFacets().isEmpty()
				&& query.getReturnedMetadatas().isFullyLoaded()
				&& query.getUserFilter() == null
				&& !query.isHighlighting();
	}

	@Override
	public List<Record> getQueryResults(LogicalSearchQuery query) {
		List<Record> cachedResults = null;
		PermanentCache cache = getCacheFor(query);
		if (cache != null) {
			LogicalSearchQuerySignature signature = LogicalSearchQuerySignature.signature(query);

			List<String> recordIds = cache.queryResults.get(signature.toStringSignature());
			if (recordIds != null) {
				cachedResults = new ArrayList<>();

				for (String recordId : recordIds) {
					cachedResults.add(get(recordId));
				}
				cachedResults = Collections.unmodifiableList(cachedResults);
			}

		}

		return cachedResults;
	}

	@Override
	public Record insert(Record insertedRecord) {

		if (insertedRecord == null || insertedRecord.isDirty() || !insertedRecord.isSaved()) {
			return insertedRecord;
		}

		if (!insertedRecord.isFullyLoaded()) {
			invalidate(insertedRecord.getId());
			return insertedRecord;
		}

		Record recordCopy = insertedRecord.getCopyOfOriginalRecord();

		CacheConfig cacheConfig = getCacheConfigOf(recordCopy.getSchemaCode());
		if (cacheConfig != null) {
			Record previousRecord = null;

			synchronized (this) {
				RecordHolder holder = cacheById.get(recordCopy.getId());
				if (holder != null) {
					previousRecord = holder.record;

					insertRecordIntoAnAlreadyExistingHolder(recordCopy, cacheConfig, holder);
					if (cacheConfig.isPermanent() && (previousRecord == null || previousRecord.getVersion() != recordCopy
							.getVersion())) {
						permanentCaches.get(cacheConfig.getSchemaType()).queryResults.clear();
					}
				} else {
					holder = insertRecordIntoAnANewHolder(recordCopy, cacheConfig);
					if (cacheConfig.isPermanent()) {
						permanentCaches.get(cacheConfig.getSchemaType()).queryResults.clear();
					}
				}
				this.recordByMetadataCache.get(cacheConfig.getSchemaType()).insert(previousRecord, holder);
			}

		}
		return insertedRecord;
	}

	private RecordHolder insertRecordIntoAnANewHolder(Record record, CacheConfig cacheConfig) {
		RecordHolder holder = new RecordHolder(record);
		cacheById.put(record.getId(), holder);
		if (cacheConfig.isVolatile()) {
			VolatileCache cache = volatileCaches.get(cacheConfig.getSchemaType());
			cache.releaseFor(1);
			cache.insert(holder);
		} else {
			PermanentCache cache = permanentCaches.get(cacheConfig.getSchemaType());
			cache.insert(holder);
		}

		return holder;
	}

	private void insertRecordIntoAnAlreadyExistingHolder(Record record, CacheConfig cacheConfig, RecordHolder currentHolder) {
		if (currentHolder.record == null && cacheConfig.isVolatile()) {
			VolatileCache cache = volatileCaches.get(cacheConfig.getSchemaType());
			cache.releaseFor(1);
			cache.insert(currentHolder);
		}

		currentHolder.set(record);
	}

	@Override
	public synchronized void invalidateRecordsOfType(String recordType) {
		CacheConfig cacheConfig = cachedTypes.get(recordType);
		if (cacheConfig.isVolatile()) {
			volatileCaches.get(cacheConfig.getSchemaType()).invalidateAll();
		} else {
			permanentCaches.get(cacheConfig.getSchemaType()).invalidateAll();
		}
	}

	public synchronized void invalidate(List<String> recordIds) {
		if (recordIds != null) {
			for (String recordId : recordIds) {
				invalidate(recordId);
			}
		}
	}

	@Override
	public synchronized void invalidate(String recordId) {
		if (recordId != null) {
			RecordHolder holder = cacheById.get(recordId);
			if (holder != null && holder.record != null) {
				CacheConfig cacheConfig = getCacheConfigOf(holder.record.getSchemaCode());
				recordByMetadataCache.get(cacheConfig.getSchemaType()).invalidate(holder.record);
				holder.invalidate();

				if (cacheConfig.isPermanent()) {
					permanentCaches.get(cacheConfig.getSchemaType()).queryResults.clear();
				}
			}
		}

	}

	public CacheConfig getCacheConfigOf(String schemaOrTypeCode) {
		String schemaTypeCode = schemaUtils.getSchemaTypeCode(schemaOrTypeCode);
		return cachedTypes.get(schemaTypeCode);
	}

	@Override
	public void configureCache(CacheConfig cacheConfig) {

		if (cacheConfig == null) {
			throw new IllegalArgumentException("Required parameter 'cacheConfig'");
		}
		if (cacheConfig.getSchemaType().contains("_")) {
			throw new RecordsCacheImplRuntimeException_InvalidSchemaTypeCode(cacheConfig.getSchemaType());
		}
		if (cachedTypes.containsKey(cacheConfig.getSchemaType())) {
			removeCache(cacheConfig.getSchemaType());
		}

		cachedTypes.put(cacheConfig.getSchemaType(), cacheConfig);
		if (cacheConfig.isPermanent()) {
			permanentCaches.put(cacheConfig.getSchemaType(), new PermanentCache());
		} else {
			volatileCaches.put(cacheConfig.getSchemaType(), new VolatileCache(cacheConfig.getVolatileMaxSize()));
		}

		recordByMetadataCache.put(cacheConfig.getSchemaType(), new RecordByMetadataCache(cacheConfig));
	}

	@Override
	public Collection<CacheConfig> getConfiguredCaches() {
		return cachedTypes.values();
	}

	@Override
	public void invalidateAll() {
		cacheById.clear();
		for (VolatileCache cache : volatileCaches.values()) {
			cache.invalidateAll();
		}

		for (PermanentCache cache : permanentCaches.values()) {
			cache.invalidateAll();
		}
	}

	@Override
	public Record getByMetadata(Metadata metadata, String value) {
		String schemaTypeCode = schemaUtils.getSchemaTypeCode(metadata);
		RecordByMetadataCache recordByMetadataCache = this.recordByMetadataCache.get(schemaTypeCode);

		Record foundRecord = null;
		if (recordByMetadataCache != null) {
			foundRecord = recordByMetadataCache.getByMetadata(metadata.getLocalCode(), value);
		}
		return foundRecord;
	}

	@Override
	public synchronized void removeCache(String schemaType) {
		recordByMetadataCache.remove(schemaType);
		if (volatileCaches.containsKey(schemaType)) {
			volatileCaches.get(schemaType).invalidateAll();
			volatileCaches.remove(schemaType);
		}
		if (permanentCaches.containsKey(schemaType)) {
			permanentCaches.get(schemaType).invalidateAll();
			permanentCaches.remove(schemaType);
		}

		cachedTypes.remove(schemaType);
	}

	@Override
	public boolean isConfigured(MetadataSchemaType type) {
		return isConfigured(type.getCode());
	}

	public boolean isConfigured(String typeCode) {
		return cachedTypes.containsKey(typeCode);
	}

	static class VolatileCache {

		int maxSize;

		LinkedList<RecordHolder> holders = new LinkedList<>();

		int recordsInCache;

		VolatileCache(int maxSize) {
			this.maxSize = maxSize;
		}

		void insert(RecordHolder holder) {
			holder.volatileCacheOccurences = 1;
			holders.add(holder);
			recordsInCache++;
		}

		void hit(RecordHolder holder) {
			if (holder.volatileCacheOccurences <= 2) {
				holder.volatileCacheOccurences++;
				holders.add(holder);
			}
		}

		void releaseFor(int qty) {
			while (recordsInCache + qty > maxSize) {
				releaseNext();
			}
		}

		void releaseNext() {
			RecordHolder recordHolder = holders.removeFirst();
			if (recordHolder.volatileCacheOccurences > 1) {
				recordHolder.volatileCacheOccurences--;
				releaseNext();
			} else {
				recordHolder.invalidate();
				recordsInCache--;
			}
		}

		void invalidateAll() {
			this.recordsInCache = 0;
			for (RecordHolder holder : holders) {
				holder.invalidate();
			}
			holders.clear();
		}

	}

	static class PermanentCache {

		Map<String, List<String>> queryResults = new HashMap<>();
		LinkedList<RecordHolder> holders = new LinkedList<>();

		void insert(RecordHolder holder) {
			holders.add(holder);
		}

		void invalidateAll() {
			for (RecordHolder holder : holders) {
				holder.invalidate();
			}
			queryResults.clear();
		}

	}

	static class RecordByMetadataCache {

		Map<String, Map<String, RecordHolder>> map = new HashMap<>();
		Map<String, Metadata> supportedMetadatas = new HashMap<>();

		RecordByMetadataCache(CacheConfig cacheConfig) {
			for (Metadata indexedMetadata : cacheConfig.getIndexes()) {
				supportedMetadatas.put(indexedMetadata.getLocalCode(), indexedMetadata);
				map.put(indexedMetadata.getLocalCode(), new HashMap<String, RecordHolder>());
			}
		}

		Record getByMetadata(String localCode, String value) {
			Map<String, RecordHolder> metadataMap = map.get(localCode);
			RecordHolder recordHolder = null;
			if (metadataMap != null) {
				recordHolder = metadataMap.get(value);
			}
			return recordHolder == null ? null : recordHolder.getCopy();
		}

		void insert(Record previousRecord, RecordHolder recordHolder) {

			for (Metadata supportedMetadata : supportedMetadatas.values()) {
				String value = null;
				String previousValue = null;

				if (previousRecord != null) {
					previousValue = previousRecord.get(supportedMetadata);
				}
				if (recordHolder.record != null) {
					value = recordHolder.record.get(supportedMetadata);
				}
				if (previousValue != null && !previousValue.equals(value)) {
					map.get(supportedMetadata.getLocalCode()).remove(previousValue);
				}
				if (value != null && !value.equals(previousValue)) {
					map.get(supportedMetadata.getLocalCode()).put(value, recordHolder);
				}
			}
		}

		void invalidate(Record record) {
			for (Metadata supportedMetadata : supportedMetadatas.values()) {
				String value = record.get(supportedMetadata);

				if (value != null) {
					map.get(supportedMetadata.getLocalCode()).remove(value);
				}
			}
		}
	}

	static class RecordHolder {

		private Record record;

		private int volatileCacheOccurences;

		RecordHolder(Record record) {
			set(record);
		}

		Record getCopy() {
			Record copy = record;
			if (copy != null) {
				copy = copy.getCopyOfOriginalRecord();
			}
			return copy;
		}

		void set(Record record) {
			Boolean logicallyDeletedStatus = record.get(Schemas.LOGICALLY_DELETED_STATUS);
			if (logicallyDeletedStatus == null || !logicallyDeletedStatus) {
				this.record = record.getCopyOfOriginalRecord();
			} else {
				this.record = null;
			}
		}

		void invalidate() {
			this.record = null;
		}

	}
}

