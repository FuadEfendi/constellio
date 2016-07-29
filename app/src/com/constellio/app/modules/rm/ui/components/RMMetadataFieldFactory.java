package com.constellio.app.modules.rm.ui.components;

import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.rm.ui.components.folder.fields.LookupFolderField;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.MetadataFieldFactory;
import com.constellio.app.ui.framework.components.RecordFieldFactory;
import com.vaadin.ui.Field;

public class RMMetadataFieldFactory extends RecordFieldFactory {//MetadataFieldFactory

	@Override
	public Field<?> build(RecordVO recordVO, MetadataVO metadata) {
		Field<?> field;
		String schemaTypeCode = metadata.getSchemaTypeCode();
		MetadataInputType inputType = metadata.getMetadataInputType();
		if (inputType == MetadataInputType.LOOKUP && schemaTypeCode.equals(Folder.SCHEMA_TYPE) && !metadata.isMultivalue()) {
			field = new LookupFolderField();
		} else {
			field = super.build(recordVO, metadata);
		}
		if (field instanceof LookupFolderField) {
			postBuild(field, recordVO, metadata);
		}
		return field;
	}

}
