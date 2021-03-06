package com.constellio.app.modules.rm.reports.factories.labels;

import java.util.List;

import com.constellio.app.modules.rm.model.labelTemplate.LabelTemplate;
import com.constellio.app.modules.rm.reports.builders.labels.LabelsReportBuilder;
import com.constellio.app.modules.rm.reports.model.labels.LabelsReportPresenter;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.framework.reports.ReportBuilder;
import com.constellio.app.ui.framework.reports.ReportBuilderFactory;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.services.factories.ModelLayerFactory;

public class LabelsReportFactory implements ReportBuilderFactory {
	private final List<String> recordIds;
	private final LabelTemplate labelConfiguration;
	private final int startPosition;
	private final int numberOfCopies;

	public LabelsReportFactory(List<String> recordIds, LabelTemplate labelTemplate, int startPosition,
			int numberOfCopies) {
		this.recordIds = recordIds;
		this.labelConfiguration = labelTemplate;
		this.startPosition = startPosition;
		this.numberOfCopies = numberOfCopies;
	}

	@Override
	public ReportBuilder getReportBuilder(ModelLayerFactory modelLayerFactory) {
		String collection = getSessionContext().getCurrentCollection();
		LabelsReportPresenter presenter = new LabelsReportPresenter(collection, modelLayerFactory);
		return new LabelsReportBuilder(presenter.build(recordIds, startPosition, numberOfCopies, labelConfiguration));
	}

	@Override
	public String getFilename() {
		return "labels.pdf";
	}

	SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}
}
