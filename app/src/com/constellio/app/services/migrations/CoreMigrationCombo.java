package com.constellio.app.services.migrations;

import static com.constellio.app.ui.i18n.i18n.$;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.entities.modules.ComboMigrationScript;
import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_0_1;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_0_4;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_0_5;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_0_6_6;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_0_7;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_0;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_1_3;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_2;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_3;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_4;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_6;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_1_7;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_5_2;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_6_1;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_6_3;
import com.constellio.app.services.migrations.scripts.CoreMigrationTo_6_4;
import com.constellio.app.services.schemasDisplay.SchemaTypesDisplayTransactionBuilder;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.search.entities.SearchBoost;

public class CoreMigrationCombo implements ComboMigrationScript {
	@Override
	public List<MigrationScript> getVersions() {
		List<MigrationScript> scripts = new ArrayList<>();
		scripts.add(new CoreMigrationTo_5_0_1());
		scripts.add(new CoreMigrationTo_5_0_4());
		scripts.add(new CoreMigrationTo_5_0_5());
		scripts.add(new CoreMigrationTo_5_0_6_6());
		scripts.add(new CoreMigrationTo_5_0_7());
		scripts.add(new CoreMigrationTo_5_1_0());
		scripts.add(new CoreMigrationTo_5_1_1_3());
		scripts.add(new CoreMigrationTo_5_1_2());
		scripts.add(new CoreMigrationTo_5_1_3());
		scripts.add(new CoreMigrationTo_5_1_4());
		scripts.add(new CoreMigrationTo_5_1_6());
		scripts.add(new CoreMigrationTo_5_1_7());
		scripts.add(new CoreMigrationTo_5_2());
		scripts.add(new CoreMigrationTo_6_1());
		scripts.add(new CoreMigrationTo_6_3());
		scripts.add(new CoreMigrationTo_6_4());
		return scripts;
	}

	GeneratedCoreMigrationCombo generatedFastCoreMigration;

	@Override
	public String getVersion() {
		return getVersions().get(getVersions().size() - 1).getVersion();
	}

	@Override
	public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory)
			throws Exception {
		generatedFastCoreMigration = new GeneratedCoreMigrationCombo(collection, appLayerFactory,
				migrationResourcesProvider);

		new SchemaAlteration(collection, migrationResourcesProvider, appLayerFactory).migrate();
		CoreMigrationTo_5_1_3.initEncryption(collection, migrationResourcesProvider, appLayerFactory);
		generatedFastCoreMigration.applyGeneratedRoles();
		generatedFastCoreMigration.applySchemasDisplay(appLayerFactory.getMetadataSchemasDisplayManager());
		applySchemasDisplay2(collection, appLayerFactory.getMetadataSchemasDisplayManager());

		appLayerFactory.getModelLayerFactory().getSearchBoostManager().add(collection,
				new SearchBoost(SearchBoost.QUERY_TYPE, "title_s", $("title"), 20.0));

		RecordServices recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		MetadataSchemaTypes types = appLayerFactory.getModelLayerFactory().getMetadataSchemasManager().getSchemaTypes(collection);

		recordServices.execute(createRecordTransaction(collection, migrationResourcesProvider, appLayerFactory, types));
	}

	private void applySchemasDisplay2(String collection, SchemasDisplayManager manager) {
		SchemaTypesDisplayTransactionBuilder transaction = manager.newTransactionBuilderFor(collection);

		transaction.add(manager.getSchema(collection, "user_default").withDisplayMetadataCodes(asList(
				"user_default_username",
				"user_default_firstname",
				"user_default_lastname",
				"user_default_title",
				"user_default_email",
				"user_default_userroles",
				"user_default_groups",
				"user_default_jobTitle",
				"user_default_phone",
				"user_default_status",
				"user_default_createdOn",
				"user_default_modifiedOn",
				"user_default_allroles"
		)));

		transaction.in("savedSearch").addToDisplay("resultsViewMode").beforeMetadata("schemaFilter");

		transaction.in("savedSearch").addToForm("resultsViewMode").beforeMetadata("schemaFilter");

		manager.execute(transaction.build());
	}

	private Transaction createRecordTransaction(String collection, MigrationResourcesProvider migrationResourcesProvider,
			AppLayerFactory appLayerFactory, MetadataSchemaTypes types) {
		Transaction transaction = new Transaction();

		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(types.getCollection(), appLayerFactory.getModelLayerFactory());

		transaction.add(rm.newFacetField().setOrder(0).setTitle(migrationResourcesProvider.get("init.facet.type"))
				.setActive(true)
				.setOpenByDefault(true)
				.setFieldDataStoreCode(Schemas.SCHEMA.getDataStoreCode()));

		transaction.add(rm.newFacetQuery().setOrder(1)
				.setTitle(migrationResourcesProvider.get("init.facet.createModification"))
				.setActive(true)
				.setOpenByDefault(true)
				.withQuery("modifiedOn_dt:[NOW-1MONTH TO NOW]", "Modifiés les 30 derniers jours")
				.withQuery("modifiedOn_dt:[NOW-7DAY TO NOW]", "Modifiés les 7 derniers jours")
				.withQuery("createdOn_dt:[NOW-1MONTH TO NOW]", "Créés les 30 derniers jours")
				.withQuery("createdOn_dt:[NOW-7DAY TO NOW]", "Créés les 7 derniers jours"));

		return transaction;
	}

	class SchemaAlteration extends MetadataSchemasAlterationHelper {

		protected SchemaAlteration(String collection,
				MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory) {
			super(collection, migrationResourcesProvider, appLayerFactory);
		}

		@Override
		protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {

			generatedFastCoreMigration.applyGeneratedSchemaAlteration(typesBuilder);

		}

	}
}
