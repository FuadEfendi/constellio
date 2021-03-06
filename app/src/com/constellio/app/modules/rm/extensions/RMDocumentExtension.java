package com.constellio.app.modules.rm.extensions;

import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.type.DocumentType;
import com.constellio.data.frameworks.extensions.ExtensionBooleanResult;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.extensions.behaviors.RecordExtension;
import com.constellio.model.extensions.events.records.RecordInCreationBeforeSaveEvent;
import com.constellio.model.extensions.events.records.RecordInModificationBeforeSaveEvent;
import com.constellio.model.extensions.events.records.RecordSetCategoryEvent;
import com.constellio.model.services.factories.ModelLayerFactory;

public class RMDocumentExtension extends RecordExtension {

	private static String OUTLOOK_MSG_MIMETYPE = "application/vnd.ms-outlook";

	RMSchemasRecordsServices rm;
	DecommissioningService decommissioningService;

	public RMDocumentExtension(String collection, ModelLayerFactory modelLayerFactory) {
		rm = new RMSchemasRecordsServices(collection, modelLayerFactory);
		decommissioningService = new DecommissioningService(collection, modelLayerFactory);
	}

	@Override
	public void setRecordCategory(RecordSetCategoryEvent event) {

		if (event.isSchemaType(Document.SCHEMA_TYPE)) {
			Document document = rm.wrapDocument(event.getRecord());
			DocumentType type = null;

			if ("default".equals(document.getSchema().getLocalCode())) {
				Content content = document.getContent();

				if (event.getCategory() != null) {
					type = rm.getDocumentTypeByCode(event.getCategory());
				}
				if (type == null && content != null) {
					String mimetype = content.getCurrentVersion().getMimetype();
					if (OUTLOOK_MSG_MIMETYPE.equals(mimetype)) {
						type = rm.getDocumentTypeByCode(DocumentType.EMAIL_DOCUMENT_TYPE);
					}
				}
				if (type != null) {
					document.setType(type);
					if (type.getLinkedSchema() != null) {
						document.changeSchemaTo(type.getLinkedSchema());
					}
				}
			}
		}

	}

	@Override
	public void recordInCreationBeforeSave(RecordInCreationBeforeSaveEvent event) {
		if (event.isSchemaType(Document.SCHEMA_TYPE)) {
			Document document = rm.wrapDocument(event.getRecord());
			Content content = document.getContent();
			boolean requireConversion = content != null && isFilePreviewSupportedFor(content.getCurrentVersion().getFilename());
			document.setMarkedForPreviewConversion(requireConversion ? true : null);
		}
	}

	@Override
	public void recordInModificationBeforeSave(RecordInModificationBeforeSaveEvent event) {
		if (event.isSchemaType(Document.SCHEMA_TYPE) && event.hasModifiedMetadata(Document.CONTENT)) {
			Document document = rm.wrapDocument(event.getRecord());
			Content content = document.getContent();
			boolean requireConversion = content != null && isFilePreviewSupportedFor(content.getCurrentVersion().getFilename());
			document.setMarkedForPreviewConversion(requireConversion ? true : null);
		}
	}

	@Override
	public ExtensionBooleanResult isRecordModifiableBy(IsRecordModifiableByParams params) {
		User user = params.getUser();
		if (params.isSchemaType(Document.SCHEMA_TYPE)) {
			Document document = rm.wrapDocument(params.getRecord());
			Folder parentFolder = rm.getFolder(document.getFolder());

			if (user.hasWriteAccess().on(document)) {
				if (parentFolder.getArchivisticStatus().isInactive()) {
					if (parentFolder.getBorrowed() != null && parentFolder.getBorrowed()) {
						return ExtensionBooleanResult
								.trueIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_BORROWED_FOLDER).on(parentFolder)
										&& user.has(RMPermissionsTo.MODIFY_INACTIVE_DOCUMENT).on(document));
					}
					return ExtensionBooleanResult.trueIf(user.has(RMPermissionsTo.MODIFY_INACTIVE_DOCUMENT).on(document));
				}
				if (parentFolder.getArchivisticStatus().isSemiActive()) {
					if (parentFolder.getBorrowed() != null && parentFolder.getBorrowed()) {
						return ExtensionBooleanResult
								.trueIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_BORROWED_FOLDER).on(parentFolder)
										&& user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_DOCUMENT).on(document));
					}
					return ExtensionBooleanResult.trueIf(user.has(RMPermissionsTo.MODIFY_SEMIACTIVE_DOCUMENT).on(document));
				}
				return ExtensionBooleanResult.TRUE;
			}

			return ExtensionBooleanResult.FALSE;
		}
		return ExtensionBooleanResult.NOT_APPLICABLE;
	}

	private boolean isFilePreviewSupportedFor(String filename) {
		return filename.endsWith(".doc")
				|| filename.endsWith(".xls")
				|| filename.endsWith(".ppt")
				|| filename.endsWith(".docx")
				|| filename.endsWith(".xlsx")
				|| filename.endsWith(".pptx")
				|| filename.endsWith(".dot")
				|| filename.endsWith(".odt");
	}
}
