package com.constellio.app.modules.rm.ui.pages.containers.edit;

import static com.constellio.app.ui.i18n.i18n.$;

import org.apache.commons.lang3.StringUtils;

import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningSecurityService;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.type.ContainerRecordType;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemasRuntimeException;
import com.constellio.model.entities.schemas.entries.DataEntryType;

public class AddEditContainerPresenter extends SingleSchemaBasePresenter<AddEditContainerView> {
	protected RecordVO container;
	protected boolean editMode;

	public AddEditContainerPresenter(AddEditContainerView view) {
		super(view, ContainerRecord.DEFAULT_SCHEMA);
	}

	public AddEditContainerPresenter forParams(String parameters) {
		editMode = StringUtils.isNotBlank(parameters);
		Record container = editMode ? getRecord(parameters) : newContainerRecord();
		this.container = new RecordToVOBuilder().build(container, VIEW_MODE.FORM, view.getSessionContext());
		return this;
	}

	public RecordVO getContainerRecord() {
		return container;
	}

	public void typeSelected(String type) {
		String newSchemaCode = getLinkedSchemaCodeOf(type);
		if (container.getSchema().getCode().equals(newSchemaCode)) {
			return;
		}
		if (editMode) {
			view.setType(container.<String>get(ContainerRecord.TYPE));
			view.showErrorMessage($("AddEditContainerView.cannotChangeSchema"));
			return;
		}
		setSchemaCode(newSchemaCode);
		container = copyMetadataToSchema(view.getUpdatedContainer(), newSchemaCode);
		view.reloadWithContainer(container);
	}

	public boolean canEditAdministrativeUnit() {
		return !editMode;
	}

	public boolean canEditDecommissioningType() {
		return !editMode;
	}

	public void saveButtonClicked(RecordVO record) {
		addOrUpdate(toRecord(record));
		view.navigate().to(RMViews.class).displayContainer(record.getId());
	}

	public void cancelRequested() {
		view.navigate().to().previousView();
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		DecommissioningSecurityService securityServices = new DecommissioningSecurityService(collection, modelLayerFactory);
		return securityServices.canCreateContainers(user);
	}

	protected Record newContainerRecord() {
		return recordServices().newRecordWithSchema(schema(ContainerRecord.DEFAULT_SCHEMA));
	}

	private RecordVO copyMetadataToSchema(RecordVO record, String schemaCode) {
		MetadataSchema schema = schema(schemaCode);
		Record container = recordServices().newRecordWithSchema(schema, record.getId());
		for (MetadataVO metadataVO : record.getMetadatas()) {
			String localCode = metadataVO.getLocalCode();
			try {
				Metadata metadata = schema.getMetadata(localCode);
				if (metadata.getDataEntry().getType() == DataEntryType.MANUAL) {
					container.set(metadata, record.get(metadataVO));
				}
			} catch (MetadataSchemasRuntimeException.NoSuchMetadata e) {
				// Do nothing
			}
		}
		return new RecordToVOBuilder().build(container, VIEW_MODE.FORM, view.getSessionContext());
	}

	private String getLinkedSchemaCodeOf(String id) {
		ContainerRecordType type = new RMSchemasRecordsServices(view.getCollection(), appLayerFactory).getContainerRecordType(id);
		return StringUtils.defaultIfBlank(type.getLinkedSchema(), ContainerRecord.DEFAULT_SCHEMA);
	}
	
}
