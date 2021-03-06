package com.constellio.app.ui.pages.management.authorizations;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.model.entities.CorePermissions;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Group;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.entities.security.CustomizedAuthorizationsBehavior;
import com.constellio.model.entities.security.Role;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.SchemasRecordsServices;

public class ListPrincipalAccessAuthorizationsPresenter extends ListAuthorizationsPresenter {
	public ListPrincipalAccessAuthorizationsPresenter(ListPrincipalAccessAuthorizationsView view) {
		super(view);
	}

	@Override
	public void backButtonClicked(String schemaCode) {
		if (schemaCode.equals(Group.DEFAULT_SCHEMA)) {
			view.navigate().to().displayCollectionGroup(recordId);
		} else {
			view.navigate().to().displayCollectionUser(recordId);
		}
	}

	@Override
	public boolean isDetacheable() {
		return false;
	}

	@Override
	public boolean isAttached() {
		return true;
	}

	@Override
	public List<String> getAllowedAccesses() {
		return asList(Role.READ, Role.WRITE, Role.DELETE);
	}

	@Override
	protected boolean isOwnAuthorization(Authorization authorization) {
		return authorization.getGrantedToPrincipals().contains(recordId);
	}

	@Override
	protected void removeAuthorization(Authorization authorization) {
		if (authorization.getGrantedToPrincipals().size() == 1) {
			authorizationsServices().delete(authorization.getDetail(), getCurrentUser());
		} else {
			authorization.getGrantedToPrincipals().remove(recordId);
			authorizationsServices().modify(authorization, CustomizedAuthorizationsBehavior.KEEP_ATTACHED, getCurrentUser());
		}
	}

	@Override
	public boolean seeRolesField() {
		return false;
	}

	@Override
	public boolean seeAccessField() {
		return true;
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return user.has(CorePermissions.MANAGE_SECURITY).globally();
	}

	public void accessCreationRequested(List<String> access) {
		SchemasRecordsServices schemas = new SchemasRecordsServices(collection, modelLayerFactory);
		User user = schemas.getUser(recordId);

		user.setCollectionReadAccess(!access.isEmpty());
		user.setCollectionWriteAccess(access.contains(Role.WRITE));
		user.setCollectionDeleteAccess(access.contains(Role.DELETE));

		try {
			recordServices().update(user);
		} catch (RecordServicesException e) {
			throw new ImpossibleRuntimeException(e);
		}
		view.refresh();

	}

	public boolean seeCollectionAccessField() {
		return isAUser() && !getCollectionAccessChoicesModifiableByCurrentUser().isEmpty();
	}

	public List<String> getCollectionAccessChoicesModifiableByCurrentUser() {
		if (getCurrentUser().has(CorePermissions.MANAGE_SECURITY).globally()) {
			return asList(Role.READ, Role.WRITE, Role.DELETE);
		} else {
			return new ArrayList<>();
		}
	}

	public boolean isAUser() {
		Record record = recordServices().getDocumentById(recordId);
		return record.getSchemaCode().startsWith(User.SCHEMA_TYPE);
	}

	public boolean isASystemUser() {
		Record record = recordServices().getDocumentById(recordId);
		if (record.getSchemaCode().startsWith(User.SCHEMA_TYPE)) {
			SchemasRecordsServices schemas = new SchemasRecordsServices(collection, modelLayerFactory);
			User user = schemas.getUser(recordId);
			return user.isSystemAdmin();
		} else {
			return false;
		}
	}

	public List<String> getUserGlobalAccess() {
		List<String> globalAccess = new ArrayList<>();
		SchemasRecordsServices schemas = new SchemasRecordsServices(collection, modelLayerFactory);
		if (isAUser()) {
			User user = schemas.getUser(recordId);
			if (user.hasCollectionReadAccess()) {
				globalAccess.add(Role.READ);
			}
			if (user.hasCollectionWriteAccess()) {
				globalAccess.add(Role.WRITE);
			}
			if (user.hasCollectionDeleteAccess()) {
				globalAccess.add(Role.DELETE);
			}
		}
		return globalAccess;
	}
}
