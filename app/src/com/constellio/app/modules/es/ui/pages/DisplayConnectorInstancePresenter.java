package com.constellio.app.modules.es.ui.pages;

import static com.constellio.app.ui.i18n.i18n.$;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.modules.es.model.connectors.ConnectorDocument;
import com.constellio.app.modules.es.model.connectors.ConnectorInstance;
import com.constellio.app.modules.es.services.ESSchemasRecordsServices;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.CorePermissions;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.users.UserServices;

public class DisplayConnectorInstancePresenter extends BasePresenter<DisplayConnectorInstanceView> {
	private static Logger LOGGER = LoggerFactory.getLogger(DisplayConnectorInstancePresenter.class);
	private RecordToVOBuilder voBuilder = new RecordToVOBuilder();
	private RecordVO recordVO;
	private ConnectorInstance connectorInstance;
	private final int NUMBER_OF_DOCS = 10;

	private transient ESSchemasRecordsServices esSchemasRecordsServices;
	private transient UserServices userServices;
	private transient RecordServices recordServices;

	public DisplayConnectorInstancePresenter(DisplayConnectorInstanceView view) {
		super(view);

		ConstellioFactories constellioFactories = view.getConstellioFactories();
		SessionContext sessionContext = view.getSessionContext();
		initTransientObjects();
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
		esSchemasRecordsServices = new ESSchemasRecordsServices(collection, appLayerFactory);
		recordServices = modelLayerFactory.newRecordServices();
		userServices = modelLayerFactory.newUserServices();
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return userServices.has(user).globalPermissionInAnyCollection(CorePermissions.MANAGE_CONNECTORS);
	}

	public void forParams(String params) {
		Record record = presenterService().getRecord(params);
		this.recordVO = voBuilder.build(record, VIEW_MODE.DISPLAY, view.getSessionContext());
		view.setRecord(recordVO);
		connectorInstance = esSchemasRecordsServices.getConnectorInstance(recordVO.getId());
	}

	@Override
	protected boolean hasRestrictedRecordAccess(String params, User user, Record restrictedRecord) {
		return userServices.has(user).globalPermissionInAnyCollection(CorePermissions.MANAGE_CONNECTORS);
	}

	@Override
	protected List<String> getRestrictedRecordIds(String params) {
		return asList(recordVO.getId());
	}

	public void viewAssembled() {
		updateDocumentsInfo();
	}

	public void backButtonClicked() {
		view.navigateTo().listConnectorInstances();
	}

	public void editConnectorInstanceButtonClicked() {
		view.navigateTo().editConnectorInstances(recordVO.getId());
	}

	public String getTitle() {
		return $("DisplayConnectorInstanceView.viewTitle") + " " + recordVO.getTitle();
	}

	String getLastDocuments() {
		StringBuilder result = new StringBuilder();
		List<String> urls = new ArrayList<>();
		List<ConnectorDocument<?>> documents = esSchemasRecordsServices.getConnectorManager()
				.getLastFetchedDocuments(connectorInstance.getId(), NUMBER_OF_DOCS);
		for (ConnectorDocument<?> document : documents) {
			urls.add(document.getURL());
		}
		for (String url : urls) {
			result.append(url);
			result.append(System.getProperty("line.separator"));
		}
		return result.toString();
	}

	public boolean isStartButtonVisible() {
		return !connectorInstance.isEnabled();
	}

	public boolean isStopButtonVisible() {
		return !isStartButtonVisible();
	}

	public void start() {
		connectorInstance.setEnabled(true);
		try {
			recordServices.update(connectorInstance.getWrappedRecord());
			view.navigateTo().displayConnectorInstance(recordVO.getId());
		} catch (RecordServicesException e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		connectorInstance.setEnabled(false);
		try {
			recordServices.update(connectorInstance.getWrappedRecord());
			view.navigateTo().displayConnectorInstance(recordVO.getId());
		} catch (RecordServicesException e) {
			throw new RuntimeException(e);
		}
	}

	public RecordVO getRecordVO() {
		return recordVO;
	}

	private long getFetchedDocumentsCount() {
		return esSchemasRecordsServices.getConnectorManager().getFetchedDocumentsCount(connectorInstance.getId());
	}

	public void editSchemasButtonClicked() {
		view.navigateTo().displayConnectorMappings(recordVO.getId());
	}

	public void backgroundViewMonitor() {
		updateDocumentsInfo();
	}

	private void updateDocumentsInfo() {
		long fetchedDocumentsCount = getFetchedDocumentsCount();
		String lastDocuments = getLastDocuments();
		view.setDocumentsCount(fetchedDocumentsCount);
		view.setLastDocuments(lastDocuments);
	}

	public void indexationReportButtonClicked() {
		try {
			view.navigateTo().connectorIndexationReport(connectorInstance.getId());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void errorsReportButtonClicked() {
		view.navigateTo().connectorErrorsReport(connectorInstance.getId());
	}

	public void deleteDocumentsButtonClicked() {
		esSchemasRecordsServices.getConnectorManager()
				.totallyDeleteConnectorRecordsSkippingValidation(modelLayerFactory.getDataLayerFactory().newRecordDao(),
						connectorInstance);
		view.navigateTo().displayConnectorInstance(recordVO.getId());
	}
}