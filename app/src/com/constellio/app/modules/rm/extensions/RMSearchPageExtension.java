package com.constellio.app.modules.rm.extensions;

import com.constellio.app.api.extensions.SearchPageExtension;
import com.constellio.app.api.extensions.taxonomies.GetCustomResultDisplayParam;
import com.constellio.app.modules.rm.ui.components.DocumentSearchResultDisplay;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.framework.components.SearchResultDisplay;

public class RMSearchPageExtension extends SearchPageExtension {

	public RMSearchPageExtension(AppLayerFactory appLayerFactory) {
		super(appLayerFactory);
	}

	@Override
	public SearchResultDisplay getCustomResultDisplayFor(GetCustomResultDisplayParam param) {
		if (param.getSchemaType().equals(Document.SCHEMA_TYPE)) {
			return new DocumentSearchResultDisplay(param.getSearchResultVO(), param.getComponentFactory(), getAppLayerFactory());
		}
		return super.getCustomResultDisplayFor(param);
	}
}
