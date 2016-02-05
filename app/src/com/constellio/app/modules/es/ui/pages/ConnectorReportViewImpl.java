package com.constellio.app.modules.es.ui.pages;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.components.BaseDisplay;
import com.constellio.app.ui.framework.components.BaseDisplay.CaptionAndComponent;
import com.constellio.app.ui.framework.components.fields.BaseTextField;
import com.constellio.app.ui.framework.components.table.BasePagedTable;
import com.constellio.app.ui.framework.containers.RecordVOWithDistinctSchemaTypesLazyContainer;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ConnectorReportViewImpl extends BaseViewImpl implements ConnectorReportView {

	private ConnectorReportPresenter presenter;
	private BasePagedTable<RecordVOWithDistinctSchemaTypesLazyContainer> table;
	private HorizontalLayout tableControls;
	private BaseTextField filterField;
	private VerticalLayout mainLayout;

	public ConnectorReportViewImpl() {
		presenter = new ConnectorReportPresenter(this);
	}

	@Override
	protected void initBeforeCreateComponents(ViewChangeEvent event) {
		super.initBeforeCreateComponents(event);
		presenter.forParams(event.getParameters());
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		mainLayout = new VerticalLayout();
		BaseDisplay statsDisplay = buildStatsDisplay();
		HorizontalLayout filterComponent = buildFilterComponent();
		table = buildTable();
		table.setColumnHeader("url", $("ConnectorReportView.url"));
		table.setColumnHeader("fetchedDateTime", $("ConnectorReportView.fetchedDateTime"));
		table.setColumnHeader("errorCode", $("ConnectorReportView.errorCode"));
		table.setColumnHeader("errorMessage", $("ConnectorReportView.errorMessage"));
		table.setColumnHeader("subject", $("ConnectorReportView.subject"));
		table.setColumnHeader("downloadTime", $("ConnectorReportView.downloadTime"));
		table.setColumnHeader("copyOf", $("ConnectorReportView.copyOf"));
		table.setColumnHeader("title", $("ConnectorReportView.title"));
		table.setColumnHeader("type", $("ConnectorReportView.type"));
		tableControls = table.createControls();
		mainLayout.addComponents(statsDisplay, filterComponent, table, tableControls);
		return mainLayout;
	}

	private HorizontalLayout buildFilterComponent() {
		HorizontalLayout filterComponent = new HorizontalLayout();
		filterField = new BaseTextField();
		BaseButton filterButton = new BaseButton($("ConnectorReportView.filterButton")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.filterButtonClicked();
			}
		};
		filterComponent.addComponents(filterField, filterButton);
		return filterComponent;
	}

	private BaseDisplay buildStatsDisplay() {
		List<CaptionAndComponent> components = new ArrayList<>();
		components.add(new CaptionAndComponent(new Label($("ConnectorReportView.totalDocsFound")),
				new Label(presenter.getTotalDocumentsCount().toString())));
		components.add(new CaptionAndComponent(new Label($("ConnectorReportView.fetchedDocsFound")),
				new Label(presenter.getFetchedDocumentsCount().toString())));
		components.add(new CaptionAndComponent(new Label($("ConnectorReportView.unfetchedDocsFound")),
				new Label(presenter.getUnfetchedDocumentsCount().toString())));
		return new BaseDisplay(components);
	}

	private BasePagedTable buildTable() {
		RecordVOWithDistinctSchemaTypesLazyContainer container = new RecordVOWithDistinctSchemaTypesLazyContainer(
				presenter.getDataProvider(), presenter.getReportMetadataList());
		table = new BasePagedTable<>(container);
		table.setContainerDataSource(container);
		table.setWidth("100%");
		return table;
	}

	@Override
	public void filterTable() {
		RecordVOWithDistinctSchemaTypesLazyContainer container = new RecordVOWithDistinctSchemaTypesLazyContainer(
				presenter.getFilteredDataProvider(filterField.getValue()), presenter.getReportMetadataList());
		BasePagedTable<RecordVOWithDistinctSchemaTypesLazyContainer> newTable = new BasePagedTable<>(container);
		newTable.setContainerDataSource(container);
		newTable.setWidth("100%");
		newTable.setColumnHeader("url", $("ConnectorReportView.url"));
		newTable.setColumnHeader("fetchedDateTime", $("ConnectorReportView.fetchedDateTime"));
		newTable.setColumnHeader("errorCode", $("ConnectorReportView.errorCode"));
		newTable.setColumnHeader("errorMessage", $("ConnectorReportView.errorMessage"));
		HorizontalLayout newTableControls = newTable.createControls();
		mainLayout.replaceComponent(table, newTable);
		mainLayout.replaceComponent(tableControls, newTableControls);
		table = newTable;
		tableControls = newTableControls;
	}

	@Override
	protected String getTitle() {
		return $("ConnectorReportView.viewTitle");
	}
}