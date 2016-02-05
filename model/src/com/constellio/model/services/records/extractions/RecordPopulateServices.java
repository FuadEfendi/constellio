package com.constellio.model.services.records.extractions;

import static com.constellio.model.entities.schemas.MetadataValueType.CONTENT;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.model.entities.enums.MetadataPopulatePriority;
import com.constellio.model.entities.enums.TitleMetadataPopulatePriority;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.ParsedContent;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.RegexConfig;
import com.constellio.model.entities.schemas.RegexConfig.RegexConfigType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.extensions.events.records.RecordSetCategoryEvent;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.contents.ContentManager;
import com.constellio.model.services.contents.ContentManagerRuntimeException.ContentManagerRuntimeException_NoSuchContent;
import com.constellio.model.services.extensions.ModelLayerExtensions;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.schemas.MetadataList;
import com.constellio.model.services.schemas.MetadataSchemasManager;

public class RecordPopulateServices {

	public static boolean LOG_CONTENT_MISSING = true;
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordPopulateServices.class);

	ContentManager contentManager;
	MetadataSchemasManager schemasManager;
	SystemConfigurationsManager systemConfigurationsManager;
	ModelLayerExtensions modelLayerExtensions;
	ConstellioEIMConfigs eimConfigs;

	public RecordPopulateServices(MetadataSchemasManager schemasManager, ContentManager contentManager,
			SystemConfigurationsManager systemConfigurationsManager, ModelLayerExtensions modelLayerExtensions) {
		this.schemasManager = schemasManager;
		this.contentManager = contentManager;
		this.systemConfigurationsManager = systemConfigurationsManager;
		this.modelLayerExtensions = modelLayerExtensions;
		this.eimConfigs = new ConstellioEIMConfigs(systemConfigurationsManager);
	}

	public void populate(Record record) {

		ParsedContentProvider parsedContentProvider = new ParsedContentProvider(contentManager);
		MetadataSchema schema = schemasManager.getSchemaTypes(record.getCollection()).getSchema(record.getSchemaCode());

		List<Metadata> contentMetadatas = schema.getMetadatas().onlyWithType(CONTENT).sortedUsing(new ContentsComparator());

		if (!record.isSaved()) {
			String category = getCategory(parsedContentProvider, contentMetadatas, record);
			setCategoryToRecord(record, category);

		}
		schema = schemasManager.getSchemaTypes(record.getCollection()).getSchema(record.getSchemaCode());
		MetadataList populatedMetadatas = schema.getMetadatas().onlyPopulated();
		Record originalRecord = record.isSaved() ? record.getCopyOfOriginalRecord() : null;

		if (!populatedMetadatas.containsMetadataWithLocalCode(Schemas.TITLE_CODE)) {
			populatedMetadatas = new MetadataList(populatedMetadatas);
			populatedMetadatas.add(schema.get(Schemas.TITLE_CODE));
		}

		MetadataPopulatePriority priority = eimConfigs.getMetadataPopulatePriority();
		TitleMetadataPopulatePriority titlePriority = eimConfigs.getTitleMetadataPopulatePriority();
		for (Metadata metadata : populatedMetadatas) {
			RecordMetadataPopulator populator = new RecordMetadataPopulator(parsedContentProvider, metadata, priority,
					titlePriority, schema);
			if (isRepopulatable(record, originalRecord, metadata, contentMetadatas, populator)) {
				Object currentPopulatedValue = populator.populate(record, contentMetadatas);

				if (currentPopulatedValue != null || !Schemas.TITLE_CODE.equals(metadata.getLocalCode())) {
					record.set(metadata, currentPopulatedValue);
				}
			}
		}
	}

	private String getCategory(ParsedContentProvider parsedContentProvider, List<Metadata> contentMetadatas, Record record) {
		for (Content content : getContents(record, contentMetadatas)) {
			ParsedContent parsedContent = parsedContentProvider.getParsedContentParsingIfNotYetDone(
					content.getCurrentVersion().getHash());
			String category = (String) parsedContent.getNormalizedProperty("category");
			if (category != null) {
				return category;
			}
		}
		return null;
	}

	private void setCategoryToRecord(Record record, String category) {
		RecordSetCategoryEvent event = new RecordSetCategoryEvent(record, category);
		modelLayerExtensions.forCollectionOf(record).callSetRecordCategory(event);
	}

	private boolean isRepopulatable(Record record, Record originalRecord, Metadata metadata, List<Metadata> contentMetadatas,
			RecordMetadataPopulator recordMetadataPopulator) {

		if (originalRecord == null) {
			if (metadata.isMultivalue()) {
				List<String> values = record.getList(metadata);
				return values.isEmpty() || isValueWritenBySystem(record, metadata, values, contentMetadatas);
			} else {
				String value = record.get(metadata);
				return record.get(metadata) == null || isValueWritenBySystem(record, metadata, value, contentMetadatas);
			}
		}

		Object previousValue = originalRecord.get(metadata);
		Object previousPopulatedValue = recordMetadataPopulator.populate(originalRecord, contentMetadatas);
		Object currentValue = record.get(metadata);

		previousValue = emptyListToNull(previousValue);
		previousPopulatedValue = emptyListToNull(previousPopulatedValue);
		currentValue = emptyListToNull(currentValue);

		if (previousValue == null) {
			//If there was no value, it's only populated if a value has not been set
			return currentValue == null;

		} else if (previousValue.equals(previousPopulatedValue)) {
			return previousValue.equals(currentValue);

		} else {
			return false;
		}

	}

	private boolean isValueWritenBySystem(Record record, Metadata metadata, String value, List<Metadata> contentMetadatas) {
		if (metadata.isSameLocalCode(Schemas.TITLE)) {
			for (Content content : getContents(record, contentMetadatas)) {
				if (content.getCurrentVersion().getFilename().equals(value)) {
					return true;
				}
			}
		}
		return value.equals(metadata.getDefaultValue());
	}

	private boolean isValueWritenBySystem(Record record, Metadata metadata, List<String> value, List<Metadata> contentMetadatas) {
		return value.equals(metadata.getDefaultValue());
	}

	private Object emptyListToNull(Object value) {
		if (value instanceof List) {
			List list = (List) value;
			return list.isEmpty() ? null : list;
		}
		return value;
	}

	private static class ParsedContentProvider {

		Map<String, ParsedContent> cache = new HashMap<>();

		ContentManager contentManager;

		private ParsedContentProvider(ContentManager contentManager) {
			this.contentManager = contentManager;
		}

		public ParsedContent getParsedContentParsingIfNotYetDone(String hash) {
			ParsedContent parsedContent = cache.get(hash);
			if (parsedContent == null) {
				parsedContent = contentManager.getParsedContentParsingIfNotYetDone(hash);
				cache.put(hash, parsedContent);
			}

			return parsedContent;
		}
	}

	private static class RecordMetadataPopulator {

		MetadataSchema schema;
		TitleMetadataPopulatePriority titlePriority;
		MetadataPopulatePriority priority;
		ParsedContentProvider parsedContentProvider;
		Metadata metadata;

		private RecordMetadataPopulator(ParsedContentProvider parsedContentProvider, Metadata metadata,
				MetadataPopulatePriority priority, TitleMetadataPopulatePriority titlePriority, MetadataSchema schema) {
			this.parsedContentProvider = parsedContentProvider;
			this.metadata = metadata;
			this.priority = priority;
			this.titlePriority = titlePriority;
			this.schema = schema;
		}

		private Object populate(Record record, List<Metadata> contentMetadatas) {

			if (metadata.isSameLocalCode(Schemas.TITLE)) {
				return populateTitleUsingPropertiesAndStyles(record, contentMetadatas);
			} else {

				Object value = null;
				for (String source : priority.getPrioritizedSources()) {
					if (value == null || value.equals(new ArrayList<>())) {
						switch (source) {
						case "styles":
							value = populateUsingStyles(record, contentMetadatas);
							break;

						case "properties":
							value = populateUsingProperties(record, contentMetadatas);
							break;

						case "regex":
							value = populateUsingRegex(record);
							break;
						}
					}
				}
				return value;
			}

		}

		private Object populateUsingRegex(Record record) {

			Object value = null;
			for (RegexConfig regexConfig : metadata.getPopulateConfigs().getRegexes()) {
				if (value == null) {
					value = populateUsingRegex(regexConfig, record);
				}
			}

			return value;
		}

		private Object populateUsingRegex(RegexConfig regexConfig, Record record) {
			Metadata inputMetadata = schema.getMetadata(regexConfig.getInputMetadata());

			List<String> populatedValues = new ArrayList<>();
			if (inputMetadata.isMultivalue()) {
				List<String> values = record.getList(inputMetadata);

				for (Object value : values) {
					String populatedValue = populateUsingRegex(regexConfig, value, inputMetadata);
					if (populatedValue != null) {
						populatedValues.add(populatedValue);
					}
				}

			} else {
				Object value = record.get(inputMetadata);
				String populatedValue = populateUsingRegex(regexConfig, value, inputMetadata);
				if (populatedValue != null) {
					populatedValues.add(populatedValue);
				}

			}

			return convert(populatedValues);
		}

		private String populateUsingRegex(RegexConfig regexConfig, String value) {
			Matcher matcher = regexConfig.getRegex().matcher(value);
			if (matcher.find()) {
				if (regexConfig.getRegexConfigType() == RegexConfigType.TRANSFORMATION) {
					String match = matcher.group();
					return regexConfig.getRegex().matcher(match).replaceAll(regexConfig.getValue());
				} else {
					return regexConfig.getValue();
				}
			}
			return null;
		}

		private String populateUsingRegex(RegexConfig regexConfig, Object value, Metadata inputMetadata) {
			if (value == null) {
				return null;
			}
			if (inputMetadata.getType().equals(MetadataValueType.CONTENT)) {
				Content content = (Content) value;
				try {
					ParsedContent parsedContent = parsedContentProvider.getParsedContentParsingIfNotYetDone(
							content.getCurrentVersion().getHash());
					return populateUsingRegex(regexConfig, parsedContent.getParsedContent());
				} catch (ContentManagerRuntimeException_NoSuchContent e) {
					if (LOG_CONTENT_MISSING) {
						LOGGER.error("No content " + content.getCurrentVersion().getHash());
					}
				}

			} else if (inputMetadata.getType().isStringOrText()) {
				return populateUsingRegex(regexConfig, (String) value);
			}

			return null;
		}

		private Object populateUsingStyles(Record record, List<Metadata> contentMetadatas) {
			for (Content content : getContents(record, contentMetadatas)) {
				List<String> contentPopulatedValue;
				try {
					ParsedContent parsedContent = parsedContentProvider.getParsedContentParsingIfNotYetDone(
							content.getCurrentVersion().getHash());
					contentPopulatedValue = populateUsingStyles(parsedContent);

					if (contentPopulatedValue != null) {
						return convert(contentPopulatedValue);
					}
				} catch (ContentManagerRuntimeException_NoSuchContent e) {
					if (LOG_CONTENT_MISSING) {
						LOGGER.error("No content " + content.getCurrentVersion().getHash());
					}
				}
			}
			return null;
		}

		private Object populateUsingProperties(Record record, List<Metadata> contentMetadatas) {
			for (Content content : getContents(record, contentMetadatas)) {
				List<String> contentPopulatedValue;
				try {
					ParsedContent parsedContent = parsedContentProvider.getParsedContentParsingIfNotYetDone(
							content.getCurrentVersion().getHash());
					contentPopulatedValue = populateUsingProperties(parsedContent);

					if (contentPopulatedValue != null) {
						return convert(contentPopulatedValue);
					}
				} catch (ContentManagerRuntimeException_NoSuchContent e) {
					if (LOG_CONTENT_MISSING) {
						LOGGER.error("No content " + content.getCurrentVersion().getHash());
					}
				}
			}
			return null;
		}

		private Object populateTitleUsingPropertiesAndStyles(Record record, List<Metadata> contentMetadatas) {
			for (Content content : getContents(record, contentMetadatas)) {
				List<String> contentPopulatedValue;
				try {
					contentPopulatedValue = populateTitleUsingPropertiesAndStyles(content);

					if (contentPopulatedValue != null) {
						return convert(contentPopulatedValue);
					}
				} catch (ContentManagerRuntimeException_NoSuchContent e) {
					if (LOG_CONTENT_MISSING) {
						LOGGER.error("No content " + content.getCurrentVersion().getHash());
					}
				}
			}
			return null;
		}

		private Object convert(List<String> contentPopulatedValues) {
			if (contentPopulatedValues == null || contentPopulatedValues.equals(new ArrayList<String>())) {
				return null;

			} else if (metadata.isMultivalue()) {

				List<String> convertedValues = new ArrayList<>();
				for (String contentPopulatedValue : contentPopulatedValues) {
					convertedValues.addAll(asStringList(contentPopulatedValue));
				}
				return convertedValues;

			} else if (contentPopulatedValues.isEmpty()) {
				return null;

			} else {
				return contentPopulatedValues.get(0).trim();
			}
		}

		private List<String> populateTitleUsingPropertiesAndStyles(Content content) {
			ParsedContent parsedContent = parsedContentProvider.getParsedContentParsingIfNotYetDone(
					content.getCurrentVersion().getHash());

			List<String> propertiesValues = populateUsingProperties(parsedContent);
			List<String> stylesValues = populateUsingStyles(parsedContent);

			for (String source : titlePriority.getPrioritizedSouces()) {
				if (source.equals("styles")) {
					if (!stylesValues.isEmpty()) {
						return stylesValues;
					}
				} else if (source.equals("properties")) {
					if (!propertiesValues.isEmpty()) {
						return propertiesValues;
					}
				} else {
					break;
				}
			}

			String fileName = content.getCurrentVersion().getFilename();

			return fileName == null ? new ArrayList<String>() : asList(content.getCurrentVersion().getFilename());

		}

		private List<String> populateUsingStyles(ParsedContent parsedContent) {
			List<String> values = new ArrayList<>();

			for (String style : metadata.getPopulateConfigs().getStyles()) {
				List<String> styleValues = parsedContent.getStyles().get(style);
				if (styleValues != null) {
					values.addAll(styleValues);
				}
			}

			return values;
		}

		private List<String> populateUsingProperties(ParsedContent parsedContent) {
			List<String> values = new ArrayList<>();

			for (String property : metadata.getPopulateConfigs().getProperties()) {
				Object value = parsedContent.getNormalizedProperty(property);
				if (value instanceof String) {
					values.add((String) value);
				} else if (value instanceof List) {
					values.addAll((List) value);
				}
			}

			return values;
		}

	}

	private static List<String> asStringList(Object value) {
		if (value == null) {
			return new ArrayList<>();
		} else {
			List<String> values = new ArrayList<>();

			for (String aValue : value.toString().split(";")) {
				values.add(aValue.replace("'", "").trim());
			}

			return values;
		}
	}

	private static List<Content> getContents(Record record, List<Metadata> contentMetadatas) {
		List<Content> contents = new ArrayList<>();

		for (Metadata contentMetadata : contentMetadatas) {
			if (contentMetadata.isMultivalue()) {
				List<Content> metadataContents = record.getList(contentMetadata);
				contents.addAll(metadataContents);
			} else {
				Content content = record.get(contentMetadata);
				if (content != null) {
					contents.add(content);
				}

			}
		}

		return contents;
	}

	public static class ContentsComparator implements Comparator<Metadata> {
		@Override
		public int compare(Metadata m1, Metadata m2) {
			if (m1.isDefaultRequirement() && !m2.isDefaultRequirement()) {
				return -1;

			} else if (!m1.isDefaultRequirement() && m2.isDefaultRequirement()) {
				return 1;

			} else if (m1.isMultivalue() && !m2.isMultivalue()) {
				return 1;

			} else if (!m1.isMultivalue() && m2.isMultivalue()) {
				return -1;

			} else {
				String code1 = m1.getLocalCode();
				String code2 = m2.getLocalCode();
				return code1.compareTo(code2);
			}

		}
	}
}