package com.constellio.app.modules.rm.migrations;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;

public class RMMigrationTo6_1 implements MigrationScript {
	@Override
	public String getVersion() {
		return "6.1";
	}

	@Override
	public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory)
			throws Exception {
		new SchemaAlterationsFor6_1(collection, migrationResourcesProvider, appLayerFactory).migrate();
	}

	public static class SchemaAlterationsFor6_1 extends MetadataSchemasAlterationHelper {
		protected SchemaAlterationsFor6_1(String collection, MigrationResourcesProvider provider, AppLayerFactory factory) {
			super(collection, provider, factory);
		}

		@Override
		protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {
			updateDocumentSchema(typesBuilder.getSchemaType(Document.SCHEMA_TYPE).getDefaultSchema());
		}

		private void updateDocumentSchema(MetadataSchemaBuilder document) {
			document.createUndeletable(Document.PUBLISHED).setType(MetadataValueType.BOOLEAN).setDefaultValue(false);
		}
	}
}
