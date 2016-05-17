package com.constellio.app.ui.pages.search.batchProcessing;

import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.records.RecordUtils.changeSchemaTypeAccordingToTypeLinkedSchema;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;

import com.constellio.app.extensions.AppLayerCollectionExtensions;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.search.batchProcessing.entities.BatchProcessPossibleImpact;
import com.constellio.app.ui.pages.search.batchProcessing.entities.BatchProcessRecordFieldModification;
import com.constellio.app.ui.pages.search.batchProcessing.entities.BatchProcessRecordModifications;
import com.constellio.app.ui.pages.search.batchProcessing.entities.BatchProcessRequest;
import com.constellio.app.ui.pages.search.batchProcessing.entities.BatchProcessResults;
import com.constellio.app.ui.util.DateFormatUtils;
import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.model.entities.EnumWithSmallCode;
import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.enums.BatchProcessingMode;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.ModificationImpact;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.records.RecordProvider;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.SchemasRecordsServices;
import com.constellio.model.services.schemas.ModificationImpactCalculator;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.SearchServices;

public class BatchProcessingPresenterService {
	private static final Logger LOGGER = getLogger(BatchProcessingPresenterService.class);
	private final SchemasRecordsServices schemas;
	private final AppLayerFactory appLayerFactory;
	private final ModelLayerFactory modelLayerFactory;
	private final RecordServices recordServices;
	private final SearchServices searchServices;
	private final String collection;
	private final Locale locale;
	private final AppLayerCollectionExtensions extensions;

	public BatchProcessingPresenterService(String collection, AppLayerFactory appLayerFactory, Locale locale) {
		this.appLayerFactory = appLayerFactory;
		this.modelLayerFactory = appLayerFactory.getModelLayerFactory();
		this.collection = collection;
		this.locale = locale;
		this.schemas = new SchemasRecordsServices(collection, modelLayerFactory);
		this.recordServices = modelLayerFactory.newRecordServices();
		this.searchServices = modelLayerFactory.newSearchServices();
		this.extensions = appLayerFactory.getExtensions().forCollection(collection);
	}

	public String getOriginSchema(String schemaType, List<String> selectedRecordIds) {
		if (selectedRecordIds == null || selectedRecordIds.isEmpty()) {
			throw new ImpossibleRuntimeException("Batch processing should be done on at least one record");
		}
		String firstRecordSchema = getRecordSchemaCode(recordServices, selectedRecordIds.get(0));
		Boolean moreThanOneSchema = false;
		for (int i = 0; i < selectedRecordIds.size(); i++) {
			String currentRecordSchema = getRecordSchemaCode(recordServices, selectedRecordIds.get(i));
			if (!currentRecordSchema.equals(firstRecordSchema)) {
				moreThanOneSchema = true;
				break;
			}
		}
		if (moreThanOneSchema) {
			return schemaType + "_" + "default";
		} else {
			return firstRecordSchema;
		}
	}

	private String getRecordSchemaCode(RecordServices recordServices, String recordId) {
		return recordServices.getDocumentById(recordId).getSchemaCode();
	}

	private String getSchemataType(Set<String> recordsSchemata) {
		String firstType = getSchemaType(recordsSchemata.iterator().next());
		ensureAllSchemataOfSameType(recordsSchemata, firstType);
		return firstType;
	}

	private String getSchemaType(String schemaCode) {
		return StringUtils.substringBefore(schemaCode, "_");
	}

	private void ensureAllSchemataOfSameType(Set<String> recordsSchemata, String firstType) {
		for (String schemaCode : recordsSchemata) {
			String currentSchemaType = getSchemaType(schemaCode);
			if (!currentSchemaType.equals(firstType)) {
				throw new ImpossibleRuntimeException("Batch processing should be done on the same schema type :" +
						StringUtils.join(recordsSchemata, ";"));
			}
		}
	}

