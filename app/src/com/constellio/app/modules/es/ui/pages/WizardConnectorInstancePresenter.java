package com.constellio.app.modules.es.ui.pages;

import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.modules.es.connectors.ConnectorUtilsServices;
import com.constellio.app.modules.es.model.connectors.ConnectorInstance;
import com.constellio.app.modules.es.model.connectors.ConnectorType;
import com.constellio.app.modules.es.model.connectors.RegisteredConnector;
import com.constellio.app.modules.es.navigation.ESViews;
import com.constellio.app.modules.es.services.ConnectorManager;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;

public class WizardConnectorInstancePresenter extends AddEditConnectorInstancePresenter {
	public static final String SELECT_CONNECTOR_TYPE_TAB = "selectConnectorType";
	public static final String ADD_CONNECTOR_INSTANCE = "addConnectorInstance";

	public WizardConnectorInstancePresenter(WizardConnectorInstanceView view) {
		super(view);
	}

	@Override
	public void forParams(String params) {
		RecordVODataProvider connectorTypeDataProvider = getConnectorTypeDataProvider();
		((WizardConnectorInstanceView) view).setConnectorTypeDataProvider(connectorTypeDataProvider);
	}

	RecordVODataProvider getConnectorTypeDataProvider() {
		List<String> metadataCodes = new ArrayList<>();
		MetadataSchemaToVOBuilder schemaVOBuilder = new MetadataSchemaToVOBuilder();
		SessionContext sessionContext = view.getSessionContext();
		RecordToVOBuilder voBuilder = new RecordToVOBuilder();
		MetadataSchemaVO schemaVO = schemaVOBuilder
				.build(schema(ConnectorType.DEFAULT_SCHEMA), VIEW_MODE.TABLE, metadataCodes, sessionContext);
		return new RecordVODataProvider(schemaVO, voBuilder, modelLayerFactory, sessionContext) {
			@Override
			protected LogicalSearchQuery getQuery() {
				LogicalSearchCondition condition = from(schemaType(ConnectorType.SCHEMA_TYPE)).returnAll();
				return new LogicalSearchQuery().setCondition(condition).sortAsc(Schemas.SCHEMA);
			}
		};
	}

	@Override
	public void cancelButtonClicked() {
		view.navigate().to(ESViews.class).listConnectorInstances();
	}

	@Override
	public void saveButtonClicked(RecordVO recordVO) {
		Record record = toRecord(recordVO);
		ConnectorInstance<?> connectorInstance = esSchemasRecordsServices.wrapConnectorInstance(record);
		esSchemasRecordsServices.getConnectorManager().createConnector(connectorInstance);
		view.navigate().to(ESViews.class).displayConnectorInstance(connectorInstance.getId());
	}

	public void connectorTypeSelected(String id) {
		setConnectorTypeId(id);
		Record record;
		if (connectorTypeId != null) {
			ConnectorType connectorType = esSchemasRecordsServices.getConnectorType(connectorTypeId);
			ConnectorInstance<?> connectorInstance = null;
			ConnectorManager connectorManager = esSchemasRecordsServices.getConnectorManager();

			for (RegisteredConnector connector : connectorManager.getRegisteredConnectors()) {
				if (connector.getConnectorCode().equals(connectorType.getCode())) {
					ConnectorUtilsServices<?> services = connector.getServices();
					connectorInstance = services.newConnectorInstance();
				}
			}
			if (connectorInstance == null) {
				throw new ImpossibleRuntimeException(
						"Invalid connector type/or ConnectorUtilsServices returns null when call of newConnectorInstance");
			}

			record = connectorInstance.getWrappedRecord();
			setRecordVO(voBuilder.build(record, VIEW_MODE.FORM, view.getSessionContext()));
			setCurrentSchemaCode(recordVO.getSchema().getCode());
			setSchemaCode(currentSchemaCode);
			view.setRecordVO(recordVO);
		} else {
			view.setRecordVO(null);
		}
		((WizardConnectorInstanceView) view).refreshConnectorForm();
	}

	@Override
	public String getTitle() {
		return $("WizardConnectorInstanceView.viewTitle");
	}
}
