package com.constellio.app.services.importExport.settings.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ImportedSequence {

	String key;
	String value;

	public String getKey() {
		return key;
	}

	public ImportedSequence setKey(String key) {
		this.key = key;
		return this;
	}

	public ImportedSequence setValue(String value) {
		this.value = value;
		return this;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);

	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
