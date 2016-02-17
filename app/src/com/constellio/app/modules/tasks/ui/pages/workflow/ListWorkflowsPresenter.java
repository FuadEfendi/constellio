package com.constellio.app.modules.tasks.ui.pages.workflow;

import com.constellio.app.modules.tasks.TasksPermissionsTo;
import com.constellio.app.modules.tasks.model.wrappers.Workflow;
import com.constellio.app.modules.tasks.services.WorkflowServices;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

public class ListWorkflowsPresenter extends SingleSchemaBasePresenter<ListWorkflowsView> {
	private transient WorkflowServices workflowServices;

	public ListWorkflowsPresenter(ListWorkflowsView view) {
		super(view, Workflow.DEFAULT_SCHEMA);
	}

	public void addButtonClicked() {
		view.navigateTo().addWorkflow();
	}

	public void backButtonClicked() {
		view.navigateTo().adminModule();
	}

	public RecordVODataProvider getWorkflows() {
		MetadataSchemaVO schema = new MetadataSchemaToVOBuilder()
				.build(schema(Workflow.DEFAULT_SCHEMA), VIEW_MODE.TABLE, view.getSessionContext());
		return new RecordVODataProvider(schema, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return workflowServices().getWorkflowsQuery();
			}
		};
	}

	public void displayButtonClicked(RecordVO record) {
		view.navigateTo().displayWorkflow(record.getId());
	}

	public void editButtonClicked(RecordVO record) {
		view.navigateTo().editWorkflow(record.getId());
	}

	public void deleteButtonClicked(RecordVO record) {
		delete(toRecord(record), false);
		view.navigateTo().listWorkflows();
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return user.has(TasksPermissionsTo.MANAGE_WORKFLOWS).globally();
	}

	private WorkflowServices workflowServices() {
		if (workflowServices == null) {
			workflowServices = new WorkflowServices(view.getCollection(), appLayerFactory);
		}
		return workflowServices;
	}
}