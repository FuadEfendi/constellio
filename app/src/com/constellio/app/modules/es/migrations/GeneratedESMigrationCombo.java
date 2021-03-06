package com.constellio.app.modules.es.migrations;

import static java.util.Arrays.asList;

import java.util.ArrayList;

import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.schemasDisplay.SchemaTypesDisplayConfig;
import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.es.connectors.smb.LastFetchedStatus;
import com.constellio.app.modules.es.model.connectors.AuthenticationScheme;
import com.constellio.app.modules.es.model.connectors.ConnectorDocumentStatus;
import com.constellio.app.modules.es.model.connectors.NextFetchCalculator;
import com.constellio.app.modules.es.model.connectors.http.enums.FetchFrequency;
import com.constellio.app.modules.es.model.connectors.ldap.enums.DirectoryType;
import com.constellio.app.modules.es.model.connectors.structures.TraversalScheduleFactory;
import com.constellio.app.modules.es.services.mapping.ConnectorFieldFactory;
import com.constellio.app.modules.es.services.mapping.ConnectorFieldValidator;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemaTypesDisplayTransactionBuilder;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.structures.MapStringListStringStructureFactory;
import com.constellio.model.services.schemas.builders.MetadataBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypeBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.schemas.calculators.AllAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.InheritedAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.ParentPathCalculator;
import com.constellio.model.services.schemas.calculators.PathCalculator;
import com.constellio.model.services.schemas.calculators.PathPartsCalculator;
import com.constellio.model.services.schemas.calculators.PrincipalPathCalculator;
import com.constellio.model.services.schemas.calculators.TokensCalculator2;
import com.constellio.model.services.schemas.validators.ManualTokenValidator;
import com.constellio.model.services.security.roles.RolesManager;

public final class GeneratedESMigrationCombo {
	String collection;

	AppLayerFactory appLayerFactory;

	MigrationResourcesProvider resourcesProvider;

	GeneratedESMigrationCombo(String collection, AppLayerFactory appLayerFactory, MigrationResourcesProvider resourcesProvider) {
		this.collection = collection;
		this.appLayerFactory = appLayerFactory;
		this.resourcesProvider = resourcesProvider;
	}

