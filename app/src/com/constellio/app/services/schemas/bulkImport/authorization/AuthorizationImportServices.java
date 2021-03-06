package com.constellio.app.services.schemas.bulkImport.authorization;

import static com.constellio.app.ui.i18n.i18n.$;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.constellio.app.services.schemas.bulkImport.BulkImportResults;
import com.constellio.app.services.schemas.bulkImport.ImportError;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_AuthorizationIDMissing;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_AuthorizationPrincipalsMissing;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_AuthorizationTargetsMissing;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_EmptyLegacyId;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_EmptyPrincipalId;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_InvalidAccess;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_InvalidPrincipalType;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_InvalidRole;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_InvalidTargetType;
import com.constellio.app.services.schemas.bulkImport.authorization.ImportedAuthorizationValidatorRuntimeException.ImportedAuthorizationValidatorRuntimeException_UseOfAccessAndRole;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.security.AuthorizationsServices;
import com.constellio.model.services.security.AuthorizationsServicesRuntimeException.NoSuchAuthorizationWithId;

public class AuthorizationImportServices {
	private static final Logger LOGGER = LogManager.getLogger(AuthorizationImportServices.class);
	public static final String AUTHORIZATION_ID_ADDED_SEVERAL_TIMES = "AuthorizationImportServices.authorizationIdAddedSeveralTimes";
	public static final String INVALID_ROLE = "AuthorizationImportServices.invalidRole";
	public static final String INVALID_ACCESS = "AuthorizationImportServices.invalidAccess";
	public static final String USE_ROLE_OR_ACCESS = "AuthorizationImportServices.useRoleOrAccess";
	public static final String AUTHORIZATION_ID_MISSING = "AuthorizationImportServices.authorizationIDMissing";
	public static final String AUTHORIZATION_TARGETS_MISSING = "AuthorizationImportServices.authorizationTargetsMissing";
	public static final String AUTHORIZATION_PRINCIPALS_MISSING = "AuthorizationImportServices.authorizationPrincipalsMissing";
	public static final String INVALID_TARGET_TYPE = "AuthorizationImportServices.invalidTargetType";
	public static final String INVALID_PRINCIPAL_TYPE = "AuthorizationImportServices.invalidPrincipalType";
	public static final String EMPTY_LEGACY_ID = "AuthorizationImportServices.emptyLegacyId";
	public static final String EMPTY_PRINCIPAL_ID = "AuthorizationImportServices.emptyPrincipalId";

	public BulkImportResults bulkImport(File file, String collection, ModelLayerFactory modelLayerFactory) {
		BulkImportResults result = new BulkImportResults();
		if (file != null) {
			Document document = getDocumentFromFile(file);
			bulkImport(document, result, collection, modelLayerFactory);
		}
		return result;
	}

	private void bulkImport(Document document, BulkImportResults bulkImportResults, String collection,
			ModelLayerFactory modelLayerFactory) {
		try {
			List<ImportedAuthorization> allImportedAuthorizations = new ImportedAuthorizationReader(
					document).readAll();
			List<ImportedAuthorization> validAuthorizations = addInvalidAuthorizationsToErrorsAndGetValidAuthorizations(
					allImportedAuthorizations, bulkImportResults);
			addUpdateOrDeleteAuthorizations(validAuthorizations, collection, modelLayerFactory, bulkImportResults);
		} catch (Exception e) {
			bulkImportResults.add(new ImportError("", e.getMessage()));
		}

	}

	private void addUpdateOrDeleteAuthorizations(List<ImportedAuthorization> authorizations, String collection,
			ModelLayerFactory modelLayerFactory, BulkImportResults bulkImportResults) {
		if (!authorizations.isEmpty()) {
			AuthorizationsServices authorizationServices = modelLayerFactory
					.newAuthorizationsServices();
			ImportedAuthorizationToAuthorizationBuilder builder = new ImportedAuthorizationToAuthorizationBuilder(collection,
					modelLayerFactory);
			for (ImportedAuthorization importedAuthorization : authorizations) {
				try {
					addUpdateOrDeleteAuthorization(importedAuthorization, builder, collection, authorizationServices);
					bulkImportResults.inc();
				} catch (Exception e) {
					bulkImportResults.add(new ImportError(importedAuthorization.getId(), e.getMessage()));
				}
			}
			modelLayerFactory.getBatchProcessesManager().waitUntilAllFinished();
		}
	}

