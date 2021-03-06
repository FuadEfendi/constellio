package com.constellio.app.ui.framework.reports;

import java.io.Serializable;

import com.constellio.model.services.factories.ModelLayerFactory;

public interface ReportBuilderFactory extends Serializable {
	ReportBuilder getReportBuilder(ModelLayerFactory modelLayerFactory);

	String getFilename();
}
