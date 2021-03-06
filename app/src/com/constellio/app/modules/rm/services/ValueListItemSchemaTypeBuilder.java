package com.constellio.app.modules.rm.services;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.constellio.app.modules.rm.wrappers.structures.CommentFactory;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.wrappers.HierarchicalValueListItem;
import com.constellio.model.entities.records.wrappers.ValueListItem;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.schemas.builders.MetadataBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypeBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;

public class ValueListItemSchemaTypeBuilder {

	public enum ValueListItemSchemaTypeCodeMode {REQUIRED_AND_UNIQUE, FACULTATIVE, DISABLED}

	MetadataSchemaTypesBuilder metadataSchemaTypesBuilder;

	public ValueListItemSchemaTypeBuilder(
			MetadataSchemaTypesBuilder metadataSchemaTypesBuilder) {
		this.metadataSchemaTypesBuilder = metadataSchemaTypesBuilder;
	}

	public MetadataSchemaTypeBuilder createValueListItemSchema(String code, Map<Language, String> labels,
			ValueListItemSchemaTypeCodeMode codeMode) {
		MetadataSchemaTypeBuilder typeBuilder = metadataSchemaTypesBuilder.createNewSchemaType(code);
		typeBuilder.setLabels(labels);
		typeBuilder.setSecurity(false);
		MetadataSchemaBuilder defaultSchemaBuilder = typeBuilder.getDefaultSchema();

		defaultSchemaBuilder.getMetadata(Schemas.TITLE_CODE).setUniqueValue(true).setDefaultRequirement(true);

		MetadataBuilder codeMetadata = defaultSchemaBuilder.create(ValueListItem.CODE).setType(
				MetadataValueType.STRING).setSearchable(true).setUndeletable(true).setSchemaAutocomplete(true);

		List<Language> languages = metadataSchemaTypesBuilder.getLanguages();
		for (Language language : languages) {
			codeMetadata.addLabel(language, $("init.valuelist.default.code"));
		}

		codeMetadata.setSchemaAutocomplete(true);
		if (codeMode == ValueListItemSchemaTypeCodeMode.REQUIRED_AND_UNIQUE) {
			codeMetadata.setUniqueValue(true).setDefaultRequirement(true);

		} else if (codeMode == ValueListItemSchemaTypeCodeMode.DISABLED) {
			codeMetadata.setEnabled(false);
		}

		MetadataBuilder descriptionMetadata = defaultSchemaBuilder.create(ValueListItem.DESCRIPTION)
				.setType(MetadataValueType.TEXT).setSearchable(true)
				.setUndeletable(true);
		for (Language language : languages) {
			descriptionMetadata.addLabel(language, $("init.valuelist.default.description"));
		}

		defaultSchemaBuilder.create(ValueListItem.COMMENTS).setMultivalue(true)
				.setType(MetadataValueType.ENUM).defineStructureFactory(CommentFactory.class);

		MetadataBuilder titleMetadata = defaultSchemaBuilder.getMetadata(Schemas.TITLE.getLocalCode()).setSearchable(true);
		for (Language language : languages) {
			titleMetadata.addLabel(language, $("init.valuelist.default.title"));
		}
		return typeBuilder;
	}

	public MetadataSchemaTypeBuilder createValueListItemSchema(String code, String label,
			ValueListItemSchemaTypeCodeMode codeMode) {

		Map<Language, String> labels = new HashMap<>();
		for (Language language : metadataSchemaTypesBuilder.getLanguages()) {
			if (StringUtils.isBlank(label)) {
				labels.put(language, $("init." + code));
			} else {
				labels.put(language, label);
			}
		}
		return createValueListItemSchema(code, labels, codeMode);
	}

	public MetadataSchemaTypeBuilder createHierarchicalValueListItemSchema(String code, String label,
			ValueListItemSchemaTypeCodeMode codeMode) {
		List<Language> languages = metadataSchemaTypesBuilder.getLanguages();
		MetadataSchemaTypeBuilder typeBuilder = createValueListItemSchema(code, label, codeMode);
		MetadataSchemaBuilder defaultSchemaBuilder = typeBuilder.getDefaultSchema();
		MetadataBuilder parentMetadata = defaultSchemaBuilder.create(HierarchicalValueListItem.PARENT)
				.defineChildOfRelationshipToType(typeBuilder)
				.setUndeletable(true);

		for (Language language : languages) {
			parentMetadata.addLabel(language, $("init.valuelist.default.parent"));
		}

		return typeBuilder;
	}

}
