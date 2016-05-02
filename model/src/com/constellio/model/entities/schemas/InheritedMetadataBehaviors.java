package com.constellio.model.entities.schemas;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class InheritedMetadataBehaviors {

	final boolean undeletable;

	final boolean multivalue;
	final boolean systemReserved;
	final boolean unmodifiable;
	final boolean uniqueValue;
	final boolean childOfRelationship;
	final boolean taxonomyRelationship;
	final boolean sortable;
	final boolean searchable;
	final boolean schemaAutocomplete;
	final boolean essential;
	final boolean encrypted;
	final boolean essentialInSummary;
	final boolean copyReferencedSearchables;

	public InheritedMetadataBehaviors(boolean undeletable, boolean multivalue, boolean systemReserved, boolean unmodifiable,
			boolean uniqueValue, boolean childOfRelationship, boolean taxonomyRelationship, boolean sortable,
			boolean searchable, boolean copyReferencedSearchables, boolean schemaAutocomplete, boolean essential,
			boolean encrypted, boolean essentialInSummary) {
		this.undeletable = undeletable;
		this.multivalue = multivalue;
		this.systemReserved = systemReserved;
		this.unmodifiable = unmodifiable;
		this.uniqueValue = uniqueValue;
		this.childOfRelationship = childOfRelationship;
		this.taxonomyRelationship = taxonomyRelationship;
		this.sortable = sortable;
		this.searchable = searchable;
		this.copyReferencedSearchables = copyReferencedSearchables;
		this.schemaAutocomplete = schemaAutocomplete;
		this.essential = essential;
		this.encrypted = encrypted;
		this.essentialInSummary = essentialInSummary;
	}

	public boolean isUndeletable() {
		return undeletable;
	}

	public boolean isMultivalue() {
		return multivalue;
	}

	public boolean isEssentialInSummary() {
		return essentialInSummary;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public boolean isSystemReserved() {
		return systemReserved;
	}

	public boolean isUnmodifiable() {
		return unmodifiable;
	}

	public boolean isUniqueValue() {
		return uniqueValue;
	}

	public boolean isChildOfRelationship() {
		return childOfRelationship;
	}

	public boolean isTaxonomyRelationship() {
		return taxonomyRelationship;
	}

	public boolean isSortable() {
		return sortable;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public boolean isCopyReferencedSearchables() {
		return copyReferencedSearchables;
	}

	public boolean isSchemaAutocomplete() {
		return schemaAutocomplete;
	}

	public boolean isEssential() {
		return essential;
	}

	public boolean isEncrypted() {
		return encrypted;
	}
}
