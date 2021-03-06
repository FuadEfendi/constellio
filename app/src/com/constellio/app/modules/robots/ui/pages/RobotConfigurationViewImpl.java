package com.constellio.app.modules.robots.ui.pages;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.constellio.app.modules.robots.ui.components.breadcrumb.RobotBreadcrumbTrail;
import com.constellio.app.modules.robots.ui.data.RobotLazyTreeDataProvider;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.buttons.AddButton;
import com.constellio.app.ui.framework.buttons.DeleteButton;
import com.constellio.app.ui.framework.buttons.EditButton;
import com.constellio.app.ui.framework.buttons.LinkButton;
import com.constellio.app.ui.framework.components.RecordDisplay;
import com.constellio.app.ui.framework.components.ReportViewer.DownloadStreamResource;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.components.tree.RecordLazyTree;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

public class RobotConfigurationViewImpl extends BaseViewImpl implements RobotConfigurationView {
	private final RobotConfigurationPresenter presenter;
	private RecordVO robot;

	public RobotConfigurationViewImpl() {
		this.presenter = new RobotConfigurationPresenter(this);
	}

	@Override
	protected void initBeforeCreateComponents(ViewChangeEvent event) {
		robot = presenter.forParams(event.getParameters()).getRootRobot();
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected BaseBreadcrumbTrail buildBreadcrumbTrail() {
		return new RobotBreadcrumbTrail(presenter.getRootRobotId());
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		TabSheet sheet = new TabSheet();
		sheet.setWidth("100%");

		RecordDisplay display = new RecordDisplay(robot);
		sheet.addTab(display, $("RobotConfigurationView.metadata"));

		RobotLazyTreeDataProvider provider = new RobotLazyTreeDataProvider(
				getConstellioFactories().getAppLayerFactory(), getCollection(), presenter.getRootRobotId());
		RecordLazyTree tree = new RecordLazyTree(provider);
		tree.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseButton.LEFT) {
					String robotId = (String) event.getItemId();
					presenter.robotNavigationRequested(robotId);
				}
			}
		});
		sheet.addTab(tree, $("RobotConfigurationView.tree"));

		return sheet;
	}

	@Override
	protected List<Button> buildActionMenuButtons(ViewChangeEvent event) {
		List<Button> buttons = super.buildActionMenuButtons(event);
		buttons.add(buildExecuteButton());
		buttons.add(buildAddButton());
		buttons.add(buildEditButton());
		buttons.add(buildDeleteButton());
		buttons.add(buildLogsButton());
		buttons.add(buildDownloadButton());
		buttons.add(buildDeleteRecordsButton());
		return buttons;
	}

	private Button buildExecuteButton() {
		LinkButton button = new LinkButton($("RobotConfigurationView.executeRobot")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.executeButtonClicked(robot);
			}
		};
		button.setVisible(presenter.canExecute(robot));
		return button;
	}

	private Button buildAddButton() {
		return new AddButton($("RobotConfigurationView.addSubRobot")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.addButtonClicked(robot);
			}
		};
	}

	private Button buildEditButton() {
		return new EditButton($("RobotConfigurationView.editRobot")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.editButtonClicked(robot);
			}
		};
	}

	private DeleteButton buildDeleteButton() {
		return new DeleteButton($("RobotConfigurationView.deleteRobot")) {
			@Override
			protected void confirmButtonClick(ConfirmDialog dialog) {
				presenter.deleteButtonClicked(robot);
			}
		};
	}

	private Button buildLogsButton() {
		return new LinkButton($("RobotConfigurationView.viewLogs")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.viewLogsButtonClicked();
			}
		};
	}

	private Button buildDownloadButton() {
		final Resource resource = new DownloadStreamResource(presenter.getResource(), presenter.getReportTitle());
		return new LinkButton($("RobotConfigurationView.download")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				Page.getCurrent().open(resource, null, false);
			}
		};
	}

	private DeleteButton buildDeleteRecordsButton() {
		return new DeleteButton($("RobotConfigurationView.deleteRecords")) {
			@Override
			protected void confirmButtonClick(ConfirmDialog dialog) {
				presenter.deleteRecordsButtonClicked(robot);
			}
		};
	}

	@Override
	protected ClickListener getBackButtonClickListener() {
		return new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.backButtonClicked();
			}
		};
	}
}