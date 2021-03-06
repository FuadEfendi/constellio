package com.constellio.app.modules.rm.ui.builders;

import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.rm.ui.entities.RetentionRuleVO;
import com.constellio.app.modules.rm.wrappers.Category;
import com.constellio.app.modules.rm.wrappers.RetentionRule;
import com.constellio.app.modules.rm.wrappers.UniformSubdivision;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.MetadataValueVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.AllowedReferences;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;

public class RetentionRuleToVOBuilder extends RecordToVOBuilder {
	private final SchemasDisplayManager schemasDisplayManager;
	private final SearchServices searchServices;
	private final MetadataSchema categories;
	private final MetadataSchema subdivisions;
	private SessionContext sessionContext;

	public RetentionRuleToVOBuilder(AppLayerFactory appLayerFactory, MetadataSchema categories, MetadataSchema subdivisions) {
		this.categories = categories;
		this.subdivisions = subdivisions;
		searchServices = appLayerFactory.getModelLayerFactory().newSearchServices();
		schemasDisplayManager = appLayerFactory.getMetadataSchemasDisplayManager();
	}

	public RetentionRuleToVOBuilder(SessionContext sessionContext, AppLayerFactory appLayerFactory, MetadataSchema categories,
			MetadataSchema subdivisions) {
		this.categories = categories;
		this.subdivisions = subdivisions;
		this.sessionContext = sessionContext;
		searchServices = appLayerFactory.getModelLayerFactory().newSearchServices();
		schemasDisplayManager = appLayerFactory.getMetadataSchemasDisplayManager();
	}

	@Override
	public RetentionRuleVO build(Record record, VIEW_MODE viewMode, SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		return (RetentionRuleVO) super.build(record, viewMode, sessionContext);
	}

	@Override
	protected RetentionRuleVO newRecordVO(String id, List<MetadataValueVO> metadataValueVOs, VIEW_MODE viewMode) {
		MetadataSchemaVO schema = metadataValueVOs.get(0).getMetadata().getSchema();

		MetadataValueVO categoriesMetadataValueVO = new MetadataValueVO(getCategoriesMetadata(schema), getCategories(id));
		MetadataValueVO uniformSubdivisionsMetadataValueVO = new MetadataValueVO(getUniformSubdivisionsMetadata(schema),
				getUniformSubdivisions(id));

		int indexOfAdministrativeUnits = getIndexOfMetadataCode(RetentionRule.ADMINISTRATIVE_UNITS, metadataValueVOs);
		if (indexOfAdministrativeUnits != -1) {
			metadataValueVOs.add(indexOfAdministrativeUnits, categoriesMetadataValueVO);
			metadataValueVOs.add(indexOfAdministrativeUnits + 1, uniformSubdivisionsMetadataValueVO);
		} else {
			metadataValueVOs.add(categoriesMetadataValueVO);
			metadataValueVOs.add(uniformSubdivisionsMetadataValueVO);
		}
		return new RetentionRuleVO(id, metadataValueVOs, viewMode);
	}

	private List<String> getCategories(String id) {
		LogicalSearchCondition condition = from(categories).where(categories.getMetadata(Category.RETENTION_RULES)).isEqualTo(id);
		return searchServices.searchRecordIds(new LogicalSearchQuery(condition));
	}

	private MetadataVO getCategoriesMetadata(MetadataSchemaVO schema) {
		return getSynteticMetadata(schema, RetentionRuleVO.CATEGORIES, Category.SCHEMA_TYPE, Category.DEFAULT_SCHEMA);
	}

	private List<String> getUniformSubdivisions(String id) {
		LogicalSearchCondition condition = from(subdivisions)
				.where(subdivisions.getMetadata(UniformSubdivision.RETENTION_RULE)).isEqualTo(id);
		return searchServices.searchRecordIds(new LogicalSearchQuery(condition));
	}

	private MetadataVO getUniformSubdivisionsMetadata(MetadataSchemaVO schema) {
		return getSynteticMetadata(schema, RetentionRuleVO.UNIFORM_SUBDIVISIONS, UniformSubdivision.SCHEMA_TYPE,
				UniformSubdivision.DEFAULT_SCHEMA);
	}

	private MetadataVO getSynteticMetadata(MetadataSchemaVO schema, String label, String referencedSchemaType,
			String referencedSchema) {
		Map<Locale, String> labels = new HashMap<>();
		labels.put(sessionContext.getCurrentLocale(), $("RetentionRules." + label));

		String[] taxoCodes = new String[0];

		Set<String> references = new HashSet<>();
		references.add(referencedSchema);

		String typeCode = new SchemaUtils().getSchemaTypeCode(schema.getCode());

		Map<String, Map<Language, String>> groups = schemasDisplayManager.getType(schema.getCollection(), typeCode)
				.getMetadataGroup();
		Language language = Language.withCode(sessionContext.getCurrentLocale().getLanguage());
		String groupLabel = groups.keySet().isEmpty() ? null : groups.entrySet().iterator().next().getValue().get(language);

		insertMetadataCodeBefore(label, RetentionRule.COPY_RETENTION_RULES, schema.getDisplayMetadataCodes());
		insertMetadataCodeBefore(label, RetentionRule.COPY_RETENTION_RULES, schema.getFormMetadataCodes());

		return new MetadataVO(label, MetadataValueType.REFERENCE, schema.getCollection(), schema, false, true, false,
				labels, null, taxoCodes, referencedSchemaType, MetadataInputType.LOOKUP,
				new AllowedReferences(referencedSchemaType, references), groupLabel, null, false);
	}

	private void insertMetadataCodeBefore(String codeToInsert, String codeToSearch, List<String> codes) {
		int index = codes.indexOf(RetentionRule.DEFAULT_SCHEMA + "_" + codeToSearch);
		codes.add(index, codeToInsert);
	}
}
