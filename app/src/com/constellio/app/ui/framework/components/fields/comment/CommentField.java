package com.constellio.app.ui.framework.components.fields.comment;

import com.constellio.app.modules.rm.wrappers.structures.Comment;
import com.constellio.app.ui.framework.components.converters.CommentToStringConverter;
import com.constellio.app.ui.framework.components.fields.BaseRichTextArea;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.ErrorMessage;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

public class CommentField extends CustomField<Comment> {
	
	private CommentToStringConverter converter = new CommentToStringConverter();
	
	private BaseRichTextArea richTextArea;
	
	public CommentField() {
		richTextArea = new BaseRichTextArea();
		richTextArea.setConverter(converter);
		richTextArea.setImmediate(false);
	}

	@Override
	protected Component initContent() {
		return richTextArea;
	}

	@Override
	public Class<? extends Comment> getType() {
		return Comment.class;
	}

	@SuppressWarnings("rawtypes")
	public Property getPropertyDataSource() {
		return richTextArea.getPropertyDataSource();
	}

	@SuppressWarnings("rawtypes")
	public void setPropertyDataSource(Property newDataSource) {
		richTextArea.setPropertyDataSource(newDataSource);
	}
	
	public void setValue(Comment newValue)
			throws com.vaadin.data.Property.ReadOnlyException {
		super.setValue(newValue);
		richTextArea.setValue(converter.convertToPresentation(newValue, String.class, getLocale()));
	}

	public void addStyleName(String style) {
		richTextArea.addStyleName(style);
	}

	public void focus() {
		richTextArea.focus();
	}

	public String getRequiredError() {
		return richTextArea.getRequiredError();
	}

	@Override
	public void setRequiredError(String requiredMessage) {
		richTextArea.setRequiredError(requiredMessage);
	}

	@Override
	public String getConversionError() {
		return richTextArea.getConversionError();
	}

	@Override
	public void setConversionError(String valueConversionError) {
		richTextArea.setConversionError(valueConversionError);
	}

	@Override
	public ErrorMessage getComponentError() {
		return richTextArea.getComponentError();
	}

	@Override
	public void setComponentError(ErrorMessage componentError) {
		richTextArea.setComponentError(componentError);
	}

	public void commit()
			throws SourceException, InvalidValueException {
		richTextArea.commit();
	}

	public void discard()
			throws SourceException {
		richTextArea.discard();
	}

	public boolean isValid() {
		return richTextArea.isValid();
	}

	public void validate()
			throws InvalidValueException {
		richTextArea.validate();
	}
	
}