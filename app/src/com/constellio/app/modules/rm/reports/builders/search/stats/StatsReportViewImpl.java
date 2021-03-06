package com.constellio.app.modules.rm.reports.builders.search.stats;

import static com.constellio.app.ui.i18n.i18n.$;

import com.constellio.app.modules.rm.reports.model.search.stats.FolderLinearMeasureStatsReportPresenter;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.framework.reports.ReportBuilder;
import com.constellio.app.ui.framework.reports.ReportBuilderFactory;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.services.factories.ModelLayerFactory;

public class StatsReportViewImpl implements ReportBuilderFactory {

	boolean withUsers = false;

	public StatsReportViewImpl() {
	}

	public StatsReportViewImpl(boolean withUsers) {
		this.withUsers = withUsers;
	}

	@Override
	public ReportBuilder getReportBuilder(ModelLayerFactory modelLayerFactory) {
		String collection = getSessionContext().getCurrentCollection();
		FolderLinearMeasureStatsReportPresenter presenter = new FolderLinearMeasureStatsReportPresenter(collection, modelLayerFactory,
				withUsers);
		return new StatsReportBuilder(presenter.build(), presenter.getFoldersLocator());
	}

	@Override
	public String getFilename() {
		return $("Report.FolderLinearMeasureStats") + ".pdf";
	}

	SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}
}
