package com.constellio.app.modules.rm.ui.pages.folder;

import static com.constellio.app.ui.framework.buttons.WindowButton.WindowConfiguration.modalDialog;
import static com.constellio.app.ui.i18n.i18n.$;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.modules.rm.model.labelTemplate.LabelTemplate;
import com.constellio.app.modules.rm.services.borrowingServices.BorrowingType;
import com.constellio.app.modules.rm.ui.components.RMMetadataDisplayFactory;
import com.constellio.app.modules.rm.ui.components.breadcrumb.FolderDocumentBreadcrumbTrail;
import com.constellio.app.modules.rm.ui.components.content.DocumentContentVersionWindowImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.LookupFolderField;
import com.constellio.app.modules.rm.ui.entities.DocumentVO;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.buttons.AddButton;
import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.buttons.DeleteWithJustificationButton;
import com.constellio.app.ui.framework.buttons.DisplayButton;
import com.constellio.app.ui.framework.buttons.DownloadLink;
import com.constellio.app.ui.framework.buttons.EditButton;
import com.constellio.app.ui.framework.buttons.IconButton;
import com.constellio.app.ui.framework.buttons.LabelsButton;
import com.constellio.app.ui.framework.buttons.LabelsButton.RecordSelector;
import com.constellio.app.ui.framework.buttons.LinkButton;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.framework.components.BaseWindow;
import com.constellio.app.ui.framework.components.ComponentState;
import com.constellio.app.ui.framework.components.RecordDisplay;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.components.content.ContentVersionVOResource;
import com.constellio.app.ui.framework.components.fields.BaseTextField;
import com.constellio.app.ui.framework.components.fields.date.JodaDateField;
import com.constellio.app.ui.framework.components.fields.lookup.LookupRecordField;
import com.constellio.app.ui.framework.components.fields.upload.ContentVersionUploadField;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.framework.containers.ButtonsContainer;
import com.constellio.app.ui.framework.containers.ButtonsContainer.ContainerButton;
import com.constellio.app.ui.framework.containers.RecordVOLazyContainer;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.framework.items.RecordVOItem;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.data.utils.Factory;
import com.constellio.data.utils.TimeProvider;
import com.constellio.model.entities.records.wrappers.User;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class DisplayFolderViewImpl extends BaseViewImpl implements DisplayFolderView, DropHandler {
	private final static Logger LOGGER = LoggerFactory.getLogger(DisplayFolderViewImpl.class);
	public static final String STYLE_NAME = "display-folder";
	public static final String USER_LOOKUP = "user-lookup";
	private RecordVO recordVO;
	private String taxonomyCode;
	private VerticalLayout mainLayout;
	private ContentVersionUploadField uploadField;
	private TabSheet tabSheet;
	private RecordDisplay recordDisplay;
	private Component documentsComponent;
	private Component subFoldersComponent;
	private Component tasksComponent;
	private DisplayFolderPresenter presenter;
	private boolean dragNDropAllowed;
	private Button deleteFolderButton, duplicateFolderButton, editFolderButton, addSubFolderButton, addDocumentButton,
			addAuthorizationButton, shareFolderButton, printLabelButton, linkToFolderButton, borrowButton, returnFolderButton,
			reminderReturnFolderButton, alertWhenAvailableButton, addToCartButton, startWorkflowButton;
	WindowButton moveInFolderButton;
	private Label borrowedLabel;

	private Window documentVersionWindow;

	public DisplayFolderViewImpl() {
		presenter = new DisplayFolderPresenter(this);
	}

	@Override
	protected void initBeforeCreateComponents(ViewChangeEvent event) {
		presenter.forParams(event.getParameters());
	}

	@Override
	protected void afterViewAssembled(ViewChangeEvent event) {
		presenter.viewAssembled();
	}

	@Override
	public void setRecord(RecordVO recordVO) {
		this.recordVO = recordVO;
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);

		uploadField = new ContentVersionUploadField();
		uploadField.setVisible(false);
		uploadField.setImmediate(true);
		uploadField.setMultiValue(false);
		uploadField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				ContentVersionVO uploadedContentVO = (ContentVersionVO) uploadField.getValue();
				presenter.contentVersionUploaded(uploadedContentVO);
			}
		});

		recordDisplay = new RecordDisplay(recordVO, new RMMetadataDisplayFactory());
		documentsComponent = new CustomComponent();
		subFoldersComponent = new CustomComponent();
		tasksComponent = new CustomComponent();

		tabSheet = new TabSheet();
		tabSheet.addStyleName(STYLE_NAME);
		tabSheet.addTab(recordDisplay, $("DisplayFolderView.tabs.metadata"));
		tabSheet.addTab(documentsComponent, $("DisplayFolderView.tabs.documents", presenter.getDocumentCount()));
		tabSheet.addTab(subFoldersComponent, $("DisplayFolderView.tabs.subFolders", presenter.getSubFolderCount()));
		tabSheet.addTab(tasksComponent, $("DisplayFolderView.tabs.tasks", presenter.getTaskCount()));

		Component disabled = new CustomComponent();
		tabSheet.addTab(disabled, $("DisplayFolderView.tabs.logs"));
		tabSheet.getTab(disabled).setEnabled(false);

		borrowedLabel = new Label();
		borrowedLabel.setVisible(false);
		borrowedLabel.addStyleName(ValoTheme.LABEL_COLORED);
		borrowedLabel.addStyleName(ValoTheme.LABEL_BOLD);

		documentVersionWindow = new BaseWindow($("DocumentContentVersionWindow.windowTitle"));
		documentVersionWindow.setWidth("400px");
		documentVersionWindow.center();
		documentVersionWindow.setModal(true);

		mainLayout.addComponents(borrowedLabel, uploadField, tabSheet);
		presenter.selectInitialTabForUser();
		return mainLayout;
	}

	@Override
	protected BaseBreadcrumbTrail buildBreadcrumbTrail() {
		return new FolderDocumentBreadcrumbTrail(recordVO.getId(), taxonomyCode);
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

	@Override
	protected List<Button> buildActionMenuButtons(ViewChangeEvent event) {
		List<Button> actionMenuButtons = new ArrayList<Button>();

		addDocumentButton = new AddButton($("DisplayFolderView.addDocument")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.addDocumentButtonClicked();
			}
		};

		moveInFolderButton = new WindowButton($("DisplayFolderView.parentFolder"), $("DisplayFolderView.parentFolder")
				, WindowButton.WindowConfiguration.modalDialog("50%", "20%")) {
			@Override
			protected Component buildWindowContent() {
				VerticalLayout verticalLayout = new VerticalLayout();
				verticalLayout.setSpacing(true);
				final LookupFolderField field = new LookupFolderField();
				verticalLayout.addComponent(field);
				BaseButton saveButton = new BaseButton($("save")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						String parentId = field.getValue();
						try {
							presenter.parentFolderButtonClicked(parentId);
						} catch (Throwable e) {
							LOGGER.warn("error when trying to modify folder parent to " + parentId, e);
							showErrorMessage("DisplayFolderView.parentFolderException");
						}
						moveInFolderButton.getWindow().close();
					}
				};
				saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
				HorizontalLayout hLayout = new HorizontalLayout();
				hLayout.setSizeFull();
				hLayout.addComponent(saveButton);
				hLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
				verticalLayout.addComponent(hLayout);
				return verticalLayout;
			}
		};

		addSubFolderButton = new AddButton($("DisplayFolderView.addSubFolder")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.addSubFolderButtonClicked();
			}
		};

		editFolderButton = new EditButton($("DisplayFolderView.editFolder")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.editFolderButtonClicked();
			}
		};

		deleteFolderButton = new DeleteWithJustificationButton($("DisplayFolderView.deleteFolder"), false) {
			@Override
			protected void deletionConfirmed(String reason) {
				presenter.deleteFolderButtonClicked(reason);
			}
		};

		duplicateFolderButton = new WindowButton($("DisplayFolderView.duplicateFolder"),
				$("DisplayFolderView.duplicateFolderOnlyOrHierarchy")) {
			@Override
			protected Component buildWindowContent() {
				BaseButton folder = new BaseButton($("DisplayFolderView.folderOnly")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.duplicateFolderButtonClicked();
						getWindow().close();
					}
				};

				BaseButton structure = new BaseButton($("DisplayFolderView.hierarchy")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.duplicateStructureButtonClicked();
						getWindow().close();
					}
				};

				BaseButton cancel = new BaseButton($("cancel")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						getWindow().close();
					}
				};
				cancel.addStyleName(ValoTheme.BUTTON_LINK);

				HorizontalLayout layout = new HorizontalLayout(folder, structure, cancel);
				layout.setComponentAlignment(folder, Alignment.TOP_LEFT);
				layout.setComponentAlignment(structure, Alignment.TOP_LEFT);
				layout.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
				layout.setExpandRatio(cancel, 1);

				layout.setWidth("95%");
				layout.setSpacing(true);

				VerticalLayout wrapper = new VerticalLayout(layout);
				wrapper.setSizeFull();

				return wrapper;
			}
		};

		linkToFolderButton = new LinkButton($("DisplayFolderView.linkToFolder")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.linkToFolderButtonClicked();
			}
		};
		linkToFolderButton.setVisible(false);

		addAuthorizationButton = new LinkButton($("DisplayFolderView.addAuthorization")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.addAuthorizationButtonClicked();
			}
		};

		shareFolderButton = new LinkButton($("DisplayFolderView.shareFolder")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.shareFolderButtonClicked();
			}
		};

		addToCartButton = buildAddToCartButton();

		Factory<List<LabelTemplate>> labelTemplatesFactory = new Factory<List<LabelTemplate>>() {
			@Override
			public List<LabelTemplate> get() {
				return presenter.getTemplates();
			}
		};
		printLabelButton = new LabelsButton(
				$("DisplayFolderView.printLabel"), $("DisplayFolderView.printLabel"),
				new RecordSelector() {
					@Override
					public List<String> getSelectedRecordIds() {
						return Arrays.asList(recordVO.getId());
					}
				}, labelTemplatesFactory);

		borrowButton = buildBorrowButton();

		returnFolderButton = buildReturnFolderButton();

		reminderReturnFolderButton = new BaseButton($("DisplayFolderView.reminderReturnFolder")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.reminderReturnFolder();
			}
		};

		alertWhenAvailableButton = new BaseButton($("RMObject.alertWhenAvailable")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.alertWhenAvailable();
			}
		};

		startWorkflowButton = new StartWorkflowButton();

		actionMenuButtons.add(addDocumentButton);
		actionMenuButtons.add(addSubFolderButton);
		actionMenuButtons.add(editFolderButton);
		actionMenuButtons.add(moveInFolderButton);
		actionMenuButtons.add(deleteFolderButton);
		actionMenuButtons.add(duplicateFolderButton);
		actionMenuButtons.add(linkToFolderButton);
		actionMenuButtons.add(addAuthorizationButton);
		actionMenuButtons.add(shareFolderButton);
		actionMenuButtons.add(addToCartButton);
		actionMenuButtons.add(printLabelButton);
		actionMenuButtons.add(borrowButton);
		actionMenuButtons.add(returnFolderButton);
		actionMenuButtons.add(reminderReturnFolderButton);
		actionMenuButtons.add(alertWhenAvailableButton);
		actionMenuButtons.add(startWorkflowButton);

		return actionMenuButtons;
	}

	private WindowButton buildAddToCartButton() {
		return new WindowButton($("DisplayFolderView.addToCart"),$("DisplayFolderView.selectCart")) {
			@Override
			protected Component buildWindowContent() {
				VerticalLayout layout = new VerticalLayout();

				HorizontalLayout newCartLayout = new HorizontalLayout();
				newCartLayout.setSpacing(true);
				newCartLayout.addComponent(new Label($("CartView.newCart")));
				final BaseTextField newCartTitleField;
				newCartLayout.addComponent(newCartTitleField = new BaseTextField());
				BaseButton saveButton;
				newCartLayout.addComponent(saveButton = new BaseButton($("save")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.createNewCartAndAddToItRequested(newCartTitleField.getValue());
						getWindow().close();
					}
				});
				saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

				TabSheet tabSheet = new TabSheet();
				final RecordVOLazyContainer ownedCartsContainer = new RecordVOLazyContainer(presenter.getOwnedCartsDataProvider());
				RecordVOTable ownedCartsTable = new RecordVOTable($("CartView.ownedCarts"), ownedCartsContainer);
				ownedCartsTable.addItemClickListener(new ItemClickListener() {
					@Override
					public void itemClick(ItemClickEvent event) {
						presenter.addToCartRequested(ownedCartsContainer.getRecordVO((int)event.getItemId()));
						getWindow().close();
					}
				});

				ownedCartsTable.setPageLength(Math.min(15, ownedCartsContainer.size()));
				ownedCartsTable.setWidth("100%");

				final RecordVOLazyContainer sharedCartsContainer = new RecordVOLazyContainer(presenter.getSharedCartsDataProvider());
				RecordVOTable sharedCartsTable = new RecordVOTable($("CartView.sharedCarts"), sharedCartsContainer);
				sharedCartsTable.addItemClickListener(new ItemClickListener() {
					@Override
					public void itemClick(ItemClickEvent event) {
						presenter.addToCartRequested(sharedCartsContainer.getRecordVO((int)event.getItemId()));
						getWindow().close();
					}
				});

				sharedCartsTable.setPageLength(Math.min(15, ownedCartsContainer.size()));
				sharedCartsTable.setWidth("100%");
				tabSheet.addTab(ownedCartsTable);
				tabSheet.addTab(sharedCartsTable);
				layout.addComponents(newCartLayout,tabSheet);
				return layout;
			}
		};
	}

	@Override
	public void setDocuments(final RecordVODataProvider dataProvider) {
		ButtonsContainer<RecordVOLazyContainer> container = new ButtonsContainer<>(new RecordVOLazyContainer(dataProvider));
		container.addButton(new ContainerButton() {
			@Override
			protected Button newButtonInstance(Object itemId) {
				int index = (int) itemId;
				final RecordVO record = dataProvider.getRecordVO(index);
				Button button = new EditButton() {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.editDocumentButtonClicked(record);
					}
				};
				button.setEnabled(presenter.canModifyDocument(record));
				return button;
			}
		});
		container.addButton(new ContainerButton() {
			@Override
			protected Button newButtonInstance(Object itemId) {
				int index = (int) itemId;
				final RecordVO record = dataProvider.getRecordVO(index);
				Button button = new IconButton(new ThemeResource("images/icons/actions/save.png"),
						$("DisplayFolderView.download")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.downloadDocumentButtonClicked(record);
					}
				};
				button.setEnabled(record.get(Document.CONTENT) != null);
				return button;
			}
		});
		container.addButton(new ContainerButton() {
			@Override
			protected Button newButtonInstance(Object itemId) {
				int index = (int) itemId;
				final RecordVO record = dataProvider.getRecordVO(index);
				Button button = new DisplayButton() {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.displayDocumentButtonClicked(record);
					}
				};
				return button;
			}
		});
		Table table = new RecordVOTable(null, container);
		table.setSizeFull();
		table.setColumnHeader(ButtonsContainer.DEFAULT_BUTTONS_PROPERTY_ID, "");
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseButton.LEFT) {
					RecordVOItem item = (RecordVOItem) event.getItem();
					RecordVO recordVO = item.getRecord();
					presenter.documentClicked(recordVO);
				}
			}
		});
		//		table.setPageLength(Math.min(15, dataProvider.size()));
		tabSheet.replaceComponent(documentsComponent, table);
		documentsComponent = table;
	}

	@Override
	public void refreshDocumentsTab() {
		Tab documentsTab = tabSheet.getTab(documentsComponent);
		documentsTab.setCaption($("DisplayFolderView.tabs.documents", presenter.getDocumentCount()));
	}

	@Override
	public void setSubFolders(RecordVODataProvider dataProvider) {
		Table table = new RecordVOTable(dataProvider);
		table.setSizeFull();
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				RecordVOItem item = (RecordVOItem) event.getItem();
				RecordVO recordVO = item.getRecord();
				presenter.subFolderClicked(recordVO);
			}
		});
		table.setPageLength(Math.min(15, dataProvider.size()));
		tabSheet.replaceComponent(subFoldersComponent, table);
		subFoldersComponent = table;
	}

	@Override
	public void setTasks(RecordVODataProvider dataProvider) {
		Table table = new RecordVOTable(dataProvider);
		table.setSizeFull();
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				RecordVOItem item = (RecordVOItem) event.getItem();
				RecordVO recordVO = item.getRecord();
				presenter.taskClicked(recordVO);
			}
		});
		table.setPageLength(Math.min(15, dataProvider.size()));
		tabSheet.replaceComponent(tasksComponent, table);
		tasksComponent = table;
	}

	@Override
	public void selectMetadataTab() {
		tabSheet.setSelectedTab(recordDisplay);
	}

	@Override
	public void selectDocumentsTab() {
		tabSheet.setSelectedTab(documentsComponent);
	}

	@Override
	public void selectSubFoldersTab() {
		tabSheet.setSelectedTab(subFoldersComponent);
	}

	@Override
	public void selectTasksTab() {
		tabSheet.setSelectedTab(tasksComponent);
	}

	@Override
	public void setLogicallyDeletable(ComponentState state) {
		deleteFolderButton.setVisible(state.isVisible());
		deleteFolderButton.setEnabled(state.isEnabled());

	}

	@Override
	public void setMoveInFolderState(ComponentState state) {
		moveInFolderButton.setVisible(state.isVisible());
		moveInFolderButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setEditButtonState(ComponentState state) {
		editFolderButton.setVisible(state.isVisible());
		editFolderButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setAddDocumentButtonState(ComponentState state) {
		addDocumentButton.setVisible(state.isVisible());
		addDocumentButton.setEnabled(state.isEnabled());
		dragNDropAllowed = state.isEnabled();
	}

	@Override
	public void setAddSubFolderButtonState(ComponentState state) {
		addSubFolderButton.setVisible(state.isVisible());
		addSubFolderButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setDuplicateFolderButtonState(ComponentState state) {
		duplicateFolderButton.setVisible(state.isVisible());
		duplicateFolderButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setPrintButtonState(ComponentState state) {
		printLabelButton.setVisible(state.isVisible());
		printLabelButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setShareFolderButtonState(ComponentState state) {
		shareFolderButton.setVisible(state.isVisible());
		shareFolderButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setAuthorizationButtonState(ComponentState state) {
		addAuthorizationButton.setVisible(state.isVisible());
		addAuthorizationButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setBorrowButtonState(ComponentState state) {
		borrowButton.setVisible(state.isVisible());
		borrowButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setReturnFolderButtonState(ComponentState state) {
		returnFolderButton.setVisible(state.isVisible());
		returnFolderButton.setEnabled(state.isEnabled());

	}

	@Override
	public void setReminderReturnFolderButtonState(ComponentState state) {
		reminderReturnFolderButton.setVisible(state.isVisible());
		reminderReturnFolderButton.setEnabled(state.isEnabled());

	}

	@Override
	public void setAlertWhenAvailableButtonState(ComponentState state) {
		alertWhenAvailableButton.setVisible(state.isVisible());
		alertWhenAvailableButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setStartWorkflowButtonState(ComponentState state) {
		startWorkflowButton.setVisible(state.isVisible());
		startWorkflowButton.setEnabled(state.isEnabled());
	}

	@Override
	public void drop(DragAndDropEvent event) {
		if (dragNDropAllowed) {
			uploadField.drop(event);
		}
	}

	@Override
	public AcceptCriterion getAcceptCriterion() {
		return uploadField != null ? uploadField.getAcceptCriterion() : AcceptAll.get();
	}

	private Button buildBorrowButton() {
		return new WindowButton($("DisplayFolderView.borrow"),
				$("DisplayFolderView.borrow")) {
			@Override
			protected Component buildWindowContent() {

				final JodaDateField borrowDatefield = new JodaDateField();
				borrowDatefield.setCaption($("DisplayFolderView.borrowDate"));
				borrowDatefield.setRequired(true);
				borrowDatefield.setId("borrowDate");
				borrowDatefield.addStyleName("borrowDate");
				borrowDatefield.setValue(TimeProvider.getLocalDate().toDate());

				final Field<?> lookupUser = new LookupRecordField(User.SCHEMA_TYPE);
				lookupUser.setCaption($("DisplayFolderView.borrower"));
				lookupUser.setId("borrower");
				lookupUser.addStyleName(USER_LOOKUP);
				lookupUser.setRequired(true);

				final ComboBox borrowingTypeField = new ComboBox();
				borrowingTypeField.setCaption($("DisplayFolderView.borrowingType"));
				for (BorrowingType borrowingType : BorrowingType.values()) {
					borrowingTypeField.addItem(borrowingType);
					borrowingTypeField
							.setItemCaption(borrowingType, $("DisplayFolderView.borrowingType." + borrowingType.getCode()));
				}
				borrowingTypeField.setRequired(true);
				borrowingTypeField.setNullSelectionAllowed(false);

				final JodaDateField previewReturnDatefield = new JodaDateField();
				previewReturnDatefield.setCaption($("DisplayFolderView.previewReturnDate"));
				previewReturnDatefield.setRequired(true);
				previewReturnDatefield.setId("previewReturnDate");
				previewReturnDatefield.addStyleName("previewReturnDate");

				final JodaDateField returnDatefield = new JodaDateField();
				returnDatefield.setCaption($("DisplayFolderView.returnDate"));
				returnDatefield.setRequired(false);
				returnDatefield.setId("returnDate");
				returnDatefield.addStyleName("returnDate");

				borrowDatefield.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						previewReturnDatefield.setValue(
								presenter.getPreviewReturnDate(borrowDatefield.getValue(), borrowingTypeField.getValue()));
					}
				});
				borrowingTypeField.addValueChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						previewReturnDatefield.setValue(
								presenter.getPreviewReturnDate(borrowDatefield.getValue(), borrowingTypeField.getValue()));
					}
				});

				BaseButton borrowButton = new BaseButton($("DisplayFolderView.borrow")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						String userId = null;
						BorrowingType borrowingType = null;
						if (lookupUser.getValue() != null) {
							userId = (String) lookupUser.getValue();
						}
						if (borrowingTypeField.getValue() != null) {
							borrowingType = BorrowingType.valueOf(borrowingTypeField.getValue().toString());
						}
						LocalDate borrowLocalDate = null;
						LocalDate previewReturnLocalDate = null;
						LocalDate returnLocalDate = null;
						if (borrowDatefield.getValue() != null) {
							borrowLocalDate = LocalDate.fromDateFields(borrowDatefield.getValue());
						}
						if (previewReturnDatefield.getValue() != null) {
							previewReturnLocalDate = LocalDate.fromDateFields(previewReturnDatefield.getValue());
						}
						if (returnDatefield.getValue() != null) {
							returnLocalDate = LocalDate.fromDateFields(returnDatefield.getValue());
						}
						if (presenter.borrowFolder(borrowLocalDate, previewReturnLocalDate, userId,
								borrowingType, returnLocalDate)) {
							getWindow().close();
						}
					}
				};
				borrowButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

				BaseButton cancelButton = new BaseButton($("cancel")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						getWindow().close();
					}
				};
				cancelButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

				HorizontalLayout horizontalLayout = new HorizontalLayout();
				horizontalLayout.setSpacing(true);
				horizontalLayout.addComponents(borrowButton, cancelButton);

				VerticalLayout verticalLayout = new VerticalLayout();
				verticalLayout
						.addComponents(borrowDatefield, borrowingTypeField, lookupUser, previewReturnDatefield, returnDatefield,
								horizontalLayout);
				verticalLayout.setSpacing(true);

				return verticalLayout;
			}
		};
	}

	private Button buildReturnFolderButton() {
		return new WindowButton($("DisplayFolderView.returnFolder"),
				$("DisplayFolderView.returnFolder")) {
			@Override
			protected Component buildWindowContent() {

				final JodaDateField returnDatefield = new JodaDateField();
				returnDatefield.setCaption($("DisplayFolderView.returnDate"));
				returnDatefield.setRequired(false);
				returnDatefield.setId("returnDate");
				returnDatefield.addStyleName("returnDate");
				returnDatefield.setValue(TimeProvider.getLocalDate().toDate());

				BaseButton returnFolderButton = new BaseButton($("DisplayFolderView.returnFolder")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						LocalDate returnLocalDate = null;
						if (returnDatefield.getValue() != null) {
							returnLocalDate = LocalDate.fromDateFields(returnDatefield.getValue());
						}
						if (presenter.returnFolder(returnLocalDate)) {
							getWindow().close();
						}
					}
				};
				returnFolderButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

				BaseButton cancelButton = new BaseButton($("cancel")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						getWindow().close();
					}
				};
				cancelButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

				HorizontalLayout horizontalLayout = new HorizontalLayout();
				horizontalLayout.setSpacing(true);
				horizontalLayout.addComponents(returnFolderButton, cancelButton);

				VerticalLayout verticalLayout = new VerticalLayout();
				verticalLayout
						.addComponents(returnDatefield, horizontalLayout);
				verticalLayout.setSpacing(true);

				return verticalLayout;
			}
		};
	}

	@Override
	public void setBorrowedMessage(String borrowedMessage) {
		if (borrowedMessage != null) {
			borrowedLabel.setVisible(true);
			borrowedLabel.setValue($(borrowedMessage));
		} else {
			borrowedLabel.setVisible(false);
			borrowedLabel.setValue(null);
		}
	}

	@Override
	public void openDocumenContentVersiontWindow(DocumentVO documentVO, ContentVersionVO contentVersionVO) {
		documentVersionWindow.setContent(new DocumentContentVersionWindowImpl(documentVO, contentVersionVO));
		UI.getCurrent().addWindow(documentVersionWindow);
	}

	@Override
	public void closeDocumentContentVersionWindow() {
		documentVersionWindow.close();
	}

	@Override
	public void openAgentURL(String agentURL) {
		Page.getCurrent().open(agentURL, null);
	}

	@Override
	public void downloadContentVersion(RecordVO recordVO, ContentVersionVO contentVersionVO) {
		ContentVersionVOResource contentVersionResource = new ContentVersionVOResource(contentVersionVO);
		Resource downloadedResource = DownloadLink.wrapForDownload(contentVersionResource);
		Page.getCurrent().open(downloadedResource, null, false);
	}

	@Override
	public void setTaxonomyCode(String taxonomyCode) {
		this.taxonomyCode = taxonomyCode;
	}

	private class StartWorkflowButton extends WindowButton {
		public StartWorkflowButton() {
			super($("TasksManagementView.startWorkflow"), $("TasksManagementView.startWorkflow"), modalDialog("75%", "75%"));
		}

		@Override
		protected Component buildWindowContent() {
			RecordVOTable table = new RecordVOTable(presenter.getWorkflows());
			table.setWidth("98%");
			table.addItemClickListener(new ItemClickListener() {
				@Override
				public void itemClick(ItemClickEvent event) {
					RecordVOItem item = (RecordVOItem) event.getItem();
					presenter.workflowStartRequested(item.getRecord());
					getWindow().close();
				}
			});
			return table;
		}
	}
}
