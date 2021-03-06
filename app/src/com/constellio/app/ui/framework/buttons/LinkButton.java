package com.constellio.app.ui.framework.buttons;

import com.vaadin.ui.themes.ValoTheme;

public abstract class LinkButton extends BaseButton {

	public LinkButton(String caption) {
		super(caption);
		addStyleName(ValoTheme.BUTTON_BORDERLESS);
		addStyleName(ValoTheme.BUTTON_LINK);
	}

}