	public void applyGeneratedSchemaAlteration(MetadataSchemaTypesBuilder typesBuilder) {
		MetadataSchemaTypeBuilder collectionSchemaType = typesBuilder.getSchemaType("collection");
		MetadataSchemaBuilder collectionSchema = collectionSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder groupSchemaType = typesBuilder.getSchemaType("group");
		MetadataSchemaBuilder groupSchema = groupSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userSchemaType = typesBuilder.getSchemaType("user");
		MetadataSchemaBuilder userSchema = userSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder emailToSendSchemaType = typesBuilder.getSchemaType("emailToSend");
		MetadataSchemaBuilder emailToSendSchema = emailToSendSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder eventSchemaType = typesBuilder.getSchemaType("event");
		MetadataSchemaBuilder eventSchema = eventSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder facetSchemaType = typesBuilder.getSchemaType("facet");
		MetadataSchemaBuilder facet_fieldSchema = facetSchemaType.getCustomSchema("field");
		MetadataSchemaBuilder facet_querySchema = facetSchemaType.getCustomSchema("query");
		MetadataSchemaBuilder facetSchema = facetSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder reportSchemaType = typesBuilder.getSchemaType("report");
		MetadataSchemaBuilder reportSchema = reportSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder savedSearchSchemaType = typesBuilder.getSchemaType("savedSearch");
		MetadataSchemaBuilder savedSearchSchema = savedSearchSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder taskSchemaType = typesBuilder.getSchemaType("task");
		MetadataSchemaBuilder task_approvalSchema = taskSchemaType.getCustomSchema("approval");
		MetadataSchemaBuilder taskSchema = taskSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userDocumentSchemaType = typesBuilder.getSchemaType("userDocument");
		MetadataSchemaBuilder userDocumentSchema = userDocumentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder connectorHttpDocumentSchemaType = typesBuilder.createNewSchemaType("connectorHttpDocument")
				.setInTransactionLog(false);
		MetadataSchemaBuilder connectorHttpDocumentSchema = connectorHttpDocumentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder connectorInstanceSchemaType = typesBuilder.createNewSchemaType("connectorInstance");
		MetadataSchemaBuilder connectorInstance_httpSchema = connectorInstanceSchemaType.createCustomSchema("http");
		MetadataSchemaBuilder connectorInstance_ldapSchema = connectorInstanceSchemaType.createCustomSchema("ldap");
		MetadataSchemaBuilder connectorInstance_smbSchema = connectorInstanceSchemaType.createCustomSchema("smb");
		MetadataSchemaBuilder connectorInstanceSchema = connectorInstanceSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder connectorLdapUserDocumentSchemaType = typesBuilder
				.createNewSchemaType("connectorLdapUserDocument").setInTransactionLog(false);
		MetadataSchemaBuilder connectorLdapUserDocumentSchema = connectorLdapUserDocumentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder connectorSmbDocumentSchemaType = typesBuilder.createNewSchemaType("connectorSmbDocument")
				.setInTransactionLog(false);
		MetadataSchemaBuilder connectorSmbDocumentSchema = connectorSmbDocumentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder connectorSmbFolderSchemaType = typesBuilder.createNewSchemaType("connectorSmbFolder")
				.setInTransactionLog(false);
		MetadataSchemaBuilder connectorSmbFolderSchema = connectorSmbFolderSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder connectorTypeSchemaType = typesBuilder.createNewSchemaType("connectorType");
		MetadataSchemaBuilder connectorTypeSchema = connectorTypeSchemaType.getDefaultSchema();
		MetadataBuilder connectorHttpDocument_allauthorizations = connectorHttpDocumentSchema.get("allauthorizations");
		connectorHttpDocument_allauthorizations.setMultivalue(true);
		connectorHttpDocument_allauthorizations.setSystemReserved(true);
		connectorHttpDocument_allauthorizations.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_authorizations = connectorHttpDocumentSchema.get("authorizations");
		connectorHttpDocument_authorizations.setMultivalue(true);
		connectorHttpDocument_authorizations.setSystemReserved(true);
		connectorHttpDocument_authorizations.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_charset = connectorHttpDocumentSchema.create("charset")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_charset.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_connector = connectorHttpDocumentSchema.create("connector")
				.setType(MetadataValueType.REFERENCE);
		connectorHttpDocument_connector.setDefaultRequirement(true);
		connectorHttpDocument_connector.setUndeletable(true);
		connectorHttpDocument_connector.defineReferencesTo(asList(connectorInstance_httpSchema));
		MetadataBuilder connectorHttpDocument_connectorType = connectorHttpDocumentSchema.create("connectorType")
				.setType(MetadataValueType.REFERENCE);
		connectorHttpDocument_connectorType.setDefaultRequirement(true);
		connectorHttpDocument_connectorType.setUndeletable(true);
		connectorHttpDocument_connectorType.defineReferencesTo(asList(connectorTypeSchema));
		MetadataBuilder connectorHttpDocument_contentType = connectorHttpDocumentSchema.create("contentType")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_contentType.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_copyOf = connectorHttpDocumentSchema.create("copyOf")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_copyOf.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_createdBy = connectorHttpDocumentSchema.get("createdBy");
		connectorHttpDocument_createdBy.setSystemReserved(true);
		connectorHttpDocument_createdBy.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_createdOn = connectorHttpDocumentSchema.get("createdOn");
		connectorHttpDocument_createdOn.setSystemReserved(true);
		connectorHttpDocument_createdOn.setUndeletable(true);
		connectorHttpDocument_createdOn.setSortable(true);
		MetadataBuilder connectorHttpDocument_deleted = connectorHttpDocumentSchema.get("deleted");
		connectorHttpDocument_deleted.setSystemReserved(true);
		connectorHttpDocument_deleted.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_denyTokens = connectorHttpDocumentSchema.get("denyTokens");
		connectorHttpDocument_denyTokens.setMultivalue(true);
		connectorHttpDocument_denyTokens.setSystemReserved(true);
		connectorHttpDocument_denyTokens.setUndeletable(true);
		connectorHttpDocument_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorHttpDocument_detachedauthorizations = connectorHttpDocumentSchema.get("detachedauthorizations");
		connectorHttpDocument_detachedauthorizations.setSystemReserved(true);
		connectorHttpDocument_detachedauthorizations.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_digest = connectorHttpDocumentSchema.create("digest")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_digest.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_downloadTime = connectorHttpDocumentSchema.create("downloadTime")
				.setType(MetadataValueType.NUMBER);
		connectorHttpDocument_downloadTime.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_errorCode = connectorHttpDocumentSchema.create("errorCode")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_errorCode.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_errorMessage = connectorHttpDocumentSchema.create("errorMessage")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_errorMessage.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_errorStackTrace = connectorHttpDocumentSchema.create("errorStackTrace")
				.setType(MetadataValueType.TEXT);
		connectorHttpDocument_errorStackTrace.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_errorsCount = connectorHttpDocumentSchema.create("errorsCount")
				.setType(MetadataValueType.NUMBER);
		connectorHttpDocument_errorsCount.setUndeletable(true);
		connectorHttpDocument_errorsCount.setDefaultValue(0);
		MetadataBuilder connectorHttpDocument_fetchDelay = connectorHttpDocumentSchema.create("fetchDelay")
				.setType(MetadataValueType.NUMBER);
		connectorHttpDocument_fetchDelay.setUndeletable(true);
		connectorHttpDocument_fetchDelay.setDefaultValue(10);
		MetadataBuilder connectorHttpDocument_fetched = connectorHttpDocumentSchema.create("fetched")
				.setType(MetadataValueType.BOOLEAN);
		connectorHttpDocument_fetched.setUndeletable(true);
		connectorHttpDocument_fetched.setDefaultValue(true);
		MetadataBuilder connectorHttpDocument_fetchedDateTime = connectorHttpDocumentSchema.create("fetchedDateTime")
				.setType(MetadataValueType.DATE_TIME);
		connectorHttpDocument_fetchedDateTime.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_followers = connectorHttpDocumentSchema.get("followers");
		connectorHttpDocument_followers.setMultivalue(true);
		connectorHttpDocument_followers.setSystemReserved(true);
		connectorHttpDocument_followers.setUndeletable(true);
		connectorHttpDocument_followers.setSearchable(true);
		MetadataBuilder connectorHttpDocument_frequency = connectorHttpDocumentSchema.create("frequency")
				.setType(MetadataValueType.ENUM);
		connectorHttpDocument_frequency.setUndeletable(true);
		connectorHttpDocument_frequency.defineAsEnum(FetchFrequency.class);
		MetadataBuilder connectorHttpDocument_id = connectorHttpDocumentSchema.get("id");
		connectorHttpDocument_id.setDefaultRequirement(true);
		connectorHttpDocument_id.setSystemReserved(true);
		connectorHttpDocument_id.setUndeletable(true);
		connectorHttpDocument_id.setSearchable(true);
		connectorHttpDocument_id.setSortable(true);
		connectorHttpDocument_id.setUniqueValue(true);
		connectorHttpDocument_id.setUnmodifiable(true);
		MetadataBuilder connectorHttpDocument_inheritedauthorizations = connectorHttpDocumentSchema
				.get("inheritedauthorizations");
		connectorHttpDocument_inheritedauthorizations.setMultivalue(true);
		connectorHttpDocument_inheritedauthorizations.setSystemReserved(true);
		connectorHttpDocument_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_inlinks = connectorHttpDocumentSchema.create("inlinks")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_inlinks.setMultivalue(true);
		connectorHttpDocument_inlinks.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_lastModified = connectorHttpDocumentSchema.create("lastModified")
				.setType(MetadataValueType.DATE_TIME);
		connectorHttpDocument_lastModified.setUndeletable(true);
		connectorHttpDocument_lastModified.setSearchable(true);
		MetadataBuilder connectorHttpDocument_legacyIdentifier = connectorHttpDocumentSchema.get("legacyIdentifier");
		connectorHttpDocument_legacyIdentifier.setDefaultRequirement(true);
		connectorHttpDocument_legacyIdentifier.setSystemReserved(true);
		connectorHttpDocument_legacyIdentifier.setUndeletable(true);
		connectorHttpDocument_legacyIdentifier.setSearchable(true);
		connectorHttpDocument_legacyIdentifier.setUniqueValue(true);
		connectorHttpDocument_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder connectorHttpDocument_level = connectorHttpDocumentSchema.create("level")
				.setType(MetadataValueType.NUMBER);
		connectorHttpDocument_level.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_manualTokens = connectorHttpDocumentSchema.get("manualTokens");
		connectorHttpDocument_manualTokens.setMultivalue(true);
		connectorHttpDocument_manualTokens.setSystemReserved(true);
		connectorHttpDocument_manualTokens.setUndeletable(true);
		connectorHttpDocument_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorHttpDocument_markedForPreviewConversion = connectorHttpDocumentSchema
				.get("markedForPreviewConversion");
		connectorHttpDocument_markedForPreviewConversion.setSystemReserved(true);
		connectorHttpDocument_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_mimetype = connectorHttpDocumentSchema.create("mimetype")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_mimetype.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_modifiedBy = connectorHttpDocumentSchema.get("modifiedBy");
		connectorHttpDocument_modifiedBy.setSystemReserved(true);
		connectorHttpDocument_modifiedBy.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_modifiedOn = connectorHttpDocumentSchema.get("modifiedOn");
		connectorHttpDocument_modifiedOn.setSystemReserved(true);
		connectorHttpDocument_modifiedOn.setUndeletable(true);
		connectorHttpDocument_modifiedOn.setSortable(true);
		MetadataBuilder connectorHttpDocument_neverFetch = connectorHttpDocumentSchema.create("neverFetch")
				.setType(MetadataValueType.BOOLEAN);
		connectorHttpDocument_neverFetch.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_nextFetch = connectorHttpDocumentSchema.create("nextFetch")
				.setType(MetadataValueType.DATE_TIME);
		connectorHttpDocument_nextFetch.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_onDemand = connectorHttpDocumentSchema.create("onDemand")
				.setType(MetadataValueType.BOOLEAN);
		connectorHttpDocument_onDemand.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_outlinks = connectorHttpDocumentSchema.create("outlinks")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_outlinks.setMultivalue(true);
		connectorHttpDocument_outlinks.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_parentpath = connectorHttpDocumentSchema.get("parentpath");
		connectorHttpDocument_parentpath.setMultivalue(true);
		connectorHttpDocument_parentpath.setSystemReserved(true);
		connectorHttpDocument_parentpath.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_parsedContent = connectorHttpDocumentSchema.create("parsedContent")
				.setType(MetadataValueType.TEXT);
		connectorHttpDocument_parsedContent.setUndeletable(true);
		connectorHttpDocument_parsedContent.setSearchable(true);
		MetadataBuilder connectorHttpDocument_path = connectorHttpDocumentSchema.get("path");
		connectorHttpDocument_path.setMultivalue(true);
		connectorHttpDocument_path.setSystemReserved(true);
		connectorHttpDocument_path.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_pathParts = connectorHttpDocumentSchema.get("pathParts");
		connectorHttpDocument_pathParts.setMultivalue(true);
		connectorHttpDocument_pathParts.setSystemReserved(true);
		connectorHttpDocument_pathParts.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_principalpath = connectorHttpDocumentSchema.get("principalpath");
		connectorHttpDocument_principalpath.setSystemReserved(true);
		connectorHttpDocument_principalpath.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_priority = connectorHttpDocumentSchema.create("priority")
				.setType(MetadataValueType.NUMBER);
		connectorHttpDocument_priority.setUndeletable(true);
		connectorHttpDocument_priority.setDefaultValue(0.5F);
		MetadataBuilder connectorHttpDocument_removedauthorizations = connectorHttpDocumentSchema.get("removedauthorizations");
		connectorHttpDocument_removedauthorizations.setMultivalue(true);
		connectorHttpDocument_removedauthorizations.setSystemReserved(true);
		connectorHttpDocument_removedauthorizations.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_schema = connectorHttpDocumentSchema.get("schema");
		connectorHttpDocument_schema.setDefaultRequirement(true);
		connectorHttpDocument_schema.setSystemReserved(true);
		connectorHttpDocument_schema.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_searchable = connectorHttpDocumentSchema.get("searchable");
		connectorHttpDocument_searchable.setSystemReserved(true);
		connectorHttpDocument_searchable.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_shareDenyTokens = connectorHttpDocumentSchema.get("shareDenyTokens");
		connectorHttpDocument_shareDenyTokens.setMultivalue(true);
		connectorHttpDocument_shareDenyTokens.setSystemReserved(true);
		connectorHttpDocument_shareDenyTokens.setUndeletable(true);
		connectorHttpDocument_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorHttpDocument_shareTokens = connectorHttpDocumentSchema.get("shareTokens");
		connectorHttpDocument_shareTokens.setMultivalue(true);
		connectorHttpDocument_shareTokens.setSystemReserved(true);
		connectorHttpDocument_shareTokens.setUndeletable(true);
		connectorHttpDocument_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorHttpDocument_status = connectorHttpDocumentSchema.create("status")
				.setType(MetadataValueType.ENUM);
		connectorHttpDocument_status.setUndeletable(true);
		connectorHttpDocument_status.defineAsEnum(ConnectorDocumentStatus.class);
		MetadataBuilder connectorHttpDocument_title = connectorHttpDocumentSchema.get("title");
		connectorHttpDocument_title.setUndeletable(true);
		connectorHttpDocument_title.setSchemaAutocomplete(true);
		connectorHttpDocument_title.setSearchable(true);
		MetadataBuilder connectorHttpDocument_tokens = connectorHttpDocumentSchema.get("tokens");
		connectorHttpDocument_tokens.setMultivalue(true);
		connectorHttpDocument_tokens.setSystemReserved(true);
		connectorHttpDocument_tokens.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_traversalCode = connectorHttpDocumentSchema.create("traversalCode")
				.setType(MetadataValueType.STRING);
		connectorHttpDocument_traversalCode.setDefaultRequirement(true);
		connectorHttpDocument_traversalCode.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_url = connectorHttpDocumentSchema.create("url").setType(MetadataValueType.STRING);
		connectorHttpDocument_url.setDefaultRequirement(true);
		connectorHttpDocument_url.setUndeletable(true);
		MetadataBuilder connectorHttpDocument_visibleInTrees = connectorHttpDocumentSchema.get("visibleInTrees");
		connectorHttpDocument_visibleInTrees.setSystemReserved(true);
		connectorHttpDocument_visibleInTrees.setUndeletable(true);
		MetadataBuilder connectorInstance_http_authenticationScheme = connectorInstance_httpSchema.create("authenticationScheme")
				.setType(MetadataValueType.ENUM);
		connectorInstance_http_authenticationScheme.setUndeletable(true);
		connectorInstance_http_authenticationScheme.defineAsEnum(AuthenticationScheme.class);
		MetadataBuilder connectorInstance_http_daysBeforeRefetching = connectorInstance_httpSchema.create("daysBeforeRefetching")
				.setType(MetadataValueType.NUMBER);
		connectorInstance_http_daysBeforeRefetching.setDefaultRequirement(true);
		connectorInstance_http_daysBeforeRefetching.setUndeletable(true);
		connectorInstance_http_daysBeforeRefetching.setDefaultValue(5);
		MetadataBuilder connectorInstance_http_documentsPerJobs = connectorInstance_httpSchema.create("documentsPerJobs")
				.setType(MetadataValueType.NUMBER);
		connectorInstance_http_documentsPerJobs.setDefaultRequirement(true);
		connectorInstance_http_documentsPerJobs.setUndeletable(true);
		connectorInstance_http_documentsPerJobs.setDefaultValue(10);
		MetadataBuilder connectorInstance_http_domain = connectorInstance_httpSchema.create("domain")
				.setType(MetadataValueType.STRING);
		connectorInstance_http_domain.setUndeletable(true);
		MetadataBuilder connectorInstance_http_excludePatterns = connectorInstance_httpSchema.create("excludePatterns")
				.setType(MetadataValueType.TEXT);
		connectorInstance_http_excludePatterns.setUndeletable(true);
		MetadataBuilder connectorInstance_http_includePatterns = connectorInstance_httpSchema.create("includePatterns")
				.setType(MetadataValueType.TEXT);
		connectorInstance_http_includePatterns.setUndeletable(true);
		MetadataBuilder connectorInstance_http_jobsInParallel = connectorInstance_httpSchema.create("jobsInParallel")
				.setType(MetadataValueType.NUMBER);
		connectorInstance_http_jobsInParallel.setDefaultRequirement(true);
		connectorInstance_http_jobsInParallel.setUndeletable(true);
		connectorInstance_http_jobsInParallel.setDefaultValue(1);
		MetadataBuilder connectorInstance_http_maxLevel = connectorInstance_httpSchema.create("maxLevel")
				.setType(MetadataValueType.NUMBER);
		connectorInstance_http_maxLevel.setDefaultRequirement(true);
		connectorInstance_http_maxLevel.setUndeletable(true);
		connectorInstance_http_maxLevel.setDefaultValue(5);
		MetadataBuilder connectorInstance_http_onDemands = connectorInstance_httpSchema.create("onDemands")
				.setType(MetadataValueType.TEXT);
		connectorInstance_http_onDemands.setUndeletable(true);
		MetadataBuilder connectorInstance_http_password = connectorInstance_httpSchema.create("password")
				.setType(MetadataValueType.STRING);
		connectorInstance_http_password.setUndeletable(true);
		connectorInstance_http_password.setEncrypted(true);
		MetadataBuilder connectorInstance_http_seeds = connectorInstance_httpSchema.create("seeds")
				.setType(MetadataValueType.TEXT);
		connectorInstance_http_seeds.setDefaultRequirement(true);
		connectorInstance_http_seeds.setUndeletable(true);
		MetadataBuilder connectorInstance_http_username = connectorInstance_httpSchema.create("username")
				.setType(MetadataValueType.STRING);
		connectorInstance_http_username.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_address = connectorInstance_ldapSchema.create("address")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_address.setMultivalue(true);
		connectorInstance_ldap_address.setUndeletable(true);
		connectorInstance_ldap_address
				.setDefaultValue(asList("physicalDeliveryOfficeName", "streetAddress", "l", "postalCode", "st", "co", "c"));
		MetadataBuilder connectorInstance_ldap_company = connectorInstance_ldapSchema.create("company")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_company.setUndeletable(true);
		connectorInstance_ldap_company.setDefaultValue("company");
		MetadataBuilder connectorInstance_ldap_connectionUsername = connectorInstance_ldapSchema.create("connectionUsername")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_connectionUsername.setDefaultRequirement(true);
		connectorInstance_ldap_connectionUsername.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_department = connectorInstance_ldapSchema.create("department")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_department.setUndeletable(true);
		connectorInstance_ldap_department.setDefaultValue("department");
		MetadataBuilder connectorInstance_ldap_directoryType = connectorInstance_ldapSchema.create("directoryType")
				.setType(MetadataValueType.ENUM);
		connectorInstance_ldap_directoryType.setDefaultRequirement(true);
		connectorInstance_ldap_directoryType.setSystemReserved(true);
		connectorInstance_ldap_directoryType.setUndeletable(true);
		connectorInstance_ldap_directoryType.setDefaultValue(DirectoryType.ACTIVE_DIRECTORY);
		connectorInstance_ldap_directoryType.defineAsEnum(DirectoryType.class);
		MetadataBuilder connectorInstance_ldap_displayName = connectorInstance_ldapSchema.create("displayName")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_displayName.setUndeletable(true);
		connectorInstance_ldap_displayName.setDefaultValue("cn");
		MetadataBuilder connectorInstance_ldap_dn = connectorInstance_ldapSchema.create("dn").setType(MetadataValueType.STRING);
		connectorInstance_ldap_dn.setDefaultRequirement(true);
		connectorInstance_ldap_dn.setUndeletable(true);
		connectorInstance_ldap_dn.setDefaultValue("distinguishedName");
		MetadataBuilder connectorInstance_ldap_documentsPerJob = connectorInstance_ldapSchema.create("documentsPerJob")
				.setType(MetadataValueType.NUMBER);
		connectorInstance_ldap_documentsPerJob.setUndeletable(true);
		connectorInstance_ldap_documentsPerJob.setDefaultValue(10);
		MetadataBuilder connectorInstance_ldap_email = connectorInstance_ldapSchema.create("email")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_email.setUndeletable(true);
		connectorInstance_ldap_email.setDefaultValue("mail");
		MetadataBuilder connectorInstance_ldap_excludeRegex = connectorInstance_ldapSchema.create("excludeRegex")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_excludeRegex.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_fetchComputers = connectorInstance_ldapSchema.create("fetchComputers")
				.setType(MetadataValueType.BOOLEAN);
		connectorInstance_ldap_fetchComputers.setSystemReserved(true);
		connectorInstance_ldap_fetchComputers.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_fetchGroups = connectorInstance_ldapSchema.create("fetchGroups")
				.setType(MetadataValueType.BOOLEAN);
		connectorInstance_ldap_fetchGroups.setSystemReserved(true);
		connectorInstance_ldap_fetchGroups.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_fetchUsers = connectorInstance_ldapSchema.create("fetchUsers")
				.setType(MetadataValueType.BOOLEAN);
		connectorInstance_ldap_fetchUsers.setSystemReserved(true);
		connectorInstance_ldap_fetchUsers.setUndeletable(true);
		connectorInstance_ldap_fetchUsers.setDefaultValue(true);
		MetadataBuilder connectorInstance_ldap_firstName = connectorInstance_ldapSchema.create("firstName")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_firstName.setUndeletable(true);
		connectorInstance_ldap_firstName.setDefaultValue("givenName");
		MetadataBuilder connectorInstance_ldap_followReferences = connectorInstance_ldapSchema.create("followReferences")
				.setType(MetadataValueType.BOOLEAN);
		connectorInstance_ldap_followReferences.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_includeRegex = connectorInstance_ldapSchema.create("includeRegex")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_includeRegex.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_jobTitle = connectorInstance_ldapSchema.create("jobTitle")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_jobTitle.setUndeletable(true);
		connectorInstance_ldap_jobTitle.setDefaultValue("title");
		MetadataBuilder connectorInstance_ldap_jobsInParallel = connectorInstance_ldapSchema.create("jobsInParallel")
				.setType(MetadataValueType.NUMBER);
		connectorInstance_ldap_jobsInParallel.setUndeletable(true);
		connectorInstance_ldap_jobsInParallel.setDefaultValue(1);
		MetadataBuilder connectorInstance_ldap_lastName = connectorInstance_ldapSchema.create("lastName")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_lastName.setUndeletable(true);
		connectorInstance_ldap_lastName.setDefaultValue("sn");
		MetadataBuilder connectorInstance_ldap_manager = connectorInstance_ldapSchema.create("manager")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_manager.setUndeletable(true);
		connectorInstance_ldap_manager.setDefaultValue("manager");
		MetadataBuilder connectorInstance_ldap_password = connectorInstance_ldapSchema.create("password")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_password.setDefaultRequirement(true);
		connectorInstance_ldap_password.setUndeletable(true);
		connectorInstance_ldap_password.setEncrypted(true);
		MetadataBuilder connectorInstance_ldap_telephone = connectorInstance_ldapSchema.create("telephone")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_telephone.setMultivalue(true);
		connectorInstance_ldap_telephone.setUndeletable(true);
		connectorInstance_ldap_telephone.setDefaultValue(asList("telephoneNumber", "mobile", "ipPhone"));
		MetadataBuilder connectorInstance_ldap_url = connectorInstance_ldapSchema.create("url").setType(MetadataValueType.STRING);
		connectorInstance_ldap_url.setMultivalue(true);
		connectorInstance_ldap_url.setDefaultRequirement(true);
		connectorInstance_ldap_url.setUndeletable(true);
		MetadataBuilder connectorInstance_ldap_username = connectorInstance_ldapSchema.create("username")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_username.setUndeletable(true);
		connectorInstance_ldap_username.setDefaultValue("sAMAccountName");
		MetadataBuilder connectorInstance_ldap_usersBaseContextList = connectorInstance_ldapSchema.create("usersBaseContextList")
				.setType(MetadataValueType.STRING);
		connectorInstance_ldap_usersBaseContextList.setMultivalue(true);
		connectorInstance_ldap_usersBaseContextList.setDefaultRequirement(true);
		connectorInstance_ldap_usersBaseContextList.setUndeletable(true);
		MetadataBuilder connectorInstance_smb_domain = connectorInstance_smbSchema.create("domain")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_domain.setUndeletable(true);
		MetadataBuilder connectorInstance_smb_exclusions = connectorInstance_smbSchema.create("exclusions")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_exclusions.setMultivalue(true);
		connectorInstance_smb_exclusions.setUndeletable(true);
		MetadataBuilder connectorInstance_smb_inclusions = connectorInstance_smbSchema.create("inclusions")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_inclusions.setMultivalue(true);
		connectorInstance_smb_inclusions.setUndeletable(true);
		MetadataBuilder connectorInstance_smb_password = connectorInstance_smbSchema.create("password")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_password.setUndeletable(true);
		connectorInstance_smb_password.setEncrypted(true);
		MetadataBuilder connectorInstance_smb_resumeUrl = connectorInstance_smbSchema.create("resumeUrl")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_resumeUrl.setUndeletable(true);
		MetadataBuilder connectorInstance_smb_skipShareAccessControl = connectorInstance_smbSchema
				.create("skipShareAccessControl").setType(MetadataValueType.BOOLEAN);
		connectorInstance_smb_skipShareAccessControl.setDefaultValue(false);
		MetadataBuilder connectorInstance_smb_smbSeeds = connectorInstance_smbSchema.create("smbSeeds")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_smbSeeds.setMultivalue(true);
		connectorInstance_smb_smbSeeds.setDefaultRequirement(true);
		connectorInstance_smb_smbSeeds.setUndeletable(true);
		MetadataBuilder connectorInstance_smb_username = connectorInstance_smbSchema.create("username")
				.setType(MetadataValueType.STRING);
		connectorInstance_smb_username.setDefaultRequirement(true);
		connectorInstance_smb_username.setUndeletable(true);
		MetadataBuilder connectorInstance_allauthorizations = connectorInstanceSchema.get("allauthorizations");
		connectorInstance_allauthorizations.setMultivalue(true);
		connectorInstance_allauthorizations.setSystemReserved(true);
		connectorInstance_allauthorizations.setUndeletable(true);
		MetadataBuilder connectorInstance_authorizations = connectorInstanceSchema.get("authorizations");
		connectorInstance_authorizations.setMultivalue(true);
		connectorInstance_authorizations.setSystemReserved(true);
		connectorInstance_authorizations.setUndeletable(true);
		MetadataBuilder connectorInstance_availableFields = connectorInstanceSchema.create("availableFields")
				.setType(MetadataValueType.STRUCTURE);
		connectorInstance_availableFields.setMultivalue(true);
		connectorInstance_availableFields.setUndeletable(true);
		connectorInstance_availableFields.defineStructureFactory(ConnectorFieldFactory.class);
		connectorInstance_availableFields.defineValidators().add(ConnectorFieldValidator.class);
		MetadataBuilder connectorInstance_code = connectorInstanceSchema.create("code").setType(MetadataValueType.STRING);
		connectorInstance_code.setDefaultRequirement(true);
		connectorInstance_code.setUndeletable(true);
		connectorInstance_code.setEssential(true);
		connectorInstance_code.setUniqueValue(true);
		MetadataBuilder connectorInstance_connectorType = connectorInstanceSchema.create("connectorType")
				.setType(MetadataValueType.REFERENCE);
		connectorInstance_connectorType.setDefaultRequirement(true);
		connectorInstance_connectorType.setUndeletable(true);
		connectorInstance_connectorType.defineReferencesTo(connectorTypeSchemaType);
		MetadataBuilder connectorInstance_createdBy = connectorInstanceSchema.get("createdBy");
		connectorInstance_createdBy.setSystemReserved(true);
		connectorInstance_createdBy.setUndeletable(true);
		MetadataBuilder connectorInstance_createdOn = connectorInstanceSchema.get("createdOn");
		connectorInstance_createdOn.setSystemReserved(true);
		connectorInstance_createdOn.setUndeletable(true);
		connectorInstance_createdOn.setSortable(true);
		MetadataBuilder connectorInstance_deleted = connectorInstanceSchema.get("deleted");
		connectorInstance_deleted.setSystemReserved(true);
		connectorInstance_deleted.setUndeletable(true);
		MetadataBuilder connectorInstance_denyTokens = connectorInstanceSchema.get("denyTokens");
		connectorInstance_denyTokens.setMultivalue(true);
		connectorInstance_denyTokens.setSystemReserved(true);
		connectorInstance_denyTokens.setUndeletable(true);
		connectorInstance_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorInstance_detachedauthorizations = connectorInstanceSchema.get("detachedauthorizations");
		connectorInstance_detachedauthorizations.setSystemReserved(true);
		connectorInstance_detachedauthorizations.setUndeletable(true);
		MetadataBuilder connectorInstance_enabled = connectorInstanceSchema.create("enabled").setType(MetadataValueType.BOOLEAN);
		connectorInstance_enabled.setUndeletable(true);
		connectorInstance_enabled.setDefaultValue(true);
		MetadataBuilder connectorInstance_followers = connectorInstanceSchema.get("followers");
		connectorInstance_followers.setMultivalue(true);
		connectorInstance_followers.setSystemReserved(true);
		connectorInstance_followers.setUndeletable(true);
		connectorInstance_followers.setSearchable(true);
		MetadataBuilder connectorInstance_id = connectorInstanceSchema.get("id");
		connectorInstance_id.setDefaultRequirement(true);
		connectorInstance_id.setSystemReserved(true);
		connectorInstance_id.setUndeletable(true);
		connectorInstance_id.setSearchable(true);
		connectorInstance_id.setSortable(true);
		connectorInstance_id.setUniqueValue(true);
		connectorInstance_id.setUnmodifiable(true);
		MetadataBuilder connectorInstance_inheritedauthorizations = connectorInstanceSchema.get("inheritedauthorizations");
		connectorInstance_inheritedauthorizations.setMultivalue(true);
		connectorInstance_inheritedauthorizations.setSystemReserved(true);
		connectorInstance_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder connectorInstance_lastTraversalOn = connectorInstanceSchema.create("lastTraversalOn")
				.setType(MetadataValueType.DATE_TIME);
		connectorInstance_lastTraversalOn.setUndeletable(true);
		MetadataBuilder connectorInstance_legacyIdentifier = connectorInstanceSchema.get("legacyIdentifier");
		connectorInstance_legacyIdentifier.setDefaultRequirement(true);
		connectorInstance_legacyIdentifier.setSystemReserved(true);
		connectorInstance_legacyIdentifier.setUndeletable(true);
		connectorInstance_legacyIdentifier.setSearchable(true);
		connectorInstance_legacyIdentifier.setUniqueValue(true);
		connectorInstance_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder connectorInstance_manualTokens = connectorInstanceSchema.get("manualTokens");
		connectorInstance_manualTokens.setMultivalue(true);
		connectorInstance_manualTokens.setSystemReserved(true);
		connectorInstance_manualTokens.setUndeletable(true);
		connectorInstance_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorInstance_markedForPreviewConversion = connectorInstanceSchema.get("markedForPreviewConversion");
		connectorInstance_markedForPreviewConversion.setSystemReserved(true);
		connectorInstance_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder connectorInstance_modifiedBy = connectorInstanceSchema.get("modifiedBy");
		connectorInstance_modifiedBy.setSystemReserved(true);
		connectorInstance_modifiedBy.setUndeletable(true);
		MetadataBuilder connectorInstance_modifiedOn = connectorInstanceSchema.get("modifiedOn");
		connectorInstance_modifiedOn.setSystemReserved(true);
		connectorInstance_modifiedOn.setUndeletable(true);
		connectorInstance_modifiedOn.setSortable(true);
		MetadataBuilder connectorInstance_parentpath = connectorInstanceSchema.get("parentpath");
		connectorInstance_parentpath.setMultivalue(true);
		connectorInstance_parentpath.setSystemReserved(true);
		connectorInstance_parentpath.setUndeletable(true);
		MetadataBuilder connectorInstance_path = connectorInstanceSchema.get("path");
		connectorInstance_path.setMultivalue(true);
		connectorInstance_path.setSystemReserved(true);
		connectorInstance_path.setUndeletable(true);
		MetadataBuilder connectorInstance_pathParts = connectorInstanceSchema.get("pathParts");
		connectorInstance_pathParts.setMultivalue(true);
		connectorInstance_pathParts.setSystemReserved(true);
		connectorInstance_pathParts.setUndeletable(true);
		MetadataBuilder connectorInstance_principalpath = connectorInstanceSchema.get("principalpath");
		connectorInstance_principalpath.setSystemReserved(true);
		connectorInstance_principalpath.setUndeletable(true);
		MetadataBuilder connectorInstance_propertiesMapping = connectorInstanceSchema.create("propertiesMapping")
				.setType(MetadataValueType.STRUCTURE);
		connectorInstance_propertiesMapping.setUndeletable(true);
		connectorInstance_propertiesMapping.defineStructureFactory(MapStringListStringStructureFactory.class);
		MetadataBuilder connectorInstance_removedauthorizations = connectorInstanceSchema.get("removedauthorizations");
		connectorInstance_removedauthorizations.setMultivalue(true);
		connectorInstance_removedauthorizations.setSystemReserved(true);
		connectorInstance_removedauthorizations.setUndeletable(true);
		MetadataBuilder connectorInstance_schema = connectorInstanceSchema.get("schema");
		connectorInstance_schema.setDefaultRequirement(true);
		connectorInstance_schema.setSystemReserved(true);
		connectorInstance_schema.setUndeletable(true);
		MetadataBuilder connectorInstance_searchable = connectorInstanceSchema.get("searchable");
		connectorInstance_searchable.setSystemReserved(true);
		connectorInstance_searchable.setUndeletable(true);
		MetadataBuilder connectorInstance_shareDenyTokens = connectorInstanceSchema.get("shareDenyTokens");
		connectorInstance_shareDenyTokens.setMultivalue(true);
		connectorInstance_shareDenyTokens.setSystemReserved(true);
		connectorInstance_shareDenyTokens.setUndeletable(true);
		connectorInstance_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorInstance_shareTokens = connectorInstanceSchema.get("shareTokens");
		connectorInstance_shareTokens.setMultivalue(true);
		connectorInstance_shareTokens.setSystemReserved(true);
		connectorInstance_shareTokens.setUndeletable(true);
		connectorInstance_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorInstance_title = connectorInstanceSchema.get("title");
		connectorInstance_title.setDefaultRequirement(true);
		connectorInstance_title.setUndeletable(true);
		connectorInstance_title.setSchemaAutocomplete(true);
		connectorInstance_title.setSearchable(true);
		MetadataBuilder connectorInstance_tokens = connectorInstanceSchema.get("tokens");
		connectorInstance_tokens.setMultivalue(true);
		connectorInstance_tokens.setSystemReserved(true);
		connectorInstance_tokens.setUndeletable(true);
		MetadataBuilder connectorInstance_traversalCode = connectorInstanceSchema.create("traversalCode")
				.setType(MetadataValueType.STRING);
		connectorInstance_traversalCode.setUndeletable(true);
		MetadataBuilder connectorInstance_traversalSchedule = connectorInstanceSchema.create("traversalSchedule")
				.setType(MetadataValueType.STRUCTURE);
		connectorInstance_traversalSchedule.setMultivalue(true);
		connectorInstance_traversalSchedule.setUndeletable(true);
		connectorInstance_traversalSchedule.defineStructureFactory(TraversalScheduleFactory.class);
		MetadataBuilder connectorInstance_visibleInTrees = connectorInstanceSchema.get("visibleInTrees");
		connectorInstance_visibleInTrees.setSystemReserved(true);
		connectorInstance_visibleInTrees.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_address = connectorLdapUserDocumentSchema.create("address")
				.setType(MetadataValueType.TEXT);
		connectorLdapUserDocument_address.setUndeletable(true);
		connectorLdapUserDocument_address.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_allauthorizations = connectorLdapUserDocumentSchema.get("allauthorizations");
		connectorLdapUserDocument_allauthorizations.setMultivalue(true);
		connectorLdapUserDocument_allauthorizations.setSystemReserved(true);
		connectorLdapUserDocument_allauthorizations.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_authorizations = connectorLdapUserDocumentSchema.get("authorizations");
		connectorLdapUserDocument_authorizations.setMultivalue(true);
		connectorLdapUserDocument_authorizations.setSystemReserved(true);
		connectorLdapUserDocument_authorizations.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_company = connectorLdapUserDocumentSchema.create("company")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_company.setUndeletable(true);
		connectorLdapUserDocument_company.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_connector = connectorLdapUserDocumentSchema.create("connector")
				.setType(MetadataValueType.REFERENCE);
		connectorLdapUserDocument_connector.setDefaultRequirement(true);
		connectorLdapUserDocument_connector.setUndeletable(true);
		connectorLdapUserDocument_connector.defineReferencesTo(asList(connectorInstance_ldapSchema));
		MetadataBuilder connectorLdapUserDocument_connectorType = connectorLdapUserDocumentSchema.create("connectorType")
				.setType(MetadataValueType.REFERENCE);
		connectorLdapUserDocument_connectorType.setDefaultRequirement(true);
		connectorLdapUserDocument_connectorType.setUndeletable(true);
		connectorLdapUserDocument_connectorType.defineReferencesTo(asList(connectorTypeSchema));
		MetadataBuilder connectorLdapUserDocument_createdBy = connectorLdapUserDocumentSchema.get("createdBy");
		connectorLdapUserDocument_createdBy.setSystemReserved(true);
		connectorLdapUserDocument_createdBy.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_createdOn = connectorLdapUserDocumentSchema.get("createdOn");
		connectorLdapUserDocument_createdOn.setSystemReserved(true);
		connectorLdapUserDocument_createdOn.setUndeletable(true);
		connectorLdapUserDocument_createdOn.setSortable(true);
		MetadataBuilder connectorLdapUserDocument_deleted = connectorLdapUserDocumentSchema.get("deleted");
		connectorLdapUserDocument_deleted.setSystemReserved(true);
		connectorLdapUserDocument_deleted.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_denyTokens = connectorLdapUserDocumentSchema.get("denyTokens");
		connectorLdapUserDocument_denyTokens.setMultivalue(true);
		connectorLdapUserDocument_denyTokens.setSystemReserved(true);
		connectorLdapUserDocument_denyTokens.setUndeletable(true);
		connectorLdapUserDocument_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorLdapUserDocument_department = connectorLdapUserDocumentSchema.create("department")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_department.setUndeletable(true);
		connectorLdapUserDocument_department.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_detachedauthorizations = connectorLdapUserDocumentSchema
				.get("detachedauthorizations");
		connectorLdapUserDocument_detachedauthorizations.setSystemReserved(true);
		connectorLdapUserDocument_detachedauthorizations.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_displayName = connectorLdapUserDocumentSchema.create("displayName")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_displayName.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_distinguishedName = connectorLdapUserDocumentSchema.create("distinguishedName")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_distinguishedName.setSystemReserved(true);
		connectorLdapUserDocument_distinguishedName.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_email = connectorLdapUserDocumentSchema.create("email")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_email.setUndeletable(true);
		connectorLdapUserDocument_email.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_enabled = connectorLdapUserDocumentSchema.create("enabled")
				.setType(MetadataValueType.BOOLEAN);
		connectorLdapUserDocument_enabled.setUndeletable(true);
		connectorLdapUserDocument_enabled.setDefaultValue(true);
		MetadataBuilder connectorLdapUserDocument_errorCode = connectorLdapUserDocumentSchema.create("errorCode")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_errorCode.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_errorMessage = connectorLdapUserDocumentSchema.create("errorMessage")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_errorMessage.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_errorStackTrace = connectorLdapUserDocumentSchema.create("errorStackTrace")
				.setType(MetadataValueType.TEXT);
		connectorLdapUserDocument_errorStackTrace.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_errorsCount = connectorLdapUserDocumentSchema.create("errorsCount")
				.setType(MetadataValueType.NUMBER);
		connectorLdapUserDocument_errorsCount.setUndeletable(true);
		connectorLdapUserDocument_errorsCount.setDefaultValue(0);
		MetadataBuilder connectorLdapUserDocument_fetchDelay = connectorLdapUserDocumentSchema.create("fetchDelay")
				.setType(MetadataValueType.NUMBER);
		connectorLdapUserDocument_fetchDelay.setUndeletable(true);
		connectorLdapUserDocument_fetchDelay.setDefaultValue(10);
		MetadataBuilder connectorLdapUserDocument_fetched = connectorLdapUserDocumentSchema.create("fetched")
				.setType(MetadataValueType.BOOLEAN);
		connectorLdapUserDocument_fetched.setUndeletable(true);
		connectorLdapUserDocument_fetched.setDefaultValue(true);
		MetadataBuilder connectorLdapUserDocument_fetchedDateTime = connectorLdapUserDocumentSchema.create("fetchedDateTime")
				.setType(MetadataValueType.DATE_TIME);
		connectorLdapUserDocument_fetchedDateTime.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_firstName = connectorLdapUserDocumentSchema.create("firstName")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_firstName.setUndeletable(true);
		connectorLdapUserDocument_firstName.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_followers = connectorLdapUserDocumentSchema.get("followers");
		connectorLdapUserDocument_followers.setMultivalue(true);
		connectorLdapUserDocument_followers.setSystemReserved(true);
		connectorLdapUserDocument_followers.setUndeletable(true);
		connectorLdapUserDocument_followers.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_frequency = connectorLdapUserDocumentSchema.create("frequency")
				.setType(MetadataValueType.ENUM);
		connectorLdapUserDocument_frequency.setUndeletable(true);
		connectorLdapUserDocument_frequency.defineAsEnum(FetchFrequency.class);
		MetadataBuilder connectorLdapUserDocument_id = connectorLdapUserDocumentSchema.get("id");
		connectorLdapUserDocument_id.setDefaultRequirement(true);
		connectorLdapUserDocument_id.setSystemReserved(true);
		connectorLdapUserDocument_id.setUndeletable(true);
		connectorLdapUserDocument_id.setSearchable(true);
		connectorLdapUserDocument_id.setSortable(true);
		connectorLdapUserDocument_id.setUniqueValue(true);
		connectorLdapUserDocument_id.setUnmodifiable(true);
		MetadataBuilder connectorLdapUserDocument_inheritedauthorizations = connectorLdapUserDocumentSchema
				.get("inheritedauthorizations");
		connectorLdapUserDocument_inheritedauthorizations.setMultivalue(true);
		connectorLdapUserDocument_inheritedauthorizations.setSystemReserved(true);
		connectorLdapUserDocument_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_lastModified = connectorLdapUserDocumentSchema.create("lastModified")
				.setType(MetadataValueType.DATE_TIME);
		connectorLdapUserDocument_lastModified.setUndeletable(true);
		connectorLdapUserDocument_lastModified.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_lastName = connectorLdapUserDocumentSchema.create("lastName")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_lastName.setUndeletable(true);
		connectorLdapUserDocument_lastName.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_legacyIdentifier = connectorLdapUserDocumentSchema.get("legacyIdentifier");
		connectorLdapUserDocument_legacyIdentifier.setDefaultRequirement(true);
		connectorLdapUserDocument_legacyIdentifier.setSystemReserved(true);
		connectorLdapUserDocument_legacyIdentifier.setUndeletable(true);
		connectorLdapUserDocument_legacyIdentifier.setSearchable(true);
		connectorLdapUserDocument_legacyIdentifier.setUniqueValue(true);
		connectorLdapUserDocument_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder connectorLdapUserDocument_manager = connectorLdapUserDocumentSchema.create("manager")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_manager.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_manualTokens = connectorLdapUserDocumentSchema.get("manualTokens");
		connectorLdapUserDocument_manualTokens.setMultivalue(true);
		connectorLdapUserDocument_manualTokens.setSystemReserved(true);
		connectorLdapUserDocument_manualTokens.setUndeletable(true);
		connectorLdapUserDocument_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorLdapUserDocument_markedForPreviewConversion = connectorLdapUserDocumentSchema
				.get("markedForPreviewConversion");
		connectorLdapUserDocument_markedForPreviewConversion.setSystemReserved(true);
		connectorLdapUserDocument_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_mimetype = connectorLdapUserDocumentSchema.create("mimetype")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_mimetype.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_modifiedBy = connectorLdapUserDocumentSchema.get("modifiedBy");
		connectorLdapUserDocument_modifiedBy.setSystemReserved(true);
		connectorLdapUserDocument_modifiedBy.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_modifiedOn = connectorLdapUserDocumentSchema.get("modifiedOn");
		connectorLdapUserDocument_modifiedOn.setSystemReserved(true);
		connectorLdapUserDocument_modifiedOn.setUndeletable(true);
		connectorLdapUserDocument_modifiedOn.setSortable(true);
		MetadataBuilder connectorLdapUserDocument_neverFetch = connectorLdapUserDocumentSchema.create("neverFetch")
				.setType(MetadataValueType.BOOLEAN);
		connectorLdapUserDocument_neverFetch.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_nextFetch = connectorLdapUserDocumentSchema.create("nextFetch")
				.setType(MetadataValueType.DATE_TIME);
		connectorLdapUserDocument_nextFetch.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_parentpath = connectorLdapUserDocumentSchema.get("parentpath");
		connectorLdapUserDocument_parentpath.setMultivalue(true);
		connectorLdapUserDocument_parentpath.setSystemReserved(true);
		connectorLdapUserDocument_parentpath.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_path = connectorLdapUserDocumentSchema.get("path");
		connectorLdapUserDocument_path.setMultivalue(true);
		connectorLdapUserDocument_path.setSystemReserved(true);
		connectorLdapUserDocument_path.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_pathParts = connectorLdapUserDocumentSchema.get("pathParts");
		connectorLdapUserDocument_pathParts.setMultivalue(true);
		connectorLdapUserDocument_pathParts.setSystemReserved(true);
		connectorLdapUserDocument_pathParts.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_principalpath = connectorLdapUserDocumentSchema.get("principalpath");
		connectorLdapUserDocument_principalpath.setSystemReserved(true);
		connectorLdapUserDocument_principalpath.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_removedauthorizations = connectorLdapUserDocumentSchema
				.get("removedauthorizations");
		connectorLdapUserDocument_removedauthorizations.setMultivalue(true);
		connectorLdapUserDocument_removedauthorizations.setSystemReserved(true);
		connectorLdapUserDocument_removedauthorizations.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_schema = connectorLdapUserDocumentSchema.get("schema");
		connectorLdapUserDocument_schema.setDefaultRequirement(true);
		connectorLdapUserDocument_schema.setSystemReserved(true);
		connectorLdapUserDocument_schema.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_searchable = connectorLdapUserDocumentSchema.get("searchable");
		connectorLdapUserDocument_searchable.setSystemReserved(true);
		connectorLdapUserDocument_searchable.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_shareDenyTokens = connectorLdapUserDocumentSchema.get("shareDenyTokens");
		connectorLdapUserDocument_shareDenyTokens.setMultivalue(true);
		connectorLdapUserDocument_shareDenyTokens.setSystemReserved(true);
		connectorLdapUserDocument_shareDenyTokens.setUndeletable(true);
		connectorLdapUserDocument_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorLdapUserDocument_shareTokens = connectorLdapUserDocumentSchema.get("shareTokens");
		connectorLdapUserDocument_shareTokens.setMultivalue(true);
		connectorLdapUserDocument_shareTokens.setSystemReserved(true);
		connectorLdapUserDocument_shareTokens.setUndeletable(true);
		connectorLdapUserDocument_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorLdapUserDocument_status = connectorLdapUserDocumentSchema.create("status")
				.setType(MetadataValueType.ENUM);
		connectorLdapUserDocument_status.setUndeletable(true);
		connectorLdapUserDocument_status.defineAsEnum(ConnectorDocumentStatus.class);
		MetadataBuilder connectorLdapUserDocument_telephone = connectorLdapUserDocumentSchema.create("telephone")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_telephone.setMultivalue(true);
		connectorLdapUserDocument_telephone.setUndeletable(true);
		connectorLdapUserDocument_telephone.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_title = connectorLdapUserDocumentSchema.get("title");
		connectorLdapUserDocument_title.setUndeletable(true);
		connectorLdapUserDocument_title.setSchemaAutocomplete(true);
		connectorLdapUserDocument_title.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_tokens = connectorLdapUserDocumentSchema.get("tokens");
		connectorLdapUserDocument_tokens.setMultivalue(true);
		connectorLdapUserDocument_tokens.setSystemReserved(true);
		connectorLdapUserDocument_tokens.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_traversalCode = connectorLdapUserDocumentSchema.create("traversalCode")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_traversalCode.setDefaultRequirement(true);
		connectorLdapUserDocument_traversalCode.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_url = connectorLdapUserDocumentSchema.create("url")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_url.setDefaultRequirement(true);
		connectorLdapUserDocument_url.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_username = connectorLdapUserDocumentSchema.create("username")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_username.setUndeletable(true);
		connectorLdapUserDocument_username.setSearchable(true);
		MetadataBuilder connectorLdapUserDocument_visibleInTrees = connectorLdapUserDocumentSchema.get("visibleInTrees");
		connectorLdapUserDocument_visibleInTrees.setSystemReserved(true);
		connectorLdapUserDocument_visibleInTrees.setUndeletable(true);
		MetadataBuilder connectorLdapUserDocument_workTitle = connectorLdapUserDocumentSchema.create("workTitle")
				.setType(MetadataValueType.STRING);
		connectorLdapUserDocument_workTitle.setUndeletable(true);
		connectorLdapUserDocument_workTitle.setSearchable(true);
		MetadataBuilder connectorSmbDocument_allauthorizations = connectorSmbDocumentSchema.get("allauthorizations");
		connectorSmbDocument_allauthorizations.setMultivalue(true);
		connectorSmbDocument_allauthorizations.setSystemReserved(true);
		connectorSmbDocument_allauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_authorizations = connectorSmbDocumentSchema.get("authorizations");
		connectorSmbDocument_authorizations.setMultivalue(true);
		connectorSmbDocument_authorizations.setSystemReserved(true);
		connectorSmbDocument_authorizations.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_connector = connectorSmbDocumentSchema.create("connector")
				.setType(MetadataValueType.REFERENCE);
		connectorSmbDocument_connector.setDefaultRequirement(true);
		connectorSmbDocument_connector.setUndeletable(true);
		connectorSmbDocument_connector.defineReferencesTo(asList(connectorInstance_smbSchema));
		MetadataBuilder connectorSmbDocument_connectorType = connectorSmbDocumentSchema.create("connectorType")
				.setType(MetadataValueType.REFERENCE);
		connectorSmbDocument_connectorType.setDefaultRequirement(true);
		connectorSmbDocument_connectorType.setUndeletable(true);
		connectorSmbDocument_connectorType.defineReferencesTo(asList(connectorTypeSchema));
		MetadataBuilder connectorSmbDocument_createdBy = connectorSmbDocumentSchema.get("createdBy");
		connectorSmbDocument_createdBy.setSystemReserved(true);
		connectorSmbDocument_createdBy.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_createdOn = connectorSmbDocumentSchema.get("createdOn");
		connectorSmbDocument_createdOn.setSystemReserved(true);
		connectorSmbDocument_createdOn.setUndeletable(true);
		connectorSmbDocument_createdOn.setSortable(true);
		MetadataBuilder connectorSmbDocument_deleted = connectorSmbDocumentSchema.get("deleted");
		connectorSmbDocument_deleted.setSystemReserved(true);
		connectorSmbDocument_deleted.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_denyTokens = connectorSmbDocumentSchema.get("denyTokens");
		connectorSmbDocument_denyTokens.setMultivalue(true);
		connectorSmbDocument_denyTokens.setSystemReserved(true);
		connectorSmbDocument_denyTokens.setUndeletable(true);
		connectorSmbDocument_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbDocument_detachedauthorizations = connectorSmbDocumentSchema.get("detachedauthorizations");
		connectorSmbDocument_detachedauthorizations.setSystemReserved(true);
		connectorSmbDocument_detachedauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_errorCode = connectorSmbDocumentSchema.create("errorCode")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_errorCode.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_errorMessage = connectorSmbDocumentSchema.create("errorMessage")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_errorMessage.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_errorStackTrace = connectorSmbDocumentSchema.create("errorStackTrace")
				.setType(MetadataValueType.TEXT);
		connectorSmbDocument_errorStackTrace.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_errorsCount = connectorSmbDocumentSchema.create("errorsCount")
				.setType(MetadataValueType.NUMBER);
		connectorSmbDocument_errorsCount.setUndeletable(true);
		connectorSmbDocument_errorsCount.setDefaultValue(0);
		MetadataBuilder connectorSmbDocument_extension = connectorSmbDocumentSchema.create("extension")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_extension.setUndeletable(true);
		connectorSmbDocument_extension.setSearchable(true);
		MetadataBuilder connectorSmbDocument_fetchDelay = connectorSmbDocumentSchema.create("fetchDelay")
				.setType(MetadataValueType.NUMBER);
		connectorSmbDocument_fetchDelay.setUndeletable(true);
		connectorSmbDocument_fetchDelay.setDefaultValue(10);
		MetadataBuilder connectorSmbDocument_fetched = connectorSmbDocumentSchema.create("fetched")
				.setType(MetadataValueType.BOOLEAN);
		connectorSmbDocument_fetched.setUndeletable(true);
		connectorSmbDocument_fetched.setDefaultValue(true);
		MetadataBuilder connectorSmbDocument_fetchedDateTime = connectorSmbDocumentSchema.create("fetchedDateTime")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbDocument_fetchedDateTime.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_followers = connectorSmbDocumentSchema.get("followers");
		connectorSmbDocument_followers.setMultivalue(true);
		connectorSmbDocument_followers.setSystemReserved(true);
		connectorSmbDocument_followers.setUndeletable(true);
		connectorSmbDocument_followers.setSearchable(true);
		MetadataBuilder connectorSmbDocument_frequency = connectorSmbDocumentSchema.create("frequency")
				.setType(MetadataValueType.ENUM);
		connectorSmbDocument_frequency.setUndeletable(true);
		connectorSmbDocument_frequency.defineAsEnum(FetchFrequency.class);
		MetadataBuilder connectorSmbDocument_id = connectorSmbDocumentSchema.get("id");
		connectorSmbDocument_id.setDefaultRequirement(true);
		connectorSmbDocument_id.setSystemReserved(true);
		connectorSmbDocument_id.setUndeletable(true);
		connectorSmbDocument_id.setSearchable(true);
		connectorSmbDocument_id.setSortable(true);
		connectorSmbDocument_id.setUniqueValue(true);
		connectorSmbDocument_id.setUnmodifiable(true);
		MetadataBuilder connectorSmbDocument_inheritedauthorizations = connectorSmbDocumentSchema.get("inheritedauthorizations");
		connectorSmbDocument_inheritedauthorizations.setMultivalue(true);
		connectorSmbDocument_inheritedauthorizations.setSystemReserved(true);
		connectorSmbDocument_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_language = connectorSmbDocumentSchema.create("language")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_language.setUndeletable(true);
		connectorSmbDocument_language.setSearchable(true);
		MetadataBuilder connectorSmbDocument_lastFetchAttempt = connectorSmbDocumentSchema.create("lastFetchAttempt")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbDocument_lastFetchAttempt.setUndeletable(true);
		connectorSmbDocument_lastFetchAttempt.setSearchable(true);
		MetadataBuilder connectorSmbDocument_lastFetchAttemptDetails = connectorSmbDocumentSchema
				.create("lastFetchAttemptDetails").setType(MetadataValueType.STRING);
		connectorSmbDocument_lastFetchAttemptDetails.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_lastFetchAttemptStatus = connectorSmbDocumentSchema.create("lastFetchAttemptStatus")
				.setType(MetadataValueType.ENUM);
		connectorSmbDocument_lastFetchAttemptStatus.setUndeletable(true);
		connectorSmbDocument_lastFetchAttemptStatus.setSearchable(true);
		connectorSmbDocument_lastFetchAttemptStatus.defineAsEnum(LastFetchedStatus.class);
		MetadataBuilder connectorSmbDocument_lastModified = connectorSmbDocumentSchema.create("lastModified")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbDocument_lastModified.setUndeletable(true);
		connectorSmbDocument_lastModified.setSearchable(true);
		MetadataBuilder connectorSmbDocument_legacyIdentifier = connectorSmbDocumentSchema.get("legacyIdentifier");
		connectorSmbDocument_legacyIdentifier.setDefaultRequirement(true);
		connectorSmbDocument_legacyIdentifier.setSystemReserved(true);
		connectorSmbDocument_legacyIdentifier.setUndeletable(true);
		connectorSmbDocument_legacyIdentifier.setSearchable(true);
		connectorSmbDocument_legacyIdentifier.setUniqueValue(true);
		connectorSmbDocument_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder connectorSmbDocument_manualTokens = connectorSmbDocumentSchema.get("manualTokens");
		connectorSmbDocument_manualTokens.setMultivalue(true);
		connectorSmbDocument_manualTokens.setSystemReserved(true);
		connectorSmbDocument_manualTokens.setUndeletable(true);
		connectorSmbDocument_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbDocument_markedForPreviewConversion = connectorSmbDocumentSchema
				.get("markedForPreviewConversion");
		connectorSmbDocument_markedForPreviewConversion.setSystemReserved(true);
		connectorSmbDocument_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_mimetype = connectorSmbDocumentSchema.create("mimetype")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_mimetype.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_modifiedBy = connectorSmbDocumentSchema.get("modifiedBy");
		connectorSmbDocument_modifiedBy.setSystemReserved(true);
		connectorSmbDocument_modifiedBy.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_modifiedOn = connectorSmbDocumentSchema.get("modifiedOn");
		connectorSmbDocument_modifiedOn.setSystemReserved(true);
		connectorSmbDocument_modifiedOn.setUndeletable(true);
		connectorSmbDocument_modifiedOn.setSortable(true);
		MetadataBuilder connectorSmbDocument_neverFetch = connectorSmbDocumentSchema.create("neverFetch")
				.setType(MetadataValueType.BOOLEAN);
		connectorSmbDocument_neverFetch.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_nextFetch = connectorSmbDocumentSchema.create("nextFetch")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbDocument_nextFetch.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_parent = connectorSmbDocumentSchema.create("parent")
				.setType(MetadataValueType.REFERENCE);
		connectorSmbDocument_parent.setUndeletable(true);
		connectorSmbDocument_parent.defineTaxonomyRelationshipToType(connectorSmbFolderSchemaType);
		MetadataBuilder connectorSmbDocument_parentpath = connectorSmbDocumentSchema.get("parentpath");
		connectorSmbDocument_parentpath.setMultivalue(true);
		connectorSmbDocument_parentpath.setSystemReserved(true);
		connectorSmbDocument_parentpath.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_parsedContent = connectorSmbDocumentSchema.create("parsedContent")
				.setType(MetadataValueType.TEXT);
		connectorSmbDocument_parsedContent.setUndeletable(true);
		connectorSmbDocument_parsedContent.setSearchable(true);
		MetadataBuilder connectorSmbDocument_path = connectorSmbDocumentSchema.get("path");
		connectorSmbDocument_path.setMultivalue(true);
		connectorSmbDocument_path.setSystemReserved(true);
		connectorSmbDocument_path.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_pathParts = connectorSmbDocumentSchema.get("pathParts");
		connectorSmbDocument_pathParts.setMultivalue(true);
		connectorSmbDocument_pathParts.setSystemReserved(true);
		connectorSmbDocument_pathParts.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_permissionsHash = connectorSmbDocumentSchema.create("permissionsHash")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_permissionsHash.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_principalpath = connectorSmbDocumentSchema.get("principalpath");
		connectorSmbDocument_principalpath.setSystemReserved(true);
		connectorSmbDocument_principalpath.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_removedauthorizations = connectorSmbDocumentSchema.get("removedauthorizations");
		connectorSmbDocument_removedauthorizations.setMultivalue(true);
		connectorSmbDocument_removedauthorizations.setSystemReserved(true);
		connectorSmbDocument_removedauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_schema = connectorSmbDocumentSchema.get("schema");
		connectorSmbDocument_schema.setDefaultRequirement(true);
		connectorSmbDocument_schema.setSystemReserved(true);
		connectorSmbDocument_schema.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_searchable = connectorSmbDocumentSchema.get("searchable");
		connectorSmbDocument_searchable.setSystemReserved(true);
		connectorSmbDocument_searchable.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_shareDenyTokens = connectorSmbDocumentSchema.get("shareDenyTokens");
		connectorSmbDocument_shareDenyTokens.setMultivalue(true);
		connectorSmbDocument_shareDenyTokens.setSystemReserved(true);
		connectorSmbDocument_shareDenyTokens.setUndeletable(true);
		connectorSmbDocument_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbDocument_shareTokens = connectorSmbDocumentSchema.get("shareTokens");
		connectorSmbDocument_shareTokens.setMultivalue(true);
		connectorSmbDocument_shareTokens.setSystemReserved(true);
		connectorSmbDocument_shareTokens.setUndeletable(true);
		connectorSmbDocument_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbDocument_size = connectorSmbDocumentSchema.create("size").setType(MetadataValueType.NUMBER);
		connectorSmbDocument_size.setUndeletable(true);
		connectorSmbDocument_size.setSearchable(true);
		MetadataBuilder connectorSmbDocument_status = connectorSmbDocumentSchema.create("status").setType(MetadataValueType.ENUM);
		connectorSmbDocument_status.setUndeletable(true);
		connectorSmbDocument_status.defineAsEnum(ConnectorDocumentStatus.class);
		MetadataBuilder connectorSmbDocument_title = connectorSmbDocumentSchema.get("title");
		connectorSmbDocument_title.setUndeletable(true);
		connectorSmbDocument_title.setSchemaAutocomplete(true);
		connectorSmbDocument_title.setSearchable(true);
		MetadataBuilder connectorSmbDocument_tokens = connectorSmbDocumentSchema.get("tokens");
		connectorSmbDocument_tokens.setMultivalue(true);
		connectorSmbDocument_tokens.setSystemReserved(true);
		connectorSmbDocument_tokens.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_traversalCode = connectorSmbDocumentSchema.create("traversalCode")
				.setType(MetadataValueType.STRING);
		connectorSmbDocument_traversalCode.setDefaultRequirement(true);
		connectorSmbDocument_traversalCode.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_url = connectorSmbDocumentSchema.create("url").setType(MetadataValueType.STRING);
		connectorSmbDocument_url.setDefaultRequirement(true);
		connectorSmbDocument_url.setUndeletable(true);
		MetadataBuilder connectorSmbDocument_visibleInTrees = connectorSmbDocumentSchema.get("visibleInTrees");
		connectorSmbDocument_visibleInTrees.setSystemReserved(true);
		connectorSmbDocument_visibleInTrees.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_allauthorizations = connectorSmbFolderSchema.get("allauthorizations");
		connectorSmbFolder_allauthorizations.setMultivalue(true);
		connectorSmbFolder_allauthorizations.setSystemReserved(true);
		connectorSmbFolder_allauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_authorizations = connectorSmbFolderSchema.get("authorizations");
		connectorSmbFolder_authorizations.setMultivalue(true);
		connectorSmbFolder_authorizations.setSystemReserved(true);
		connectorSmbFolder_authorizations.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_connector = connectorSmbFolderSchema.create("connector")
				.setType(MetadataValueType.REFERENCE);
		connectorSmbFolder_connector.setDefaultRequirement(true);
		connectorSmbFolder_connector.setUndeletable(true);
		connectorSmbFolder_connector.defineReferencesTo(asList(connectorInstance_smbSchema));
		MetadataBuilder connectorSmbFolder_connectorType = connectorSmbFolderSchema.create("connectorType")
				.setType(MetadataValueType.REFERENCE);
		connectorSmbFolder_connectorType.setDefaultRequirement(true);
		connectorSmbFolder_connectorType.setUndeletable(true);
		connectorSmbFolder_connectorType.defineReferencesTo(asList(connectorTypeSchema));
		MetadataBuilder connectorSmbFolder_createdBy = connectorSmbFolderSchema.get("createdBy");
		connectorSmbFolder_createdBy.setSystemReserved(true);
		connectorSmbFolder_createdBy.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_createdOn = connectorSmbFolderSchema.get("createdOn");
		connectorSmbFolder_createdOn.setSystemReserved(true);
		connectorSmbFolder_createdOn.setUndeletable(true);
		connectorSmbFolder_createdOn.setSortable(true);
		MetadataBuilder connectorSmbFolder_deleted = connectorSmbFolderSchema.get("deleted");
		connectorSmbFolder_deleted.setSystemReserved(true);
		connectorSmbFolder_deleted.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_denyTokens = connectorSmbFolderSchema.get("denyTokens");
		connectorSmbFolder_denyTokens.setMultivalue(true);
		connectorSmbFolder_denyTokens.setSystemReserved(true);
		connectorSmbFolder_denyTokens.setUndeletable(true);
		connectorSmbFolder_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbFolder_detachedauthorizations = connectorSmbFolderSchema.get("detachedauthorizations");
		connectorSmbFolder_detachedauthorizations.setSystemReserved(true);
		connectorSmbFolder_detachedauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_errorCode = connectorSmbFolderSchema.create("errorCode")
				.setType(MetadataValueType.STRING);
		connectorSmbFolder_errorCode.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_errorMessage = connectorSmbFolderSchema.create("errorMessage")
				.setType(MetadataValueType.STRING);
		connectorSmbFolder_errorMessage.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_errorStackTrace = connectorSmbFolderSchema.create("errorStackTrace")
				.setType(MetadataValueType.TEXT);
		connectorSmbFolder_errorStackTrace.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_errorsCount = connectorSmbFolderSchema.create("errorsCount")
				.setType(MetadataValueType.NUMBER);
		connectorSmbFolder_errorsCount.setUndeletable(true);
		connectorSmbFolder_errorsCount.setDefaultValue(0);
		MetadataBuilder connectorSmbFolder_fetchDelay = connectorSmbFolderSchema.create("fetchDelay")
				.setType(MetadataValueType.NUMBER);
		connectorSmbFolder_fetchDelay.setUndeletable(true);
		connectorSmbFolder_fetchDelay.setDefaultValue(10);
		MetadataBuilder connectorSmbFolder_fetched = connectorSmbFolderSchema.create("fetched")
				.setType(MetadataValueType.BOOLEAN);
		connectorSmbFolder_fetched.setUndeletable(true);
		connectorSmbFolder_fetched.setDefaultValue(true);
		MetadataBuilder connectorSmbFolder_fetchedDateTime = connectorSmbFolderSchema.create("fetchedDateTime")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbFolder_fetchedDateTime.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_followers = connectorSmbFolderSchema.get("followers");
		connectorSmbFolder_followers.setMultivalue(true);
		connectorSmbFolder_followers.setSystemReserved(true);
		connectorSmbFolder_followers.setUndeletable(true);
		connectorSmbFolder_followers.setSearchable(true);
		MetadataBuilder connectorSmbFolder_frequency = connectorSmbFolderSchema.create("frequency")
				.setType(MetadataValueType.ENUM);
		connectorSmbFolder_frequency.setUndeletable(true);
		connectorSmbFolder_frequency.defineAsEnum(FetchFrequency.class);
		MetadataBuilder connectorSmbFolder_id = connectorSmbFolderSchema.get("id");
		connectorSmbFolder_id.setDefaultRequirement(true);
		connectorSmbFolder_id.setSystemReserved(true);
		connectorSmbFolder_id.setUndeletable(true);
		connectorSmbFolder_id.setSearchable(true);
		connectorSmbFolder_id.setSortable(true);
		connectorSmbFolder_id.setUniqueValue(true);
		connectorSmbFolder_id.setUnmodifiable(true);
		MetadataBuilder connectorSmbFolder_inheritedauthorizations = connectorSmbFolderSchema.get("inheritedauthorizations");
		connectorSmbFolder_inheritedauthorizations.setMultivalue(true);
		connectorSmbFolder_inheritedauthorizations.setSystemReserved(true);
		connectorSmbFolder_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_lastFetchAttempt = connectorSmbFolderSchema.create("lastFetchAttempt")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbFolder_lastFetchAttempt.setUndeletable(true);
		connectorSmbFolder_lastFetchAttempt.setSearchable(true);
		MetadataBuilder connectorSmbFolder_lastFetchedStatus = connectorSmbFolderSchema.create("lastFetchedStatus")
				.setType(MetadataValueType.ENUM);
		connectorSmbFolder_lastFetchedStatus.setUndeletable(true);
		connectorSmbFolder_lastFetchedStatus.setSearchable(true);
		connectorSmbFolder_lastFetchedStatus.defineAsEnum(LastFetchedStatus.class);
		MetadataBuilder connectorSmbFolder_lastModified = connectorSmbFolderSchema.create("lastModified")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbFolder_lastModified.setUndeletable(true);
		connectorSmbFolder_lastModified.setSearchable(true);
		MetadataBuilder connectorSmbFolder_legacyIdentifier = connectorSmbFolderSchema.get("legacyIdentifier");
		connectorSmbFolder_legacyIdentifier.setDefaultRequirement(true);
		connectorSmbFolder_legacyIdentifier.setSystemReserved(true);
		connectorSmbFolder_legacyIdentifier.setUndeletable(true);
		connectorSmbFolder_legacyIdentifier.setSearchable(true);
		connectorSmbFolder_legacyIdentifier.setUniqueValue(true);
		connectorSmbFolder_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder connectorSmbFolder_manualTokens = connectorSmbFolderSchema.get("manualTokens");
		connectorSmbFolder_manualTokens.setMultivalue(true);
		connectorSmbFolder_manualTokens.setSystemReserved(true);
		connectorSmbFolder_manualTokens.setUndeletable(true);
		connectorSmbFolder_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbFolder_markedForPreviewConversion = connectorSmbFolderSchema
				.get("markedForPreviewConversion");
		connectorSmbFolder_markedForPreviewConversion.setSystemReserved(true);
		connectorSmbFolder_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_mimetype = connectorSmbFolderSchema.create("mimetype")
				.setType(MetadataValueType.STRING);
		connectorSmbFolder_mimetype.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_modifiedBy = connectorSmbFolderSchema.get("modifiedBy");
		connectorSmbFolder_modifiedBy.setSystemReserved(true);
		connectorSmbFolder_modifiedBy.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_modifiedOn = connectorSmbFolderSchema.get("modifiedOn");
		connectorSmbFolder_modifiedOn.setSystemReserved(true);
		connectorSmbFolder_modifiedOn.setUndeletable(true);
		connectorSmbFolder_modifiedOn.setSortable(true);
		MetadataBuilder connectorSmbFolder_neverFetch = connectorSmbFolderSchema.create("neverFetch")
				.setType(MetadataValueType.BOOLEAN);
		connectorSmbFolder_neverFetch.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_nextFetch = connectorSmbFolderSchema.create("nextFetch")
				.setType(MetadataValueType.DATE_TIME);
		connectorSmbFolder_nextFetch.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_parent = connectorSmbFolderSchema.create("parent")
				.setType(MetadataValueType.REFERENCE);
		connectorSmbFolder_parent.setUndeletable(true);
		connectorSmbFolder_parent.defineChildOfRelationshipToType(connectorSmbFolderSchemaType);
		MetadataBuilder connectorSmbFolder_parentpath = connectorSmbFolderSchema.get("parentpath");
		connectorSmbFolder_parentpath.setMultivalue(true);
		connectorSmbFolder_parentpath.setSystemReserved(true);
		connectorSmbFolder_parentpath.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_path = connectorSmbFolderSchema.get("path");
		connectorSmbFolder_path.setMultivalue(true);
		connectorSmbFolder_path.setSystemReserved(true);
		connectorSmbFolder_path.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_pathParts = connectorSmbFolderSchema.get("pathParts");
		connectorSmbFolder_pathParts.setMultivalue(true);
		connectorSmbFolder_pathParts.setSystemReserved(true);
		connectorSmbFolder_pathParts.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_principalpath = connectorSmbFolderSchema.get("principalpath");
		connectorSmbFolder_principalpath.setSystemReserved(true);
		connectorSmbFolder_principalpath.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_removedauthorizations = connectorSmbFolderSchema.get("removedauthorizations");
		connectorSmbFolder_removedauthorizations.setMultivalue(true);
		connectorSmbFolder_removedauthorizations.setSystemReserved(true);
		connectorSmbFolder_removedauthorizations.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_schema = connectorSmbFolderSchema.get("schema");
		connectorSmbFolder_schema.setDefaultRequirement(true);
		connectorSmbFolder_schema.setSystemReserved(true);
		connectorSmbFolder_schema.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_searchable = connectorSmbFolderSchema.get("searchable");
		connectorSmbFolder_searchable.setSystemReserved(true);
		connectorSmbFolder_searchable.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_shareDenyTokens = connectorSmbFolderSchema.get("shareDenyTokens");
		connectorSmbFolder_shareDenyTokens.setMultivalue(true);
		connectorSmbFolder_shareDenyTokens.setSystemReserved(true);
		connectorSmbFolder_shareDenyTokens.setUndeletable(true);
		connectorSmbFolder_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbFolder_shareTokens = connectorSmbFolderSchema.get("shareTokens");
		connectorSmbFolder_shareTokens.setMultivalue(true);
		connectorSmbFolder_shareTokens.setSystemReserved(true);
		connectorSmbFolder_shareTokens.setUndeletable(true);
		connectorSmbFolder_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorSmbFolder_status = connectorSmbFolderSchema.create("status").setType(MetadataValueType.ENUM);
		connectorSmbFolder_status.setUndeletable(true);
		connectorSmbFolder_status.defineAsEnum(ConnectorDocumentStatus.class);
		MetadataBuilder connectorSmbFolder_title = connectorSmbFolderSchema.get("title");
		connectorSmbFolder_title.setUndeletable(true);
		connectorSmbFolder_title.setSchemaAutocomplete(true);
		connectorSmbFolder_title.setSearchable(true);
		MetadataBuilder connectorSmbFolder_tokens = connectorSmbFolderSchema.get("tokens");
		connectorSmbFolder_tokens.setMultivalue(true);
		connectorSmbFolder_tokens.setSystemReserved(true);
		connectorSmbFolder_tokens.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_traversalCode = connectorSmbFolderSchema.create("traversalCode")
				.setType(MetadataValueType.STRING);
		connectorSmbFolder_traversalCode.setDefaultRequirement(true);
		connectorSmbFolder_traversalCode.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_url = connectorSmbFolderSchema.create("url").setType(MetadataValueType.STRING);
		connectorSmbFolder_url.setDefaultRequirement(true);
		connectorSmbFolder_url.setUndeletable(true);
		MetadataBuilder connectorSmbFolder_visibleInTrees = connectorSmbFolderSchema.get("visibleInTrees");
		connectorSmbFolder_visibleInTrees.setSystemReserved(true);
		connectorSmbFolder_visibleInTrees.setUndeletable(true);
		MetadataBuilder connectorType_allauthorizations = connectorTypeSchema.get("allauthorizations");
		connectorType_allauthorizations.setMultivalue(true);
		connectorType_allauthorizations.setSystemReserved(true);
		connectorType_allauthorizations.setUndeletable(true);
		MetadataBuilder connectorType_authorizations = connectorTypeSchema.get("authorizations");
		connectorType_authorizations.setMultivalue(true);
		connectorType_authorizations.setSystemReserved(true);
		connectorType_authorizations.setUndeletable(true);
		MetadataBuilder connectorType_code = connectorTypeSchema.create("code").setType(MetadataValueType.STRING);
		connectorType_code.setDefaultRequirement(true);
		connectorType_code.setUndeletable(true);
		connectorType_code.setEssential(true);
		connectorType_code.setUniqueValue(true);
		MetadataBuilder connectorType_connectorClassName = connectorTypeSchema.create("connectorClassName")
				.setType(MetadataValueType.STRING);
		connectorType_connectorClassName.setDefaultRequirement(true);
		connectorType_connectorClassName.setUndeletable(true);
		MetadataBuilder connectorType_createdBy = connectorTypeSchema.get("createdBy");
		connectorType_createdBy.setSystemReserved(true);
		connectorType_createdBy.setUndeletable(true);
		MetadataBuilder connectorType_createdOn = connectorTypeSchema.get("createdOn");
		connectorType_createdOn.setSystemReserved(true);
		connectorType_createdOn.setUndeletable(true);
		connectorType_createdOn.setSortable(true);
		MetadataBuilder connectorType_defaultAvailableFields = connectorTypeSchema.create("defaultAvailableFields")
				.setType(MetadataValueType.STRUCTURE);
		connectorType_defaultAvailableFields.setMultivalue(true);
		connectorType_defaultAvailableFields.setUndeletable(true);
		connectorType_defaultAvailableFields.defineStructureFactory(ConnectorFieldFactory.class);
		connectorType_defaultAvailableFields.defineValidators().add(ConnectorFieldValidator.class);
		MetadataBuilder connectorType_deleted = connectorTypeSchema.get("deleted");
		connectorType_deleted.setSystemReserved(true);
		connectorType_deleted.setUndeletable(true);
		MetadataBuilder connectorType_denyTokens = connectorTypeSchema.get("denyTokens");
		connectorType_denyTokens.setMultivalue(true);
		connectorType_denyTokens.setSystemReserved(true);
		connectorType_denyTokens.setUndeletable(true);
		connectorType_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorType_detachedauthorizations = connectorTypeSchema.get("detachedauthorizations");
		connectorType_detachedauthorizations.setSystemReserved(true);
		connectorType_detachedauthorizations.setUndeletable(true);
		MetadataBuilder connectorType_followers = connectorTypeSchema.get("followers");
		connectorType_followers.setMultivalue(true);
		connectorType_followers.setSystemReserved(true);
		connectorType_followers.setUndeletable(true);
		connectorType_followers.setSearchable(true);
		MetadataBuilder connectorType_id = connectorTypeSchema.get("id");
		connectorType_id.setDefaultRequirement(true);
		connectorType_id.setSystemReserved(true);
		connectorType_id.setUndeletable(true);
		connectorType_id.setSearchable(true);
		connectorType_id.setSortable(true);
		connectorType_id.setUniqueValue(true);
		connectorType_id.setUnmodifiable(true);
		MetadataBuilder connectorType_inheritedauthorizations = connectorTypeSchema.get("inheritedauthorizations");
		connectorType_inheritedauthorizations.setMultivalue(true);
		connectorType_inheritedauthorizations.setSystemReserved(true);
		connectorType_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder connectorType_legacyIdentifier = connectorTypeSchema.get("legacyIdentifier");
		connectorType_legacyIdentifier.setDefaultRequirement(true);
		connectorType_legacyIdentifier.setSystemReserved(true);
		connectorType_legacyIdentifier.setUndeletable(true);
		connectorType_legacyIdentifier.setSearchable(true);
		connectorType_legacyIdentifier.setUniqueValue(true);
		connectorType_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder connectorType_linkedSchema = connectorTypeSchema.create("linkedSchema").setType(MetadataValueType.STRING);
		connectorType_linkedSchema.setDefaultRequirement(true);
		connectorType_linkedSchema.setUndeletable(true);
		MetadataBuilder connectorType_manualTokens = connectorTypeSchema.get("manualTokens");
		connectorType_manualTokens.setMultivalue(true);
		connectorType_manualTokens.setSystemReserved(true);
		connectorType_manualTokens.setUndeletable(true);
		connectorType_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorType_markedForPreviewConversion = connectorTypeSchema.get("markedForPreviewConversion");
		connectorType_markedForPreviewConversion.setSystemReserved(true);
		connectorType_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder connectorType_modifiedBy = connectorTypeSchema.get("modifiedBy");
		connectorType_modifiedBy.setSystemReserved(true);
		connectorType_modifiedBy.setUndeletable(true);
		MetadataBuilder connectorType_modifiedOn = connectorTypeSchema.get("modifiedOn");
		connectorType_modifiedOn.setSystemReserved(true);
		connectorType_modifiedOn.setUndeletable(true);
		connectorType_modifiedOn.setSortable(true);
		MetadataBuilder connectorType_parentpath = connectorTypeSchema.get("parentpath");
		connectorType_parentpath.setMultivalue(true);
		connectorType_parentpath.setSystemReserved(true);
		connectorType_parentpath.setUndeletable(true);
		MetadataBuilder connectorType_path = connectorTypeSchema.get("path");
		connectorType_path.setMultivalue(true);
		connectorType_path.setSystemReserved(true);
		connectorType_path.setUndeletable(true);
		MetadataBuilder connectorType_pathParts = connectorTypeSchema.get("pathParts");
		connectorType_pathParts.setMultivalue(true);
		connectorType_pathParts.setSystemReserved(true);
		connectorType_pathParts.setUndeletable(true);
		MetadataBuilder connectorType_principalpath = connectorTypeSchema.get("principalpath");
		connectorType_principalpath.setSystemReserved(true);
		connectorType_principalpath.setUndeletable(true);
		MetadataBuilder connectorType_removedauthorizations = connectorTypeSchema.get("removedauthorizations");
		connectorType_removedauthorizations.setMultivalue(true);
		connectorType_removedauthorizations.setSystemReserved(true);
		connectorType_removedauthorizations.setUndeletable(true);
		MetadataBuilder connectorType_schema = connectorTypeSchema.get("schema");
		connectorType_schema.setDefaultRequirement(true);
		connectorType_schema.setSystemReserved(true);
		connectorType_schema.setUndeletable(true);
		MetadataBuilder connectorType_searchable = connectorTypeSchema.get("searchable");
		connectorType_searchable.setSystemReserved(true);
		connectorType_searchable.setUndeletable(true);
		MetadataBuilder connectorType_shareDenyTokens = connectorTypeSchema.get("shareDenyTokens");
		connectorType_shareDenyTokens.setMultivalue(true);
		connectorType_shareDenyTokens.setSystemReserved(true);
		connectorType_shareDenyTokens.setUndeletable(true);
		connectorType_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorType_shareTokens = connectorTypeSchema.get("shareTokens");
		connectorType_shareTokens.setMultivalue(true);
		connectorType_shareTokens.setSystemReserved(true);
		connectorType_shareTokens.setUndeletable(true);
		connectorType_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder connectorType_title = connectorTypeSchema.get("title");
		connectorType_title.setUndeletable(true);
		connectorType_title.setSchemaAutocomplete(true);
		connectorType_title.setSearchable(true);
		MetadataBuilder connectorType_tokens = connectorTypeSchema.get("tokens");
		connectorType_tokens.setMultivalue(true);
		connectorType_tokens.setSystemReserved(true);
		connectorType_tokens.setUndeletable(true);
		MetadataBuilder connectorType_visibleInTrees = connectorTypeSchema.get("visibleInTrees");
		connectorType_visibleInTrees.setSystemReserved(true);
		connectorType_visibleInTrees.setUndeletable(true);
		connectorHttpDocument_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		connectorHttpDocument_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		connectorHttpDocument_nextFetch.defineDataEntry().asCalculated(NextFetchCalculator.class);
		connectorHttpDocument_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		connectorHttpDocument_path.defineDataEntry().asCalculated(PathCalculator.class);
		connectorHttpDocument_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		connectorHttpDocument_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		connectorHttpDocument_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		connectorInstance_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		connectorInstance_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		connectorInstance_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		connectorInstance_path.defineDataEntry().asCalculated(PathCalculator.class);
		connectorInstance_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		connectorInstance_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		connectorInstance_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		connectorLdapUserDocument_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		connectorLdapUserDocument_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		connectorLdapUserDocument_nextFetch.defineDataEntry().asCalculated(NextFetchCalculator.class);
		connectorLdapUserDocument_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		connectorLdapUserDocument_path.defineDataEntry().asCalculated(PathCalculator.class);
		connectorLdapUserDocument_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		connectorLdapUserDocument_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		connectorLdapUserDocument_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		connectorSmbDocument_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		connectorSmbDocument_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		connectorSmbDocument_nextFetch.defineDataEntry().asCalculated(NextFetchCalculator.class);
		connectorSmbDocument_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		connectorSmbDocument_path.defineDataEntry().asCalculated(PathCalculator.class);
		connectorSmbDocument_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		connectorSmbDocument_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		connectorSmbDocument_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		connectorSmbFolder_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		connectorSmbFolder_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		connectorSmbFolder_nextFetch.defineDataEntry().asCalculated(NextFetchCalculator.class);
		connectorSmbFolder_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		connectorSmbFolder_path.defineDataEntry().asCalculated(PathCalculator.class);
		connectorSmbFolder_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		connectorSmbFolder_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		connectorSmbFolder_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		connectorType_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		connectorType_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		connectorType_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		connectorType_path.defineDataEntry().asCalculated(PathCalculator.class);
		connectorType_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		connectorType_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		connectorType_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
	}

