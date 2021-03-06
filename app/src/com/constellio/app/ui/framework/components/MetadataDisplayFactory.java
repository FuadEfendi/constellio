package com.constellio.app.ui.framework.components;

import static com.constellio.app.ui.i18n.i18n.$;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.rm.wrappers.structures.CommentFactory;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.MetadataValueVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.converters.BaseStringToDateConverter;
import com.constellio.app.ui.framework.components.converters.BaseStringToDateTimeConverter;
import com.constellio.app.ui.framework.components.converters.JodaDateTimeToStringConverter;
import com.constellio.app.ui.framework.components.converters.JodaDateToStringConverter;
import com.constellio.app.ui.framework.components.display.EnumWithSmallCodeDisplay;
import com.constellio.app.ui.framework.components.display.ReferenceDisplay;
import com.constellio.app.ui.framework.components.fields.comment.RecordCommentsEditorImpl;
import com.constellio.model.entities.EnumWithSmallCode;
import com.constellio.model.entities.schemas.AllowedReferences;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.StructureFactory;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class MetadataDisplayFactory implements Serializable {

	private BaseStringToDateConverter utilDateConverter = new BaseStringToDateConverter();

	private BaseStringToDateTimeConverter utilDateTimeConverter = new BaseStringToDateTimeConverter();

	private JodaDateToStringConverter jodaDateConverter = new JodaDateToStringConverter();

	private JodaDateTimeToStringConverter jodaDateTimeConverter = new JodaDateTimeToStringConverter();

	public Component build(RecordVO recordVO, MetadataValueVO metadataValue) {
		Component displayComponent;
		MetadataVO metadataVO = metadataValue.getMetadata();
		Object displayValue = metadataValue.getValue();
		String metadataCode = metadataVO.getCode();
		StructureFactory structureFactory = metadataVO.getStructureFactory();

		if (metadataVO.isMultivalue() && structureFactory != null && structureFactory instanceof CommentFactory) {
			displayComponent = new RecordCommentsEditorImpl(recordVO, metadataCode);
			displayComponent.setWidthUndefined();
		} else if (displayValue == null) {
			displayComponent = null;
		} else if (displayValue instanceof Collection<?>) {
			Collection<?> collectionDisplayValue = (Collection<?>) displayValue;
			if (collectionDisplayValue.isEmpty()) {
				displayComponent = null;
			} else {
				List<Component> elementDisplayComponents = new ArrayList<Component>();
				for (Object elementDisplayValue : collectionDisplayValue) {
					Component elementDisplayComponent = buildSingleValue(recordVO,
							metadataValue.getMetadata(), elementDisplayValue);
					if (elementDisplayComponent != null) {
						elementDisplayComponent.setSizeFull();
						elementDisplayComponents.add(elementDisplayComponent);
					}
				}
				if (!elementDisplayComponents.isEmpty()) {
					displayComponent = newCollectionValueDisplayComponent(elementDisplayComponents);
				} else {
					displayComponent = null;
				}
			}
		} else {
			displayComponent = buildSingleValue(recordVO, metadataVO, displayValue);
		}
		return displayComponent;
	}

	/**
	 * @param recordVO May be null, be careful!
	 * @param metadata The metadata for which we want a display component
	 * @param displayValue The value to display
	 * @return
	 */
	public Component buildSingleValue(RecordVO recordVO, MetadataVO metadata, Object displayValue) {
		Component displayComponent;
		Locale locale = ConstellioUI.getCurrentSessionContext().getCurrentLocale();

		String[] taxonomyCodes = metadata.getTaxonomyCodes();
		AllowedReferences allowedReferences = metadata.getAllowedReferences();

		MetadataInputType metadataInputType = metadata.getMetadataInputType();
		MetadataValueType metadataValueType = metadata.getType();

		if (displayValue == null) {
			displayComponent = null;
		} else if ((displayValue instanceof String) && StringUtils.isBlank(displayValue.toString())) {
			displayComponent = null;
		} else {
			switch (metadataValueType) {
			case BOOLEAN:
				String key = Boolean.TRUE.equals(displayValue) ? "yes" : "no";
				displayComponent = new Label($(key));
				break;
			case DATE:
				if (displayValue instanceof LocalDate) {
					String convertedJodaDate = jodaDateConverter
							.convertToPresentation((LocalDate) displayValue, String.class, locale);
					displayComponent = new Label(convertedJodaDate);
				} else if (displayValue instanceof Date) {
					String convertedDate = utilDateConverter.convertToPresentation((Date) displayValue, String.class, locale);
					displayComponent = new Label(convertedDate);
				} else {
					displayComponent = null;
				}
				break;
			case DATE_TIME:
				if (displayValue instanceof LocalDateTime) {
					String convertedJodaDate = jodaDateTimeConverter
							.convertToPresentation((LocalDateTime) displayValue, String.class, locale);
					displayComponent = new Label(convertedJodaDate);
				} else if (displayValue instanceof Date) {
					String convertedDate = utilDateTimeConverter.convertToPresentation((Date) displayValue, String.class, locale);
					displayComponent = new Label(convertedDate);
				} else {
					displayComponent = null;
				}
				break;
			case NUMBER:
				String strDisplayValue = displayValue.toString();
				if (strDisplayValue.endsWith(".0")) {
					strDisplayValue = StringUtils.substringBefore(strDisplayValue, ".");
				}
				displayComponent = new Label(strDisplayValue);
				((Label) displayComponent).setConverter(new StringToDoubleConverter());
				break;
			case INTEGER:
				displayComponent = new Label(displayValue.toString());
				((Label) displayComponent).setConverter(new StringToIntegerConverter());
				break;
			case STRING:
				if (MetadataInputType.PASSWORD.equals(metadataInputType)) {
					displayComponent = null;
				} else {
					displayComponent = new Label(displayValue.toString());
				}
				break;
			case TEXT:
				switch (metadataInputType) {
				case RICHTEXT:
					displayComponent = new Label(displayValue.toString(), ContentMode.HTML);
					break;
				default:
					String stringValue = StringUtils.replace(displayValue.toString(), "\n", "<br/>");
					displayComponent = new Label(stringValue, ContentMode.HTML);
					break;
				}
				break;
			case STRUCTURE:
				displayComponent = new Label(displayValue.toString());
				break;
			case CONTENT:
				ContentVersionVO contentVersionVO = (ContentVersionVO) displayValue;
				displayComponent = new ContentVersionDisplay(recordVO, contentVersionVO);
				break;
			case REFERENCE:
				switch (metadataInputType) {
				case LOOKUP:
					displayComponent = new ReferenceDisplay(displayValue.toString());
					break;
				default:
					if (allowedReferences != null) {
						displayComponent = new ReferenceDisplay(displayValue.toString());
					} else if (taxonomyCodes.length > 0) {
						displayComponent = new Label(taxonomyCodes.toString());
					} else {
						displayComponent = new Label(displayValue.toString());
					}
					break;
				}
				break;
			case ENUM:
				if (displayValue instanceof EnumWithSmallCode) {
					displayComponent = new EnumWithSmallCodeDisplay<>((EnumWithSmallCode) displayValue);
				} else if (displayValue instanceof String) {
					displayComponent = new Label($(metadata.getEnumClass().getSimpleName() + "." + displayValue));
				} else {
					displayComponent = null;
				}
				break;
			default:
				displayComponent = null;
				break;
			}
		}
		return displayComponent;
	}

	//	protected Component newContentVersionDisplayComponent(RecordVO recordVO, ContentVersionVO contentVersionVO) {
	//		return new DownloadContentVersionLink(contentVersionVO);
	//	}

	public Component newCollectionValueDisplayComponent(List<Component> elementDisplayComponents) {
		VerticalLayout verticalLayout = new VerticalLayout();
		for (Component elementDisplayComponent : elementDisplayComponents) {
			verticalLayout.addComponent(elementDisplayComponent);
		}
		return verticalLayout;
	}
}