	void addUpdateOrDeleteAuthorization(ImportedAuthorization importedAuthorization,
			ImportedAuthorizationToAuthorizationBuilder builder, String collection,
			AuthorizationsServices authorizationServices) {
		if (StringUtils.isBlank(importedAuthorization.getAccess()) && (importedAuthorization.getRoles() == null
				|| importedAuthorization.getRoles().isEmpty())) {
			deleteAuthorizationIfExists(authorizationServices, collection, importedAuthorization.getId());
		} else {
			try {
				Authorization authorization = builder.build(importedAuthorization);
				try {
					authorizationServices
							.getAuthorizationIdByIdWithoutPrefix(collection, importedAuthorization.getId());
					authorizationServices.modify(authorization, null);
				} catch (NoSuchAuthorizationWithId e) {
					//new authorization
					authorizationServices.add(authorization, null);
				}
			} catch (ImportedAuthorizationBuilderRuntimeException e) {
				deleteAuthorizationIfExists(authorizationServices, collection, importedAuthorization.getId());
			}
		}

	}

	private void deleteAuthorizationIfExists(AuthorizationsServices authorizationServices, String collection, String id) {
		try {
			String authorizationId = authorizationServices
					.getAuthorizationIdByIdWithoutPrefix(collection, id);
			authorizationServices.delete(authorizationId, collection, null, true);
		} catch (NoSuchAuthorizationWithId e) {
			LOGGER.warn("Authorization not deleted : no authorization with id " + id);
		}
	}

	List<ImportedAuthorization> addInvalidAuthorizationsToErrorsAndGetValidAuthorizations(
			List<ImportedAuthorization> allImportedAuthorizations,
			BulkImportResults bulkImportResults) {
		List<String> addedAuthorizationsCodes = new ArrayList<>();
		List<ImportedAuthorization> validAuthorizations = new ArrayList<>();
		ImportedAuthorizationValidator importedAuthorizationValidator = new ImportedAuthorizationValidator();

		for (ImportedAuthorization importedAuthorization : allImportedAuthorizations) {
			String id = importedAuthorization.getId();
			if (addedAuthorizationsCodes.contains(id)) {
				bulkImportResults.add(new ImportError(id, AUTHORIZATION_ID_ADDED_SEVERAL_TIMES));
			} else {
				try {
					importedAuthorizationValidator.validate(importedAuthorization);
					validAuthorizations.add(importedAuthorization);
				} catch (ImportedAuthorizationValidatorRuntimeException e) {
					bulkImportResults.add(new ImportError(id, getErrorCode(e)));
				}
			}
		}
		return validAuthorizations;
	}

	private String getErrorCode(ImportedAuthorizationValidatorRuntimeException e) {
		if (e instanceof ImportedAuthorizationValidatorRuntimeException_InvalidRole) {
			return $(INVALID_ROLE);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_InvalidAccess) {
			return $(INVALID_ACCESS);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_UseOfAccessAndRole) {
			return $(USE_ROLE_OR_ACCESS);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_AuthorizationIDMissing) {
			return $(AUTHORIZATION_ID_MISSING);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_AuthorizationTargetsMissing) {
			return $(AUTHORIZATION_TARGETS_MISSING);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_AuthorizationPrincipalsMissing) {
			return $(AUTHORIZATION_PRINCIPALS_MISSING);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_InvalidTargetType) {
			return $(INVALID_TARGET_TYPE);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_InvalidPrincipalType) {
			return $(INVALID_PRINCIPAL_TYPE);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_EmptyLegacyId) {
			return $(EMPTY_LEGACY_ID);
		} else if (e instanceof ImportedAuthorizationValidatorRuntimeException_EmptyPrincipalId) {
			return $(EMPTY_PRINCIPAL_ID);
		} else {
			throw new RuntimeException("Unsupported exception");
		}
	}

	private Document getDocumentFromFile(File file) {
		SAXBuilder builder = new SAXBuilder();
		try {
			return builder.build(file);
		} catch (JDOMException e) {
			throw new RuntimeException("JDOM2 Exception", e);
		} catch (IOException e) {
			throw new RuntimeException("build Document JDOM2 from file", e);
		}
	}

}