	public void applySchemasDisplay(SchemasDisplayManager manager) {
		SchemaTypesDisplayTransactionBuilder transaction = manager.newTransactionBuilderFor(collection);
		SchemaTypesDisplayConfig typesConfig = manager.getTypes(collection);
		transaction.add(manager.getSchema(collection, "collection_default").withFormMetadataCodes(
				asList("collection_default_code", "collection_default_title", "collection_default_languages",
						"collection_default_name")).withDisplayMetadataCodes(
				asList("collection_default_code", "collection_default_title", "collection_default_createdOn",
						"collection_default_modifiedOn", "collection_default_languages", "collection_default_name"))
				.withSearchResultsMetadataCodes(asList("collection_default_title", "collection_default_modifiedOn"))
				.withTableMetadataCodes(asList("collection_default_title", "collection_default_modifiedOn")));
		transaction.add(manager.getType(collection, "connectorHttpDocument").withSimpleSearchStatus(true)
				.withAdvancedSearchStatus(true).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMap(asList("default"))));
		transaction.add(manager.getSchema(collection, "connectorHttpDocument_default").withFormMetadataCodes(
				asList("connectorHttpDocument_default_title", "connectorHttpDocument_default_charset",
						"connectorHttpDocument_default_connector", "connectorHttpDocument_default_connectorType",
						"connectorHttpDocument_default_contentType", "connectorHttpDocument_default_copyOf",
						"connectorHttpDocument_default_digest", "connectorHttpDocument_default_downloadTime",
						"connectorHttpDocument_default_errorCode", "connectorHttpDocument_default_errorMessage",
						"connectorHttpDocument_default_errorsCount", "connectorHttpDocument_default_fetchDelay",
						"connectorHttpDocument_default_frequency", "connectorHttpDocument_default_inlinks",
						"connectorHttpDocument_default_level", "connectorHttpDocument_default_mimetype",
						"connectorHttpDocument_default_outlinks", "connectorHttpDocument_default_priority",
						"connectorHttpDocument_default_status", "connectorHttpDocument_default_traversalCode",
						"connectorHttpDocument_default_url", "connectorHttpDocument_default_fetchedDateTime",
						"connectorHttpDocument_default_lastModified", "connectorHttpDocument_default_fetched",
						"connectorHttpDocument_default_neverFetch", "connectorHttpDocument_default_onDemand",
						"connectorHttpDocument_default_errorStackTrace", "connectorHttpDocument_default_parsedContent"))
				.withDisplayMetadataCodes(asList("connectorHttpDocument_default_title", "connectorHttpDocument_default_createdBy",
						"connectorHttpDocument_default_createdOn", "connectorHttpDocument_default_modifiedBy",
						"connectorHttpDocument_default_modifiedOn", "connectorHttpDocument_default_charset",
						"connectorHttpDocument_default_connector", "connectorHttpDocument_default_connectorType",
						"connectorHttpDocument_default_contentType", "connectorHttpDocument_default_copyOf",
						"connectorHttpDocument_default_digest", "connectorHttpDocument_default_downloadTime",
						"connectorHttpDocument_default_errorCode", "connectorHttpDocument_default_errorMessage",
						"connectorHttpDocument_default_errorsCount", "connectorHttpDocument_default_fetchDelay",
						"connectorHttpDocument_default_fetched", "connectorHttpDocument_default_fetchedDateTime",
						"connectorHttpDocument_default_frequency", "connectorHttpDocument_default_inlinks",
						"connectorHttpDocument_default_lastModified", "connectorHttpDocument_default_level",
						"connectorHttpDocument_default_mimetype", "connectorHttpDocument_default_neverFetch",
						"connectorHttpDocument_default_nextFetch", "connectorHttpDocument_default_onDemand",
						"connectorHttpDocument_default_outlinks", "connectorHttpDocument_default_priority",
						"connectorHttpDocument_default_status", "connectorHttpDocument_default_traversalCode",
						"connectorHttpDocument_default_errorStackTrace", "connectorHttpDocument_default_parsedContent"))
				.withSearchResultsMetadataCodes(
						asList("connectorHttpDocument_default_title", "connectorHttpDocument_default_modifiedOn",
								"connectorHttpDocument_default_url")).withTableMetadataCodes(
						asList("connectorHttpDocument_default_title", "connectorHttpDocument_default_modifiedOn",
								"connectorHttpDocument_default_url")));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_connector").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_contentType").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_errorCode").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_level").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_parsedContent").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorHttpDocument_default_url").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getType(collection, "connectorInstance").withSimpleSearchStatus(false)
				.withAdvancedSearchStatus(false).withManageableStatus(false).withMetadataGroup(resourcesProvider.getLanguageMap(
						asList("connectors.advanced", "default:connectors.configurationTab", "connectors.ldapUserTab",
								"connectors.credentialsTab", "connectors.executionTab"))));
		transaction.add(manager.getSchema(collection, "connectorInstance_http").withFormMetadataCodes(
				asList("connectorInstance_http_code", "connectorInstance_http_title", "connectorInstance_http_connectorType",
						"connectorInstance_http_traversalCode", "connectorInstance_http_lastTraversalOn",
						"connectorInstance_http_enabled", "connectorInstance_http_availableFields",
						"connectorInstance_http_propertiesMapping", "connectorInstance_http_traversalSchedule",
						"connectorInstance_http_authenticationScheme", "connectorInstance_http_daysBeforeRefetching",
						"connectorInstance_http_documentsPerJobs", "connectorInstance_http_domain",
						"connectorInstance_http_jobsInParallel", "connectorInstance_http_maxLevel",
						"connectorInstance_http_password", "connectorInstance_http_username", "connectorInstance_http_onDemands",
						"connectorInstance_http_seeds", "connectorInstance_http_includePatterns",
						"connectorInstance_http_excludePatterns")).withDisplayMetadataCodes(
				asList("connectorInstance_http_code", "connectorInstance_http_title", "connectorInstance_http_connectorType",
						"connectorInstance_http_traversalCode", "connectorInstance_http_createdBy",
						"connectorInstance_http_createdOn", "connectorInstance_http_modifiedBy", "connectorInstance_http_enabled",
						"connectorInstance_http_lastTraversalOn", "connectorInstance_http_authenticationScheme",
						"connectorInstance_http_daysBeforeRefetching", "connectorInstance_http_documentsPerJobs",
						"connectorInstance_http_domain", "connectorInstance_http_jobsInParallel",
						"connectorInstance_http_maxLevel", "connectorInstance_http_onDemands", "connectorInstance_http_password",
						"connectorInstance_http_seeds", "connectorInstance_http_username", "connectorInstance_http_modifiedOn",
						"connectorInstance_http_includePatterns", "connectorInstance_http_excludePatterns"))
				.withSearchResultsMetadataCodes(asList("connectorInstance_http_title", "connectorInstance_http_connectorType",
						"connectorInstance_http_traversalCode")).withTableMetadataCodes(new ArrayList<String>()));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_authenticationScheme")
				.withMetadataGroup("connectors.credentialsTab").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_daysBeforeRefetching")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_documentsPerJobs")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_domain")
				.withMetadataGroup("connectors.credentialsTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_enabled")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_jobsInParallel")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_password")
				.withMetadataGroup("connectors.credentialsTab").withInputType(MetadataInputType.PASSWORD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_traversalSchedule")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_http_username")
				.withMetadataGroup("connectors.credentialsTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getSchema(collection, "connectorInstance_ldap").withFormMetadataCodes(
				asList("connectorInstance_ldap_code", "connectorInstance_ldap_title", "connectorInstance_ldap_connectionUsername",
						"connectorInstance_ldap_password", "connectorInstance_ldap_url",
						"connectorInstance_ldap_usersBaseContextList", "connectorInstance_ldap_includeRegex",
						"connectorInstance_ldap_excludeRegex", "connectorInstance_ldap_followReferences",
						"connectorInstance_ldap_enabled", "connectorInstance_ldap_traversalSchedule",
						"connectorInstance_ldap_documentsPerJob", "connectorInstance_ldap_jobsInParallel",
						"connectorInstance_ldap_dn", "connectorInstance_ldap_username", "connectorInstance_ldap_firstName",
						"connectorInstance_ldap_lastName", "connectorInstance_ldap_email", "connectorInstance_ldap_telephone",
						"connectorInstance_ldap_address", "connectorInstance_ldap_jobTitle", "connectorInstance_ldap_displayName",
						"connectorInstance_ldap_company", "connectorInstance_ldap_department", "connectorInstance_ldap_manager"))
				.withDisplayMetadataCodes(
						asList("connectorInstance_ldap_code", "connectorInstance_ldap_title", "connectorInstance_ldap_url",
								"connectorInstance_ldap_usersBaseContextList", "connectorInstance_ldap_includeRegex",
								"connectorInstance_ldap_excludeRegex")).withSearchResultsMetadataCodes(
						asList("connectorInstance_ldap_title", "connectorInstance_ldap_connectorType",
								"connectorInstance_ldap_traversalCode")).withTableMetadataCodes(new ArrayList<String>()));
		transaction
				.add(manager.getMetadata(collection, "connectorInstance_ldap_address").withMetadataGroup("connectors.ldapUserTab")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction
				.add(manager.getMetadata(collection, "connectorInstance_ldap_company").withMetadataGroup("connectors.ldapUserTab")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_connectionUsername")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_department")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_directoryType")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_displayName")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_dn").withMetadataGroup("connectors.ldapUserTab")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_documentsPerJob")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction
				.add(manager.getMetadata(collection, "connectorInstance_ldap_email").withMetadataGroup("connectors.ldapUserTab")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_enabled")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_fetchUsers")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_firstName")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_followReferences")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_jobTitle")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_jobsInParallel")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_lastName")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction
				.add(manager.getMetadata(collection, "connectorInstance_ldap_manager").withMetadataGroup("connectors.ldapUserTab")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_password")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.PASSWORD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_telephone")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_traversalSchedule")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_url")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_username")
				.withMetadataGroup("connectors.ldapUserTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_ldap_usersBaseContextList")
				.withMetadataGroup("default:connectors.configurationTab").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getSchema(collection, "connectorInstance_smb").withFormMetadataCodes(
				asList("connectorInstance_smb_code", "connectorInstance_smb_title", "connectorInstance_smb_domain",
						"connectorInstance_smb_username", "connectorInstance_smb_password", "connectorInstance_smb_smbSeeds",
						"connectorInstance_smb_exclusions", "connectorInstance_smb_inclusions", "connectorInstance_smb_enabled",
						"connectorInstance_smb_traversalSchedule", "connectorInstance_smb_skipShareAccessControl"))
				.withDisplayMetadataCodes(
						asList("connectorInstance_smb_code", "connectorInstance_smb_title", "connectorInstance_smb_domain",
								"connectorInstance_smb_username", "connectorInstance_smb_smbSeeds",
								"connectorInstance_smb_exclusions", "connectorInstance_smb_inclusions"))
				.withSearchResultsMetadataCodes(asList("connectorInstance_smb_title", "connectorInstance_smb_connectorType",
						"connectorInstance_smb_traversalCode")).withTableMetadataCodes(new ArrayList<String>()));
		transaction
				.add(manager.getMetadata(collection, "connectorInstance_smb_enabled").withMetadataGroup("connectors.executionTab")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_smb_password").withMetadataGroup("")
				.withInputType(MetadataInputType.PASSWORD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_smb_skipShareAccessControl")
				.withMetadataGroup("connectors.advanced").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "connectorInstance_smb_traversalSchedule")
				.withMetadataGroup("connectors.executionTab").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getSchema(collection, "connectorInstance_default").withFormMetadataCodes(
				asList("connectorInstance_default_code", "connectorInstance_default_title",
						"connectorInstance_default_connectorType", "connectorInstance_default_traversalCode",
						"connectorInstance_default_lastTraversalOn", "connectorInstance_default_enabled",
						"connectorInstance_default_availableFields", "connectorInstance_default_propertiesMapping",
						"connectorInstance_default_traversalSchedule")).withDisplayMetadataCodes(
				asList("connectorInstance_default_code", "connectorInstance_default_title",
						"connectorInstance_default_connectorType", "connectorInstance_default_traversalCode",
						"connectorInstance_default_createdBy", "connectorInstance_default_createdOn",
						"connectorInstance_default_modifiedBy", "connectorInstance_default_enabled",
						"connectorInstance_default_lastTraversalOn", "connectorInstance_default_modifiedOn"))
				.withSearchResultsMetadataCodes(
						asList("connectorInstance_default_title", "connectorInstance_default_connectorType",
								"connectorInstance_default_traversalCode")).withTableMetadataCodes(
						asList("connectorInstance_default_title", "connectorInstance_default_connectorType",
								"connectorInstance_default_traversalCode")));
		transaction.add(manager.getMetadata(collection, "connectorInstance_default_connectorType").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "connectorLdapUserDocument").withSimpleSearchStatus(true)
				.withAdvancedSearchStatus(true).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMap(asList("default"))));
		transaction.add(manager.getSchema(collection, "connectorLdapUserDocument_default").withFormMetadataCodes(
				asList("connectorLdapUserDocument_default_title", "connectorLdapUserDocument_default_company",
						"connectorLdapUserDocument_default_connector", "connectorLdapUserDocument_default_connectorType",
						"connectorLdapUserDocument_default_department", "connectorLdapUserDocument_default_displayName",
						"connectorLdapUserDocument_default_email", "connectorLdapUserDocument_default_errorCode",
						"connectorLdapUserDocument_default_errorMessage", "connectorLdapUserDocument_default_errorsCount",
						"connectorLdapUserDocument_default_fetchDelay", "connectorLdapUserDocument_default_firstName",
						"connectorLdapUserDocument_default_frequency", "connectorLdapUserDocument_default_lastName",
						"connectorLdapUserDocument_default_manager", "connectorLdapUserDocument_default_mimetype",
						"connectorLdapUserDocument_default_status", "connectorLdapUserDocument_default_telephone",
						"connectorLdapUserDocument_default_traversalCode", "connectorLdapUserDocument_default_url",
						"connectorLdapUserDocument_default_username", "connectorLdapUserDocument_default_workTitle",
						"connectorLdapUserDocument_default_fetchedDateTime", "connectorLdapUserDocument_default_lastModified",
						"connectorLdapUserDocument_default_enabled", "connectorLdapUserDocument_default_fetched",
						"connectorLdapUserDocument_default_neverFetch", "connectorLdapUserDocument_default_address",
						"connectorLdapUserDocument_default_errorStackTrace")).withDisplayMetadataCodes(
				asList("connectorLdapUserDocument_default_title", "connectorLdapUserDocument_default_createdBy",
						"connectorLdapUserDocument_default_createdOn", "connectorLdapUserDocument_default_modifiedBy",
						"connectorLdapUserDocument_default_modifiedOn", "connectorLdapUserDocument_default_company",
						"connectorLdapUserDocument_default_connector", "connectorLdapUserDocument_default_connectorType",
						"connectorLdapUserDocument_default_department", "connectorLdapUserDocument_default_displayName",
						"connectorLdapUserDocument_default_email", "connectorLdapUserDocument_default_enabled",
						"connectorLdapUserDocument_default_errorCode", "connectorLdapUserDocument_default_errorMessage",
						"connectorLdapUserDocument_default_errorsCount", "connectorLdapUserDocument_default_fetchDelay",
						"connectorLdapUserDocument_default_fetched", "connectorLdapUserDocument_default_fetchedDateTime",
						"connectorLdapUserDocument_default_firstName", "connectorLdapUserDocument_default_frequency",
						"connectorLdapUserDocument_default_lastModified", "connectorLdapUserDocument_default_lastName",
						"connectorLdapUserDocument_default_manager", "connectorLdapUserDocument_default_mimetype",
						"connectorLdapUserDocument_default_neverFetch", "connectorLdapUserDocument_default_nextFetch",
						"connectorLdapUserDocument_default_status", "connectorLdapUserDocument_default_telephone",
						"connectorLdapUserDocument_default_traversalCode", "connectorLdapUserDocument_default_username",
						"connectorLdapUserDocument_default_workTitle", "connectorLdapUserDocument_default_address",
						"connectorLdapUserDocument_default_errorStackTrace")).withSearchResultsMetadataCodes(
				asList("connectorLdapUserDocument_default_firstName", "connectorLdapUserDocument_default_lastName",
						"connectorLdapUserDocument_default_telephone", "connectorLdapUserDocument_default_email",
						"connectorLdapUserDocument_default_address", "connectorLdapUserDocument_default_title"))
				.withTableMetadataCodes(
						asList("connectorLdapUserDocument_default_firstName", "connectorLdapUserDocument_default_lastName",
								"connectorLdapUserDocument_default_telephone", "connectorLdapUserDocument_default_email",
								"connectorLdapUserDocument_default_address", "connectorLdapUserDocument_default_title")));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_address").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_connector").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_email").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_firstName").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_lastName").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_telephone").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorLdapUserDocument_default_username").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getType(collection, "connectorSmbDocument").withSimpleSearchStatus(true)
				.withAdvancedSearchStatus(true).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMap(asList("default"))));
		transaction.add(manager.getSchema(collection, "connectorSmbDocument_default").withFormMetadataCodes(
				asList("connectorSmbDocument_default_title", "connectorSmbDocument_default_connector",
						"connectorSmbDocument_default_connectorType", "connectorSmbDocument_default_errorCode",
						"connectorSmbDocument_default_errorMessage", "connectorSmbDocument_default_errorsCount",
						"connectorSmbDocument_default_extension", "connectorSmbDocument_default_fetchDelay",
						"connectorSmbDocument_default_frequency", "connectorSmbDocument_default_language",
						"connectorSmbDocument_default_lastFetchAttemptDetails",
						"connectorSmbDocument_default_lastFetchAttemptStatus", "connectorSmbDocument_default_mimetype",
						"connectorSmbDocument_default_parent", "connectorSmbDocument_default_permissionsHash",
						"connectorSmbDocument_default_size", "connectorSmbDocument_default_status",
						"connectorSmbDocument_default_traversalCode", "connectorSmbDocument_default_url",
						"connectorSmbDocument_default_fetchedDateTime", "connectorSmbDocument_default_lastFetchAttempt",
						"connectorSmbDocument_default_lastModified", "connectorSmbDocument_default_fetched",
						"connectorSmbDocument_default_neverFetch", "connectorSmbDocument_default_errorStackTrace",
						"connectorSmbDocument_default_parsedContent")).withDisplayMetadataCodes(
				asList("connectorSmbDocument_default_title", "connectorSmbDocument_default_createdBy",
						"connectorSmbDocument_default_createdOn", "connectorSmbDocument_default_modifiedBy",
						"connectorSmbDocument_default_modifiedOn", "connectorSmbDocument_default_connector",
						"connectorSmbDocument_default_connectorType", "connectorSmbDocument_default_errorCode",
						"connectorSmbDocument_default_errorMessage", "connectorSmbDocument_default_errorsCount",
						"connectorSmbDocument_default_extension", "connectorSmbDocument_default_fetchDelay",
						"connectorSmbDocument_default_fetched", "connectorSmbDocument_default_fetchedDateTime",
						"connectorSmbDocument_default_frequency", "connectorSmbDocument_default_language",
						"connectorSmbDocument_default_lastFetchAttempt", "connectorSmbDocument_default_lastFetchAttemptDetails",
						"connectorSmbDocument_default_lastFetchAttemptStatus", "connectorSmbDocument_default_lastModified",
						"connectorSmbDocument_default_mimetype", "connectorSmbDocument_default_neverFetch",
						"connectorSmbDocument_default_nextFetch", "connectorSmbDocument_default_parent",
						"connectorSmbDocument_default_permissionsHash", "connectorSmbDocument_default_size",
						"connectorSmbDocument_default_status", "connectorSmbDocument_default_traversalCode",
						"connectorSmbDocument_default_errorStackTrace", "connectorSmbDocument_default_parsedContent"))
				.withSearchResultsMetadataCodes(
						asList("connectorSmbDocument_default_title", "connectorSmbDocument_default_modifiedOn"))
				.withTableMetadataCodes(asList("connectorSmbDocument_default_title", "connectorSmbDocument_default_modifiedOn")));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_connector").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_errorCode").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_extension").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_fetchedDateTime").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_language").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_parsedContent").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_size").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "connectorSmbDocument_default_url").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getSchema(collection, "connectorSmbFolder_default").withFormMetadataCodes(
				asList("connectorSmbFolder_default_title", "connectorSmbFolder_default_connector",
						"connectorSmbFolder_default_connectorType", "connectorSmbFolder_default_errorCode",
						"connectorSmbFolder_default_errorMessage", "connectorSmbFolder_default_errorsCount",
						"connectorSmbFolder_default_fetchDelay", "connectorSmbFolder_default_frequency",
						"connectorSmbFolder_default_lastFetchedStatus", "connectorSmbFolder_default_mimetype",
						"connectorSmbFolder_default_parent", "connectorSmbFolder_default_status",
						"connectorSmbFolder_default_traversalCode", "connectorSmbFolder_default_url",
						"connectorSmbFolder_default_fetchedDateTime", "connectorSmbFolder_default_lastFetchAttempt",
						"connectorSmbFolder_default_lastModified", "connectorSmbFolder_default_fetched",
						"connectorSmbFolder_default_neverFetch", "connectorSmbFolder_default_errorStackTrace"))
				.withDisplayMetadataCodes(asList("connectorSmbFolder_default_title", "connectorSmbFolder_default_createdBy",
						"connectorSmbFolder_default_createdOn", "connectorSmbFolder_default_modifiedBy",
						"connectorSmbFolder_default_modifiedOn", "connectorSmbFolder_default_connector",
						"connectorSmbFolder_default_connectorType", "connectorSmbFolder_default_errorCode",
						"connectorSmbFolder_default_errorMessage", "connectorSmbFolder_default_errorsCount",
						"connectorSmbFolder_default_fetchDelay", "connectorSmbFolder_default_fetched",
						"connectorSmbFolder_default_fetchedDateTime", "connectorSmbFolder_default_frequency",
						"connectorSmbFolder_default_lastFetchAttempt", "connectorSmbFolder_default_lastFetchedStatus",
						"connectorSmbFolder_default_lastModified", "connectorSmbFolder_default_mimetype",
						"connectorSmbFolder_default_neverFetch", "connectorSmbFolder_default_nextFetch",
						"connectorSmbFolder_default_parent", "connectorSmbFolder_default_status",
						"connectorSmbFolder_default_traversalCode", "connectorSmbFolder_default_errorStackTrace"))
				.withSearchResultsMetadataCodes(
						asList("connectorSmbFolder_default_title", "connectorSmbFolder_default_modifiedOn"))
				.withTableMetadataCodes(asList("connectorSmbFolder_default_title", "connectorSmbFolder_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "connectorType_default").withFormMetadataCodes(
				asList("connectorType_default_code", "connectorType_default_title", "connectorType_default_connectorClassName",
						"connectorType_default_linkedSchema", "connectorType_default_defaultAvailableFields"))
				.withDisplayMetadataCodes(
						asList("connectorType_default_code", "connectorType_default_title", "connectorType_default_createdBy",
								"connectorType_default_createdOn", "connectorType_default_modifiedBy",
								"connectorType_default_modifiedOn", "connectorType_default_connectorClassName",
								"connectorType_default_linkedSchema"))
				.withSearchResultsMetadataCodes(asList("connectorType_default_title", "connectorType_default_modifiedOn"))
				.withTableMetadataCodes(asList("connectorType_default_title", "connectorType_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "emailToSend_default").withFormMetadataCodes(
				asList("emailToSend_default_title", "emailToSend_default_error", "emailToSend_default_parameters",
						"emailToSend_default_subject", "emailToSend_default_template", "emailToSend_default_tryingCount",
						"emailToSend_default_sendOn", "emailToSend_default_BCC", "emailToSend_default_CC",
						"emailToSend_default_from", "emailToSend_default_to")).withDisplayMetadataCodes(
				asList("emailToSend_default_title", "emailToSend_default_createdBy", "emailToSend_default_createdOn",
						"emailToSend_default_modifiedBy", "emailToSend_default_modifiedOn", "emailToSend_default_error",
						"emailToSend_default_parameters", "emailToSend_default_sendOn", "emailToSend_default_subject",
						"emailToSend_default_template", "emailToSend_default_tryingCount"))
				.withSearchResultsMetadataCodes(asList("emailToSend_default_title", "emailToSend_default_modifiedOn"))
				.withTableMetadataCodes(asList("emailToSend_default_title", "emailToSend_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "event_default").withFormMetadataCodes(
				asList("event_default_title", "event_default_type", "event_default_delta", "event_default_eventPrincipalPath",
						"event_default_ip", "event_default_permissionDateRange", "event_default_permissionRoles",
						"event_default_permissionUsers", "event_default_reason", "event_default_userRoles",
						"event_default_username")).withDisplayMetadataCodes(
				asList("event_default_title", "event_default_type", "event_default_createdBy", "event_default_createdOn",
						"event_default_modifiedBy", "event_default_modifiedOn", "event_default_delta",
						"event_default_eventPrincipalPath", "event_default_ip", "event_default_permissionDateRange",
						"event_default_permissionRoles", "event_default_permissionUsers", "event_default_reason",
						"event_default_recordIdentifier", "event_default_userRoles", "event_default_username"))
				.withSearchResultsMetadataCodes(asList("event_default_title", "event_default_modifiedOn"))
				.withTableMetadataCodes(asList("event_default_title", "event_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "facet_field").withFormMetadataCodes(
				asList("facet_field_title", "facet_field_elementPerPage", "facet_field_facetType",
						"facet_field_fieldDatastoreCode", "facet_field_order", "facet_field_orderResult", "facet_field_pages",
						"facet_field_active", "facet_field_openByDefault", "facet_field_fieldValuesLabel"))
				.withDisplayMetadataCodes(
						asList("facet_field_title", "facet_field_createdBy", "facet_field_createdOn", "facet_field_modifiedBy",
								"facet_field_modifiedOn", "facet_field_active", "facet_field_elementPerPage",
								"facet_field_facetType", "facet_field_fieldDatastoreCode", "facet_field_openByDefault",
								"facet_field_order", "facet_field_orderResult", "facet_field_pages",
								"facet_field_fieldValuesLabel"))
				.withSearchResultsMetadataCodes(asList("facet_field_title", "facet_field_modifiedOn"))
				.withTableMetadataCodes(new ArrayList<String>()));
		transaction.add(manager.getSchema(collection, "facet_query").withFormMetadataCodes(
				asList("facet_query_title", "facet_query_elementPerPage", "facet_query_facetType",
						"facet_query_fieldDatastoreCode", "facet_query_order", "facet_query_orderResult", "facet_query_pages",
						"facet_query_active", "facet_query_openByDefault", "facet_query_listQueries")).withDisplayMetadataCodes(
				asList("facet_query_title", "facet_query_createdBy", "facet_query_createdOn", "facet_query_modifiedBy",
						"facet_query_modifiedOn", "facet_query_active", "facet_query_elementPerPage", "facet_query_facetType",
						"facet_query_fieldDatastoreCode", "facet_query_openByDefault", "facet_query_order",
						"facet_query_orderResult", "facet_query_pages", "facet_query_listQueries"))
				.withSearchResultsMetadataCodes(asList("facet_query_title", "facet_query_modifiedOn"))
				.withTableMetadataCodes(new ArrayList<String>()));
		transaction.add(manager.getSchema(collection, "facet_default").withFormMetadataCodes(
				asList("facet_default_title", "facet_default_elementPerPage", "facet_default_facetType",
						"facet_default_fieldDatastoreCode", "facet_default_order", "facet_default_orderResult",
						"facet_default_pages", "facet_default_active", "facet_default_openByDefault")).withDisplayMetadataCodes(
				asList("facet_default_title", "facet_default_createdBy", "facet_default_createdOn", "facet_default_modifiedBy",
						"facet_default_modifiedOn", "facet_default_active", "facet_default_elementPerPage",
						"facet_default_facetType", "facet_default_fieldDatastoreCode", "facet_default_openByDefault",
						"facet_default_order", "facet_default_orderResult", "facet_default_pages"))
				.withSearchResultsMetadataCodes(asList("facet_default_title", "facet_default_modifiedOn"))
				.withTableMetadataCodes(asList("facet_default_title", "facet_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "group_default").withFormMetadataCodes(
				asList("group_default_code", "group_default_title", "group_default_parent", "group_default_roles",
						"group_default_isGlobal")).withDisplayMetadataCodes(
				asList("group_default_code", "group_default_title", "group_default_createdOn", "group_default_modifiedOn",
						"group_default_isGlobal", "group_default_parent", "group_default_roles"))
				.withSearchResultsMetadataCodes(asList("group_default_title", "group_default_modifiedOn"))
				.withTableMetadataCodes(asList("group_default_title", "group_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "report_default").withFormMetadataCodes(
				asList("report_default_title", "report_default_columnsCount", "report_default_linesCount",
						"report_default_schemaTypeCode", "report_default_separator", "report_default_username",
						"report_default_reportedMetadata")).withDisplayMetadataCodes(
				asList("report_default_title", "report_default_createdBy", "report_default_createdOn",
						"report_default_modifiedBy", "report_default_modifiedOn", "report_default_columnsCount",
						"report_default_linesCount", "report_default_schemaTypeCode", "report_default_separator",
						"report_default_username"))
				.withSearchResultsMetadataCodes(asList("report_default_title", "report_default_modifiedOn"))
				.withTableMetadataCodes(asList("report_default_title", "report_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "task_approval").withFormMetadataCodes(
				asList("task_approval_title", "task_approval_assignCandidates", "task_approval_assignedTo",
						"task_approval_finishedBy", "task_approval_workflowIdentifier", "task_approval_workflowRecordIdentifiers",
						"task_approval_assignedOn", "task_approval_dueDate", "task_approval_finishedOn",
						"task_approval_decision")).withDisplayMetadataCodes(
				asList("task_approval_title", "task_approval_createdBy", "task_approval_createdOn", "task_approval_modifiedBy",
						"task_approval_modifiedOn", "task_approval_assignCandidates", "task_approval_assignedOn",
						"task_approval_assignedTo", "task_approval_dueDate", "task_approval_finishedBy",
						"task_approval_finishedOn", "task_approval_workflowIdentifier", "task_approval_workflowRecordIdentifiers",
						"task_approval_decision"))
				.withSearchResultsMetadataCodes(asList("task_approval_title", "task_approval_modifiedOn"))
				.withTableMetadataCodes(new ArrayList<String>()));
		transaction.add(manager.getSchema(collection, "task_default").withFormMetadataCodes(
				asList("task_default_title", "task_default_assignCandidates", "task_default_assignedTo",
						"task_default_finishedBy", "task_default_workflowIdentifier", "task_default_workflowRecordIdentifiers",
						"task_default_assignedOn", "task_default_dueDate", "task_default_finishedOn")).withDisplayMetadataCodes(
				asList("task_default_title", "task_default_createdBy", "task_default_createdOn", "task_default_modifiedBy",
						"task_default_modifiedOn", "task_default_assignCandidates", "task_default_assignedOn",
						"task_default_assignedTo", "task_default_dueDate", "task_default_finishedBy", "task_default_finishedOn",
						"task_default_workflowIdentifier", "task_default_workflowRecordIdentifiers"))
				.withSearchResultsMetadataCodes(asList("task_default_title", "task_default_modifiedOn"))
				.withTableMetadataCodes(asList("task_default_title", "task_default_modifiedOn")));
		transaction.add(manager.getSchema(collection, "userDocument_default").withFormMetadataCodes(
				asList("userDocument_default_title", "userDocument_default_user", "userDocument_default_content"))
				.withDisplayMetadataCodes(
						asList("userDocument_default_title", "userDocument_default_createdOn", "userDocument_default_modifiedOn",
								"userDocument_default_user", "userDocument_default_content"))
				.withSearchResultsMetadataCodes(asList("userDocument_default_title", "userDocument_default_modifiedOn"))
				.withTableMetadataCodes(asList("userDocument_default_title", "userDocument_default_modifiedOn")));
		manager.execute(transaction.build());
	}

	public void applyGeneratedRoles() {
		RolesManager rolesManager = appLayerFactory.getModelLayerFactory().getRolesManager();
		;
		rolesManager.updateRole(rolesManager.getRole(collection, "ADM").withNewPermissions(
				asList("core.deleteContentVersion", "core.ldapConfigurationManagement", "core.manageConnectors",
						"core.manageEmailServer", "core.manageFacets", "core.manageMetadataExtractor",
						"core.manageMetadataSchemas", "core.manageSearchEngine", "core.manageSearchReports",
						"core.manageSecurity", "core.manageSystemCollections", "core.manageSystemConfiguration",
						"core.manageSystemDataImports", "core.manageSystemGroups", "core.manageSystemModules",
						"core.manageSystemServers", "core.manageSystemUpdates", "core.manageSystemUsers", "core.manageTaxonomies",
						"core.manageTrash", "core.manageValueList", "core.viewEvents")));
	}
}
