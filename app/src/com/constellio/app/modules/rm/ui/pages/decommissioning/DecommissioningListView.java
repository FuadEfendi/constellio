package com.constellio.app.modules.rm.ui.pages.decommissioning;

import com.constellio.app.modules.rm.ui.entities.FolderDetailVO;
import com.constellio.app.modules.rm.ui.pages.viewGroups.ArchivesManagementViewGroup;
import com.constellio.app.ui.pages.base.BaseView;

public interface DecommissioningListView extends BaseView, ArchivesManagementViewGroup {

	void updateProcessButtonState(boolean processable);

	void setProcessable(FolderDetailVO folderVO);

	void setPackageable(FolderDetailVO folderVO);
}
