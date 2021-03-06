package com.constellio.app.ui.framework.components.table;

import static com.constellio.app.ui.i18n.i18n.$;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class BasePagedTable<T extends Container> extends PagedTable {

	public static final int DEFAULT_PAGE_LENGTH = 10;
	
	protected T container;
	protected ComboBox itemsPerPageField;

	public BasePagedTable(T container) {
		this.container = container;
		itemsPerPageField = new ComboBox();
	}

	@Override
	public HorizontalLayout createControls() {
		HorizontalLayout pageSize;

		Label itemsPerPageLabel = new Label($("SearchResultTable.itemsPerPage"));
		itemsPerPageField.addItem(DEFAULT_PAGE_LENGTH);
		if (container.size() >= 10) {
			itemsPerPageField.addItem(10);
		}
		if (container.size() >= 25) {
			itemsPerPageField.addItem(25);
		}
		if (container.size() >= 50) {
			itemsPerPageField.addItem(50);
		}
		if (container.size() >= 100) {
			itemsPerPageField.addItem(100);
		}
		itemsPerPageField.setNullSelectionAllowed(false);
		itemsPerPageField.setWidth("85px");

		itemsPerPageField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				setPageLength((int) itemsPerPageField.getValue());
			}
		});
		itemsPerPageField.setEnabled(itemsPerPageField.size() > 1);

		pageSize = new HorizontalLayout(itemsPerPageLabel, itemsPerPageField);
		pageSize.setComponentAlignment(itemsPerPageLabel, Alignment.MIDDLE_LEFT);
		pageSize.setComponentAlignment(itemsPerPageField, Alignment.MIDDLE_LEFT);
		pageSize.setSpacing(true);

		Label page = new Label($("SearchResultTable.page"));
		final TextField currentPage = new TextField();
		currentPage.setConverter(Integer.class);
		currentPage.setConvertedValue(getCurrentPage());
		currentPage.setWidth("45px");
		currentPage.addValidator(
				new IntegerRangeValidator("Wrong page number", 1, getTotalAmountOfPages()));
		currentPage.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				if (currentPage.isValid() && currentPage.getValue() != null) {
					setCurrentPage((int) currentPage.getConvertedValue());
				}
			}
		});
		currentPage.setEnabled(getTotalAmountOfPages() > 1);

		Label separator = new Label($("SearchResultTable.of"));
		final Label totalPages = new Label(String.valueOf(getTotalAmountOfPages()));

		final Button first = new Button("\uF100", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				setCurrentPage(0);
			}
		});
		first.setStyleName(ValoTheme.BUTTON_LINK);
		first.setEnabled(getCurrentPage() > 1);

		final Button previous = new Button("\uF104", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				previousPage();
			}
		});
		previous.setStyleName(ValoTheme.BUTTON_LINK);
		previous.setEnabled(getCurrentPage() > 1);

		final Button next = new Button("\uF105", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				nextPage();
			}
		});
		next.setStyleName(ValoTheme.BUTTON_LINK);
		next.setEnabled(getCurrentPage() < getTotalAmountOfPages());

		final Button last = new Button("\uF101", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				setCurrentPage(getTotalAmountOfPages());
			}
		});
		last.setStyleName(ValoTheme.BUTTON_LINK);
		last.setEnabled(getCurrentPage() < getTotalAmountOfPages());

		HorizontalLayout pageManagement = new HorizontalLayout(
				first, previous, page, currentPage, separator, totalPages, next, last);
		pageManagement.setComponentAlignment(first, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(page, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(currentPage, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(separator, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(totalPages, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(next, Alignment.MIDDLE_LEFT);
		pageManagement.setComponentAlignment(last, Alignment.MIDDLE_LEFT);
		pageManagement.setSpacing(true);

		HorizontalLayout controlBar = new HorizontalLayout(pageSize, pageManagement);
		controlBar.setComponentAlignment(pageManagement, Alignment.MIDDLE_CENTER);
		controlBar.setExpandRatio(pageSize, 1);
		controlBar.setWidth("100%");

		addListener(new PageChangeListener() {
			public void pageChanged(PagedTableChangeEvent event) {
				first.setEnabled(getCurrentPage() > 1);
				previous.setEnabled(getCurrentPage() > 1);
				next.setEnabled(getCurrentPage() < getTotalAmountOfPages());
				last.setEnabled(getCurrentPage() < getTotalAmountOfPages());
				currentPage.setValue(String.valueOf(getCurrentPage()));
				currentPage.setEnabled(getTotalAmountOfPages() > 1);
				totalPages.setValue(String.valueOf(getTotalAmountOfPages()));
			}
		});

		return controlBar;
	}

	public ComboBox getItemsPerPageField() {
		return itemsPerPageField;
	}

	public void setItemsPerPageValue(int value) {
		itemsPerPageField.setValue(value);
	}
}
