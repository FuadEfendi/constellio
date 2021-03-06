package com.constellio.model.services.records;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.constellio.data.dao.dto.records.RecordDTO;
import com.constellio.data.utils.KeyListMap;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.RecordWrapper;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.schemas.entries.DataEntryType;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.utils.DependencyUtils;

public class RecordUtils {

	private SchemaUtils schemaUtils;

	public RecordUtils() {
		schemaUtils = newSchemaUtils();
	}

	public List<String> toIdList(List<Record> records) {
		List<String> idList = new ArrayList<>();

		for (Record record : records) {
			idList.add(record.getId());
		}
		return idList;
	}

	public List<String> toWrappedRecordIdsList(List<? extends RecordWrapper> records) {
		List<String> idList = new ArrayList<>();

		for (RecordWrapper record : records) {
			idList.add(record.getId());
		}
		return idList;
	}

	public Map<String, List<Record>> splitRecordsBySchemaTypes(List<Record> records) {
		KeyListMap<String, Record> recordsSplittedByTypes = new KeyListMap<>();

		SchemaUtils schemaUtils = new SchemaUtils();
		for (Record record : records) {
			String schemaType = schemaUtils.getSchemaTypeCode(record.getSchemaCode());
			recordsSplittedByTypes.add(schemaType, record);
		}
		return recordsSplittedByTypes.getNestedMap();
	}

	public Map<String, Record> toIdRecordMap(List<Record> records) {
		Map<String, Record> idRecordMap = new HashMap<>();

		for (Record record : records) {
			idRecordMap.put(record.getId(), record);
		}
		return idRecordMap;
	}

	public List<Record> sortRecordsOnDependencies(List<Record> unsortedRecords, MetadataSchemaTypes schemaTypes) {
		schemaUtils = new SchemaUtils();
		List<Record> recordsSortedOnDependencies = new ArrayList<>();

		KeyListMap<String, Record> keyListMap = new KeyListMap<>();

		for (Record record : unsortedRecords) {

			keyListMap.add(schemaUtils.getSchemaTypeCode(record.getSchemaCode()), record);
		}

		List<String> typesSortedByDependency = schemaTypes.getSchemaTypesSortedByDependency();
		for (String schemaTypeCode : typesSortedByDependency) {
			MetadataSchemaType type = schemaTypes.getSchemaType(schemaTypeCode);
			List<Record> records = keyListMap.get(schemaTypeCode);
			if (records != null) {
				recordsSortedOnDependencies.addAll(sortRecordsOfType(type, records));
			}
		}

		return recordsSortedOnDependencies;
	}

	private List<Record> sortRecordsOfType(MetadataSchemaType schemaType, List<Record> unsortedRecords) {
		List<Record> sortedRecords = new ArrayList<>();

		List<Metadata> referenceMetadatas = schemaType.getAllParentReferences();

		Map<String, Set<String>> dependencyMap = new HashMap<>();
		for (Record record : unsortedRecords) {
			String parentDependencyId = record.getNonNullValueIn(referenceMetadatas);
			dependencyMap.put(record.getId(), Collections.singleton(parentDependencyId));
		}
		List<String> sortedIds = new DependencyUtils<String>().sortByDependency(dependencyMap);
		Map<String, Record> idRecordMap = toIdRecordMap(unsortedRecords);
		for (String recordId : sortedIds) {
			sortedRecords.add(idRecordMap.get(recordId));
		}
		return sortedRecords;
	}

	public List<String> getModifiedMetadatasDataStoreCodes(List<Record> records) {
		Set<String> modifiedMetadatasCodes = new HashSet<>();
		for (Record record : records) {
			RecordImpl recordImpl = (RecordImpl) record;
			Map<String, Object> modifiedValues = recordImpl.getModifiedValues();
			modifiedMetadatasCodes.addAll(modifiedValues.keySet());
		}

		return new ArrayList<>(modifiedMetadatasCodes);
	}

	SchemaUtils newSchemaUtils() {
		return new SchemaUtils();
	}

	public List<RecordDTO> toRecordDTOList(List<Record> records) {

		List<RecordDTO> recordDTOs = new ArrayList<>();

		for (Record record : records) {
			recordDTOs.add(((RecordImpl) record).getRecordDTO());
		}

		return recordDTOs;
	}

	public List<Record> newListWithoutDuplicates(List<Record> records) {
		List<Record> listWithoutDuplicates = new ArrayList<>();
		Set<String> ids = new HashSet<>();

		for (Record record : records) {
			if (!ids.contains(record.getId())) {
				ids.add(record.getId());
				listWithoutDuplicates.add(record);
			}
		}

		return listWithoutDuplicates;
	}

