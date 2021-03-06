package com.constellio.app.modules.tasks.ui.pages;

import static com.constellio.app.modules.tasks.model.wrappers.Task.ASSIGNEE;
import static com.constellio.app.modules.tasks.model.wrappers.Task.ASSIGNER;
import static com.constellio.app.modules.tasks.model.wrappers.Task.DUE_DATE;
import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.entities.records.wrappers.RecordWrapper.TITLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.model.wrappers.Workflow;
import com.constellio.app.modules.tasks.model.wrappers.WorkflowInstance;
import com.constellio.app.modules.tasks.navigation.TaskViews;
import com.constellio.app.modules.tasks.services.TaskPresenterServices;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.modules.tasks.services.TasksSearchServices;
import com.constellio.app.modules.tasks.services.WorkflowServices;
import com.constellio.app.modules.tasks.ui.builders.TaskToVOBuilder;
import com.constellio.app.modules.tasks.ui.components.TaskTable.TaskPresenter;
import com.constellio.app.modules.tasks.ui.components.WorkflowTable.WorkflowPresenter;
import com.constellio.app.modules.tasks.ui.entities.TaskVO;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

public class TaskManagementPresenter extends SingleSchemaBasePresenter<TaskManagementView>
		implements TaskPresenter, WorkflowPresenter {
	public static final String TASKS_ASSIGNED_BY_CURRENT_USER = "tasksAssignedByCurrentUser";
	public static final String TASKS_NOT_ASSIGNED = "nonAssignedTasks";
	public static final String TASKS_ASSIGNED_TO_CURRENT_USER = "tasksAssignedToCurrentUser";
	public static final String TASKS_RECENTLY_COMPLETED = "recentlyCompletedTasks";
	public static final String WORKFLOWS_STARTED = "startedWorkflows";

	private transient TasksSearchServices tasksSearchServices;
	private transient TaskPresenterServices taskPresenterServices;
	private transient WorkflowServices workflowServices;

	public TaskManagementPresenter(TaskManagementView view) {
		super(view, Task.DEFAULT_SCHEMA);
		initTransientObjects();
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	public List<String> getTabs() {

		List<String> tabs = new ArrayList<>();
		tabs.add(TASKS_ASSIGNED_TO_CURRENT_USER);
		tabs.add(TASKS_ASSIGNED_BY_CURRENT_USER);
		tabs.add(TASKS_NOT_ASSIGNED);
		tabs.add(TASKS_RECENTLY_COMPLETED);

		if (areWorkflowsEnabled()) {
			tabs.add(WORKFLOWS_STARTED);
		}

		return tabs;
	}

	public void tabSelected(String tabId) {
		if (isWorkflowTab(tabId)) {
			RecordVODataProvider provider = getWorkflowInstances(tabId);
			view.displayWorkflows(provider);
		} else {
			RecordVODataProvider provider = getTasks(tabId);
			view.displayTasks(provider);
		}
	}

	public void addTaskButtonClicked() {
		view.navigate().to(TaskViews.class).addTask();
	}

	public String getTabCaption(String tabId) {
		return $("TasksManagementView.tab." + tabId);
	}

	private void refreshCurrentTab() {
		view.reloadCurrentTab();
	}

	@Override
	public void displayButtonClicked(RecordVO record) {
		view.navigate().to(TaskViews.class).displayTask(record.getId());
	}

	@Override
	public void editButtonClicked(RecordVO record) {
		view.navigate().to().editTask(record.getId());
	}

	@Override
	public void deleteButtonClicked(RecordVO record) {
		taskPresenterServices.deleteTask(toRecord(record), getCurrentUser());
		view.reloadCurrentTab();
	}

	@Override
	public void completeButtonClicked(RecordVO record) {
		view.navigate().to().editTask(record.getId(), true);
	}

	@Override
	public void closeButtonClicked(RecordVO record) {
		taskPresenterServices.closeTask(toRecord(record), getCurrentUser());
		refreshCurrentTab();
	}

	@Override
	public boolean isTaskOverdue(TaskVO taskVO) {
		return taskPresenterServices.isTaskOverdue(taskVO);
	}

	@Override
	public boolean isFinished(TaskVO taskVO) {
		return taskPresenterServices.isFinished(taskVO);
	}

	@Override
	public void autoAssignButtonClicked(RecordVO recordVO) {
		taskPresenterServices.autoAssignTask(toRecord(recordVO), getCurrentUser());
		refreshCurrentTab();
	}

	@Override
	public boolean isAutoAssignButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isAutoAssignButtonEnabled(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isEditButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isEditTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isCompleteButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isCompleteTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isCloseButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isCloseTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isDeleteButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isDeleteTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public void displayWorkflowInstanceRequested(RecordVO recordVO) {
		view.navigate().to(TaskViews.class).displayWorkflowInstance(recordVO.getId());
	}

	@Override
	public void cancelWorkflowInstanceRequested(RecordVO record) {
		WorkflowInstance instance = new TasksSchemasRecordsServices(view.getCollection(), appLayerFactory)
				.getWorkflowInstance(record.getId());
		workflowServices.cancel(instance);
		refreshCurrentTab();
	}

	public RecordVODataProvider getWorkflows() {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder().build(
				schema(Workflow.DEFAULT_SCHEMA), VIEW_MODE.TABLE, view.getSessionContext());

		return new RecordVODataProvider(schemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return workflowServices.getWorkflowsQuery();
			}
		};
	}

	public void workflowStartRequested(RecordVO record) {
		Workflow workflow = new TasksSchemasRecordsServices(view.getCollection(), appLayerFactory).getWorkflow(record.getId());
		Map<String, List<String>> parameters = new HashMap<>();
		workflowServices.start(workflow, getCurrentUser(), parameters);
		refreshCurrentTab();
	}

	private RecordVODataProvider getTasks(String tabId) {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder()
				.build(defaultSchema(), VIEW_MODE.TABLE, getMetadataForTab(tabId), view.getSessionContext());

		switch (tabId) {
		case TASKS_ASSIGNED_TO_CURRENT_USER:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					return tasksSearchServices.getTasksAssignedToUserQuery(getCurrentUser());
				}
			};
		case TASKS_ASSIGNED_BY_CURRENT_USER:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					return tasksSearchServices.getTasksAssignedByUserQuery(getCurrentUser());
				}
			};
		case TASKS_NOT_ASSIGNED:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					return tasksSearchServices.getUnassignedTasksQuery(getCurrentUser());
				}
			};
		case TASKS_RECENTLY_COMPLETED:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					return tasksSearchServices.getRecentlyCompletedTasks(getCurrentUser());
				}
			};
		default:
			throw new RuntimeException("BUG: Unknown tabId + " + tabId);
		}
	}

	private RecordVODataProvider getWorkflowInstances(String tabId) {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder()
				.build(schema(WorkflowInstance.DEFAULT_SCHEMA), VIEW_MODE.TABLE, view.getSessionContext());

		switch (tabId) {
		case WORKFLOWS_STARTED:
			return new RecordVODataProvider(schemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					return workflowServices.getCurrentWorkflowInstancesQuery();
				}
			};
		default:
			throw new RuntimeException("BUG: Unknown tabId + " + tabId);
		}
	}

	private List<String> getMetadataForTab(String tabId) {
		switch (tabId) {
		case TASKS_ASSIGNED_TO_CURRENT_USER:
			return Arrays.asList(TITLE, ASSIGNER, DUE_DATE);
		case TASKS_ASSIGNED_BY_CURRENT_USER:
			return Arrays.asList(TITLE, ASSIGNEE, DUE_DATE);
		case TASKS_NOT_ASSIGNED:
			return Arrays.asList(TITLE, DUE_DATE);
		default:
			return Arrays.asList(TITLE, ASSIGNER, ASSIGNEE, DUE_DATE);
		}
	}

	private boolean isWorkflowTab(String tabId) {
		return WORKFLOWS_STARTED.equals(tabId);
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
		TasksSchemasRecordsServices schemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
		workflowServices = new WorkflowServices(collection, appLayerFactory);
		tasksSearchServices = new TasksSearchServices(schemas);
		taskPresenterServices = new TaskPresenterServices(
				schemas, recordServices(), tasksSearchServices, modelLayerFactory.newLoggingServices());
	}

	public boolean areWorkflowsEnabled() {
		RMConfigs configs = new RMConfigs(modelLayerFactory.getSystemConfigurationsManager());
		return configs.areWorkflowsEnabled();
	}
}
