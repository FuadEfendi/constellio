package com.constellio.app.modules.rm.ui.components.folder;

import static com.constellio.app.modules.rm.wrappers.Folder.ACTUAL_DEPOSIT_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.ACTUAL_DESTRUCTION_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.ACTUAL_TRANSFER_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.ADMINISTRATIVE_UNIT_ENTERED;
import static com.constellio.app.modules.rm.wrappers.Folder.BORROWING_TYPE;
import static com.constellio.app.modules.rm.wrappers.Folder.BORROW_PREVIEW_RETURN_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.BORROW_RETURN_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.CATEGORY_ENTERED;
import static com.constellio.app.modules.rm.wrappers.Folder.CONTAINER;
import static com.constellio.app.modules.rm.wrappers.Folder.COPY_STATUS_ENTERED;
import static com.constellio.app.modules.rm.wrappers.Folder.LINEAR_SIZE;
import static com.constellio.app.modules.rm.wrappers.Folder.MAIN_COPY_RULE_ID_ENTERED;
import static com.constellio.app.modules.rm.wrappers.Folder.MANUAL_ARCHIVISTIC_STATUS;
import static com.constellio.app.modules.rm.wrappers.Folder.MANUAL_EXPECTED_DEPOSIT_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.MANUAL_EXPECTED_DESTRIUCTION_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.MANUAL_EXPECTED_TRANSFER_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.OPENING_DATE;
import static com.constellio.app.modules.rm.wrappers.Folder.PARENT_FOLDER;
import static com.constellio.app.modules.rm.wrappers.Folder.RETENTION_RULE_ENTERED;
import static com.constellio.app.modules.rm.wrappers.Folder.TYPE;
import static com.constellio.app.modules.rm.wrappers.Folder.UNIFORM_SUBDIVISION_ENTERED;

import java.util.List;

import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.enums.CalculatorWithManualMetadataChoice;
import com.constellio.app.modules.rm.ui.components.RMMetadataFieldFactory;
import com.constellio.app.modules.rm.ui.components.folder.fields.CustomFolderField;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderActualDepositDateFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderActualDestructionDateFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderActualTransferDateFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderAdministrativeUnitFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderBorrpwingTypeFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderCategoryFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderContainerFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderCopyRuleFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderCopyStatusEnteredFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderLinearSizeFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderOpeningDateFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderParentFolderFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderPreviewReturnDateFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderRetentionRuleFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderReturnDateFieldImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderTypeFieldComboBoxImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderTypeFieldLookupImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderTypeFieldOptionGroupImpl;
import com.constellio.app.modules.rm.ui.components.folder.fields.FolderUniformSubdivisionFieldImpl;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.vaadin.ui.Field;

public class FolderFieldFactory extends RMMetadataFieldFactory {
	private final String collection;
	private final List<CopyRetentionRule> rules;
	private ModelLayerFactory modelLayerFactory;

	public FolderFieldFactory(String collection, List<CopyRetentionRule> rules) {
		this.collection = collection;
		this.rules = rules;
	}

	@Override
	public Field<?> build(RecordVO recordVO, MetadataVO metadata) {
		Field<?> field;
		String[] taxonomyCodes = metadata.getTaxonomyCodes();
		MetadataInputType inputType = metadata.getMetadataInputType();

		switch (metadata.getLocalCode()) {
		case TYPE:
			if (MetadataInputType.LOOKUP.equals(inputType)) {
				field = new FolderTypeFieldLookupImpl();
			} else if (MetadataInputType.RADIO_BUTTONS.equals(inputType)) {
				field = new FolderTypeFieldOptionGroupImpl();
			} else {
				field = new FolderTypeFieldComboBoxImpl();
			}
			break;
		case PARENT_FOLDER:
			field = new FolderParentFolderFieldImpl(taxonomyCodes);
			break;
		case ADMINISTRATIVE_UNIT_ENTERED:
			field = new FolderAdministrativeUnitFieldImpl();
			break;
		case CATEGORY_ENTERED:
			field = new FolderCategoryFieldImpl();
			break;
		case UNIFORM_SUBDIVISION_ENTERED:
			field = new FolderUniformSubdivisionFieldImpl();
			break;
		case RETENTION_RULE_ENTERED:
			field = new FolderRetentionRuleFieldImpl(collection);
			break;
		case COPY_STATUS_ENTERED:
			field = new FolderCopyStatusEnteredFieldImpl();
			break;
		case ACTUAL_TRANSFER_DATE:
			field = new FolderActualTransferDateFieldImpl();
			break;
		case ACTUAL_DEPOSIT_DATE:
			field = new FolderActualDepositDateFieldImpl();
			break;
		case ACTUAL_DESTRUCTION_DATE:
			field = new FolderActualDestructionDateFieldImpl();
			break;
		case CONTAINER:
			field = new FolderContainerFieldImpl();
			break;
		case LINEAR_SIZE:
			field = new FolderLinearSizeFieldImpl();
			break;
		case BORROW_PREVIEW_RETURN_DATE:
			field = new FolderPreviewReturnDateFieldImpl();
			break;
		case BORROW_RETURN_DATE:
			field = new FolderReturnDateFieldImpl();
			break;
		case BORROWING_TYPE:
			field = new FolderBorrpwingTypeFieldImpl();
			break;
		case OPENING_DATE:
			field = new FolderOpeningDateFieldImpl();
			break;
		case MAIN_COPY_RULE_ID_ENTERED:
			field = new FolderCopyRuleFieldImpl(rules);
			break;
		case MANUAL_ARCHIVISTIC_STATUS:
		case MANUAL_EXPECTED_DEPOSIT_DATE:
		case MANUAL_EXPECTED_DESTRIUCTION_DATE:
		case MANUAL_EXPECTED_TRANSFER_DATE:
			field = super.build(recordVO, metadata);
			postBuild(field, recordVO, metadata);
		default:
			field = super.build(recordVO, metadata);
		}

		if (field instanceof CustomFolderField) {
			postBuild(field, recordVO, metadata);
		}
		return field;
	}

	@Override
	public void postBuild(Field<?> field, RecordVO recordVO, MetadataVO metadataVO) {
		super.postBuild(field, recordVO, metadataVO);
		switch (metadataVO.getLocalCode()) {
		case MANUAL_ARCHIVISTIC_STATUS:
		case MANUAL_EXPECTED_DEPOSIT_DATE:
		case MANUAL_EXPECTED_DESTRIUCTION_DATE:
		case MANUAL_EXPECTED_TRANSFER_DATE:
			field.setVisible(isManualArchivisticStatusVisible(recordVO));
		}
	}

	private boolean isManualArchivisticStatusVisible(RecordVO recordVO) {
		RMConfigs rmConfigs = new RMConfigs(getModelLayerFactory().getSystemConfigurationsManager());
		CalculatorWithManualMetadataChoice type;
		switch (type = rmConfigs.getArchivisticCalculatorsMode()) {
		case DISABLE:
			return false;
		case ENABLE:
			return true;
		case ENABLE_DURING_IMPORT:
			return recordVO.get(Schemas.LEGACY_ID.getLocalCode()) != null;
		default:
			throw new RuntimeException("Unsupported type " + type);
		}
	}

	private ModelLayerFactory getModelLayerFactory() {
		if (modelLayerFactory == null) {
			modelLayerFactory = ConstellioUI.getCurrent().getConstellioFactories().getModelLayerFactory();
		}
		return modelLayerFactory;
	}
}
