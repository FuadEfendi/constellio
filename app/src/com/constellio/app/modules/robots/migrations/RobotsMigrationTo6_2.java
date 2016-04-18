package com.constellio.app.modules.robots.migrations;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.robots.model.wrappers.Robot;
import com.constellio.app.modules.robots.model.wrappers.RobotLog;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypeBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;

public class RobotsMigrationTo6_2 implements MigrationScript {
	@Override
	public String getVersion() {
		return "6.2";
	}

	@Override
	public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory)
			throws Exception {
		new SchemasAlterationsFor6_2(collection, migrationResourcesProvider, appLayerFactory).migrate();
	}

	static class SchemasAlterationsFor6_2 extends MetadataSchemasAlterationHelper {
		protected SchemasAlterationsFor6_2(String collection, MigrationResourcesProvider provider, AppLayerFactory factory) {
			super(collection, provider, factory);
		}

		@Override
		protected void migrate(MetadataSchemaTypesBuilder builder) {
			changeRobotLogSchemaType(builder, builder.getSchemaType(Robot.SCHEMA_TYPE));
		}

		private void changeRobotLogSchemaType(MetadataSchemaTypesBuilder types, MetadataSchemaTypeBuilder robot) {
			MetadataSchemaTypeBuilder robotLog = types.getOrCreateNewSchemaType(RobotLog.SCHEMA_TYPE);
			MetadataSchemaBuilder schema = robotLog.getDefaultSchema();

			schema.createUndeletable(RobotLog.COUNT).setType(MetadataValueType.NUMBER);
		}
	}
}