	public List<RecordWrapper> newWrappersListWithoutDuplicates(List<RecordWrapper> recordWrappers) {
		List<RecordWrapper> listWithoutDuplicates = new ArrayList<>();
		Set<String> ids = new HashSet<>();

		for (RecordWrapper recordWrapper : recordWrappers) {
			if (!ids.contains(recordWrapper.getId())) {
				ids.add(recordWrapper.getId());
				listWithoutDuplicates.add(recordWrapper);
			}
		}

		return listWithoutDuplicates;
	}

	public String getRecordsCollection(List<Record> records) {
		String collection = null;

		for (Record record : records) {
			if (collection == null) {
				collection = record.getCollection();
			} else if (collection.equals(record.getCollection())) {
				throw new RuntimeException("Records are in different collections");
			}
		}

		return collection;
	}

	public Record findRecordWithId(List<Record> records, String id) {
		for (Record record : records) {
			if (record.getId().equals(id)) {
				return record;
			}
		}
		return null;
	}

	public static List<Record> unwrap(List<? extends RecordWrapper> recordWrappers) {
		List<Record> records = new ArrayList<>();
		for (RecordWrapper recordWrapper : recordWrappers) {
			records.add(recordWrapper.getWrappedRecord());
		}
		return records;
	}

	public static void copyMetadatas(RecordWrapper source, RecordWrapper destination) {
		copyMetadatas(source.getWrappedRecord(), destination.getWrappedRecord(), source.getMetadataSchemaTypes());
	}

	private static List<String> excludedMetadatas = asList(Schemas.IDENTIFIER.getLocalCode(), Schemas.LEGACY_ID.getLocalCode());

	public static void copyMetadatas(Record source, Record destination, MetadataSchemaTypes types) {
		MetadataSchema sourceRecordSchema = types.getSchema(source.getSchemaCode());
		MetadataSchema destinationRecordSchema = types.getSchema(destination.getSchemaCode());

		for (Metadata sourceMetadata : sourceRecordSchema.getMetadatas()) {

			String sourceMetadataLocalCode = SchemaUtils.getMetadataLocalCodeWithoutPrefix(sourceMetadata);

			for (Metadata destinationMetadata : destinationRecordSchema.getMetadatas()) {
				String destMetadataLocalCode = SchemaUtils.getMetadataLocalCodeWithoutPrefix(destinationMetadata);
				if (sourceMetadataLocalCode.equals(destMetadataLocalCode)) {
					Object value = source.get(sourceMetadata);
					if (destinationMetadata.getDataEntry().getType() == DataEntryType.MANUAL
							&& destinationMetadata.getType() == sourceMetadata.getType()
							&& destinationMetadata.isMultivalue() == sourceMetadata.isMultivalue()
							&& !destinationMetadata.isSystemReserved()
							&& value != null
							&& !excludedMetadatas.contains(destinationMetadata.getLocalCode())) {

						destination.set(destinationMetadata, value);
					}
				}
			}
		}
	}

	public static void changeSchemaTypeAccordingToTypeLinkedSchema(Record record, MetadataSchemaTypes schemaTypes,
			RecordProvider recordProvider) {
		MetadataSchema recordSchema = schemaTypes.getSchema(record.getSchemaCode());

		for (Metadata metadata : recordSchema.getMetadatas()) {

			if (schemaTypes.isRecordTypeMetadata(metadata)) {
				changeSchemaTypeAccordingToTypeLinkedSchema(record, schemaTypes, recordProvider, metadata);
			}
		}
	}

	public static void changeSchemaTypeAccordingToTypeLinkedSchema(Record record, MetadataSchemaTypes schemaTypes,
			RecordProvider recordProvider, Metadata typeMetadata) {
		MetadataSchema recordSchema = schemaTypes.getSchema(record.getSchemaCode());
		String newSchemaCode = getSchemaAccordingToTypeLinkedSchema(record, schemaTypes, recordProvider, typeMetadata);
		if (!record.getSchemaCode().equals(newSchemaCode)) {
			MetadataSchema newSchema = schemaTypes.getSchema(newSchemaCode);
			record.changeSchema(recordSchema, newSchema);
		}

	}

	public static String getSchemaAccordingToTypeLinkedSchema(Record record, MetadataSchemaTypes schemaTypes,
			RecordProvider recordProvider, Metadata typeMetadata) {
		MetadataSchema recordSchema = schemaTypes.getSchema(record.getSchemaCode());
		MetadataSchema referencedSchema = schemaTypes.getDefaultSchema(typeMetadata.getReferencedSchemaType());
		String schemaTypeCode = new SchemaUtils().getSchemaTypeCode(record.getSchemaCode());
		String typeId = record.get(typeMetadata);
		String customSchema = null;
		if (typeId != null) {

			Record typeRecord = recordProvider.getRecord(typeId);
			customSchema = typeRecord.get(referencedSchema.get("linkedSchema"));
		}

		if (customSchema != null && customSchema.contains("_")) {
			return customSchema;
		}

		return schemaTypeCode + "_" + (customSchema == null ? "default" : customSchema);
	}
}