	public List<String> getDestinationSchemata(String schemaType) {
		List<String> schemataCodes = new ArrayList<>();
		List<MetadataSchema> schemata = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection)
				.getSchemaType(schemaType).getAllSchemas();
		for (MetadataSchema currentSchema : schemata) {
			schemataCodes.add(currentSchema.getCode());
		}
		return schemataCodes;
	}

	public RecordVO newRecordVO(String schemaCode, SessionContext sessionContext) {
		MetadataSchema schema = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection).getSchema(schemaCode);
		Record tmpRecord = modelLayerFactory.newRecordServices().newRecordWithSchema(schema);
		return new RecordToVOBuilder().build(tmpRecord, RecordVO.VIEW_MODE.FORM, sessionContext);
	}

	public BatchProcessResults execute(BatchProcessRequest request)
			throws RecordServicesException {

		Transaction transaction = prepareTransaction(request);
		recordServices.validateTransaction(transaction);
		BatchProcessResults results = toBatchProcessResults(transaction);
		recordServices.executeHandlingImpactsAsync(transaction);
		return results;
	}

	public BatchProcessResults simulate(BatchProcessRequest request)
			throws RecordServicesException {

		Transaction transaction = prepareTransaction(request);
		recordServices.validateTransaction(transaction);
		return toBatchProcessResults(transaction);
	}

	public BatchProcessingMode getBatchProcessingMode() {
		ConstellioEIMConfigs eimConfigs = new ConstellioEIMConfigs(modelLayerFactory.getSystemConfigurationsManager());
		return eimConfigs.getBatchProcessingMode();
	}

	private BatchProcessResults toBatchProcessResults(Transaction transaction) {

		List<BatchProcessRecordModifications> recordModificationses = new ArrayList<>();
		for (Record record : transaction.getModifiedRecords()) {

			List<BatchProcessRecordFieldModification> recordFieldModifications = new ArrayList<>();
			List<BatchProcessPossibleImpact> impacts = new ArrayList<>();
			Record originalRecord = record.getCopyOfOriginalRecord();
			for (Metadata metadata : record.getModifiedMetadatas(schemas.getTypes())) {
				if (!metadata.hasSameCode(Schemas.MODIFIED_ON) && extensions.isMetadataDisplayedWhenModifiedInBatchProcessing(metadata)) {
					String valueBefore = convertToString(metadata, originalRecord.get(metadata));
					String valueAfter = convertToString(metadata, record.get(metadata));
					recordFieldModifications.add(new BatchProcessRecordFieldModification(valueBefore, valueAfter, metadata));
				}
			}

			List<Taxonomy> taxonomies = modelLayerFactory.getTaxonomiesManager().getEnabledTaxonomies(collection);
			for (ModificationImpact impact : new ModificationImpactCalculator(schemas.getTypes(), taxonomies, searchServices)
					.findTransactionImpact(transaction, true)) {
				impacts.add(new BatchProcessPossibleImpact(impact.getPotentialImpactsCount(), impact.getImpactedSchemaType()));
			}

			recordModificationses.add(new BatchProcessRecordModifications(originalRecord.getId(), originalRecord.getTitle(),
					impacts, recordFieldModifications));
		}

		return new BatchProcessResults(recordModificationses);
	}

	private String convertToString(Metadata metadata, Object value) {
		try {
			if (value == null) {
				return null;

			} else if (metadata.isMultivalue()) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("{");
				List<Object> list = (List<Object>) value;

				for (Object item : list) {
					if (stringBuilder.length() > 1) {
						stringBuilder.append(", ");
					}
					stringBuilder.append(convertScalarToString(metadata, item));
				}

				stringBuilder.append("}");

				return stringBuilder.toString();
			} else {
				return convertScalarToString(metadata, value);
			}
		} catch (Exception e) {
			LOGGER.warn("Cannot format unsupported value '" + value + "'", e);
			return "?";
		}
	}

	private String convertScalarToString(Metadata metadata, Object value) {
		if (value == null) {
			return null;
		}
		switch (metadata.getType()) {

		case DATE:
			return DateFormatUtils.format((LocalDate) value);

		case DATE_TIME:
			return DateFormatUtils.format((LocalDateTime) value);

		case STRING:
		case TEXT:
			return value.toString();

		case INTEGER:
		case NUMBER:
			return value.toString();

		case BOOLEAN:
			return $(value.toString(), locale);

		case REFERENCE:
			Record record = recordServices.getDocumentById(value.toString());
			return record.getIdTitle();

		case CONTENT:
			return ((Content) value).getCurrentVersion().getFilename();

		case STRUCTURE:
			return value.toString();

		case ENUM:
			return $(metadata.getEnumClass().getSimpleName() + "." + ((EnumWithSmallCode) value).getCode(), locale);
		}

		throw new ImpossibleRuntimeException("Unsupported type : " + metadata.getType());
	}

	private Transaction prepareTransaction(BatchProcessRequest request) {
		Transaction transaction = new Transaction();
		MetadataSchemaTypes types = schemas.getTypes();
		for (String id : request.getIds()) {
			Record record = recordServices.getDocumentById(id);
			transaction.add(record);
			MetadataSchema currentRecordSchema = types.getSchema(record.getSchemaCode());

			for (Map.Entry<String, Object> entry : request.getModifiedMetadatas().entrySet()) {
				String localMetadataCode = new SchemaUtils().getLocalCodeFromMetadataCode(entry.getKey());
				String metadataCode = currentRecordSchema.getCode() + "_" + localMetadataCode;
				if (currentRecordSchema.hasMetadataWithCode(metadataCode)) {
					Metadata metadata = currentRecordSchema.get(metadataCode);

					if (types.isRecordTypeMetadata(metadata)) {
						record.set(metadata, entry.getValue());
						changeSchemaTypeAccordingToTypeLinkedSchema(record, types, new RecordProvider(recordServices), metadata);
					}
				}
			}

			currentRecordSchema = types.getSchema(record.getSchemaCode());
			for (Map.Entry<String, Object> entry : request.getModifiedMetadatas().entrySet()) {
				String localMetadataCode = new SchemaUtils().getLocalCodeFromMetadataCode(entry.getKey());

				String metadataCode = currentRecordSchema.getCode() + "_" + localMetadataCode;
				if (currentRecordSchema.hasMetadataWithCode(metadataCode)) {
					Metadata metadata = currentRecordSchema.get(currentRecordSchema.getCode() + "_" + localMetadataCode);
					record.set(metadata, entry.getValue());
				}
			}

		}

		for (Record record : transaction.getModifiedRecords()) {
			recordServices.recalculate(record);
		}

		return transaction;
	}

}