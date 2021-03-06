package com.constellio.app.modules.tasks.navigation;

import java.io.Serializable;

import com.constellio.app.entities.navigation.NavigationConfig;
import com.constellio.app.entities.navigation.NavigationItem;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.tasks.TasksPermissionsTo;
import com.constellio.app.modules.tasks.ui.pages.TaskManagementViewImpl;
import com.constellio.app.modules.tasks.ui.pages.TasksLogsViewImpl;
import com.constellio.app.modules.tasks.ui.pages.tasks.AddEditTaskViewImpl;
import com.constellio.app.modules.tasks.ui.pages.tasks.DisplayTaskViewImpl;
import com.constellio.app.modules.tasks.ui.pages.viewGroups.TasksViewGroup;
import com.constellio.app.modules.tasks.ui.pages.workflow.AddEditWorkflowViewImpl;
import com.constellio.app.modules.tasks.ui.pages.workflow.DisplayWorkflowViewImpl;
import com.constellio.app.modules.tasks.ui.pages.workflow.ListWorkflowsViewImpl;
import com.constellio.app.modules.tasks.ui.pages.workflowInstance.DisplayWorkflowInstanceViewImpl;
import com.constellio.app.ui.application.Navigation;
import com.constellio.app.ui.application.NavigatorConfigurationService;
import com.constellio.app.ui.framework.components.ComponentState;
import com.constellio.app.ui.pages.base.MainLayout;
import com.constellio.app.ui.pages.home.HomeView;
import com.constellio.app.ui.pages.management.AdminView;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.factories.ModelLayerFactory;

public class TasksNavigationConfiguration implements Serializable {
	public static final String TASK_MANAGEMENT = "taskManagement";
	public static final String ADD_TASK = "addTask";
	public static final String WORKFLOW_MANAGEMENT = "workflowManagement";
	public static final String WORKFLOW_MANAGEMENT_ICON = "images/icons/config/workflows.png";
    public static final String EDIT_TASK = "editTask";
    public static final String DISPLAY_TASK = "displayTask";
    public static final String ADD_WORKFLOW = "addWorkflow";
    public static final String EDIT_WORKFLOW = "editWorkflow";
    public static final String DISPLAY_WORKFLOW = "displayWorkflow";
    public static final String LIST_WORKFLOWS = "listWorkflows";
    public static final String LIST_TASKS_LOGS = "listTaksLogs";
    public static final String DISPLAY_WORKFLOW_INSTANCE = "displayWorkflowInstance";

    public static void configureNavigation(NavigationConfig config) {
		configureMainLayoutNavigation(config);
		configureHomeActionMenu(config);
		configureCollectionAdmin(config);
	}

    public static void configureNavigation(NavigatorConfigurationService service) {
        service.register(ADD_TASK, AddEditTaskViewImpl.class);
        service.register(EDIT_TASK, AddEditTaskViewImpl.class);
        service.register(DISPLAY_TASK, DisplayTaskViewImpl.class);
        service.register(ADD_WORKFLOW, AddEditWorkflowViewImpl.class);
        service.register(EDIT_WORKFLOW, AddEditWorkflowViewImpl.class);
        service.register(DISPLAY_WORKFLOW, DisplayWorkflowViewImpl.class);
        service.register(LIST_WORKFLOWS, ListWorkflowsViewImpl.class);
        service.register(TASK_MANAGEMENT, TaskManagementViewImpl.class);
        service.register(LIST_TASKS_LOGS, TasksLogsViewImpl.class);
        service.register(DISPLAY_WORKFLOW_INSTANCE, DisplayWorkflowInstanceViewImpl.class);
    }

    private static void configureHomeActionMenu(NavigationConfig config) {
		config.add(HomeView.ACTION_MENU, new NavigationItem.Active(ADD_TASK) {
			@Override
			public void activate(Navigation navigate) {
				navigate.to(TaskViews.class).addTask();
			}

			@Override
			public ComponentState getStateFor(User user, ModelLayerFactory modelLayerFactory) {
				return ComponentState.ENABLED;
			}
		});
	}

	private static void configureMainLayoutNavigation(NavigationConfig config) {
		config.add(MainLayout.MAIN_LAYOUT_NAVIGATION, new NavigationItem.Active(TASK_MANAGEMENT, TasksViewGroup.class) {
			@Override
			public void activate(Navigation navigate) {
				navigate.to(TaskViews.class).taskManagement();
			}

			@Override
			public int getOrderValue() {
				return 30;
			}

			@Override
			public ComponentState getStateFor(User user, ModelLayerFactory modelLayerFactory) {
				return ComponentState.ENABLED;
			}
		});
	}

	private static void configureCollectionAdmin(NavigationConfig config) {
		config.add(AdminView.COLLECTION_SECTION, new NavigationItem.Active(WORKFLOW_MANAGEMENT, WORKFLOW_MANAGEMENT_ICON) {
			@Override
			public void activate(Navigation navigate) {
				navigate.to(TaskViews.class).listWorkflows();
			}

			@Override
			public ComponentState getStateFor(User user, ModelLayerFactory modelLayerFactory) {

				RMConfigs configs = new RMConfigs(modelLayerFactory.getSystemConfigurationsManager());
				if (!configs.areWorkflowsEnabled()) {
					return ComponentState.INVISIBLE;
				}

				return ComponentState.visibleIf(user.has(TasksPermissionsTo.MANAGE_WORKFLOWS).globally());
			}
		});
	}
}
