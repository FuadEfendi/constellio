package com.constellio.app.modules.rm.model.calculators.folder;

import static java.util.Arrays.asList;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;

import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.DynamicDependencyValues;
import com.constellio.model.entities.calculators.dependencies.DynamicLocalDependency;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataValueType;

public class FolderDecomDatesDynamicLocalDependency extends DynamicLocalDependency {

	public static List<String> excludedMetadatas = asList(
			Folder.BORROW_DATE,
			Folder.BORROW_PREVIEW_RETURN_DATE,
			Folder.BORROW_RETURN_DATE,
			Folder.COPY_RULES_EXPECTED_DEPOSIT_DATES,
			Folder.COPY_RULES_EXPECTED_DESTRUCTION_DATES,
			Folder.COPY_RULES_EXPECTED_TRANSFER_DATES,
			Folder.OPENING_DATE,
			Folder.CLOSING_DATE,
			Folder.ENTERED_CLOSING_DATE,
			Folder.DECOMMISSIONING_DATE,
			Folder.ACTUAL_DEPOSIT_DATE,
			Folder.ACTUAL_DEPOSIT_DATE,
			Folder.ACTUAL_DESTRUCTION_DATE,
			Folder.ACTUAL_TRANSFER_DATE,
			Folder.EXPECTED_DEPOSIT_DATE,
			Folder.EXPECTED_DESTRUCTION_DATE,
			Folder.EXPECTED_TRANSFER_DATE
	);

	@Override
	public boolean isDependentOf(Metadata metadata) {
		if (metadata.getType() == MetadataValueType.DATE
				|| metadata.getType() == MetadataValueType.DATE_TIME
				|| metadata.getType() == MetadataValueType.NUMBER
				|| isFolderTimeRangeMetadata(metadata)) {
			return !excludedMetadatas.contains(metadata.getLocalCode());
		} else {
			return false;
		}
	}

	private boolean isFolderTimeRangeMetadata(Metadata metadata) {
		return metadata.getLocalCode().equals(Folder.TIME_RANGE) && metadata.getCode().startsWith(Folder.SCHEMA_TYPE + "_");
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/*@Deprecated
	@Override
	public LocalDate getDate(String metadata, DynamicDependencyValues values) {
		return super.getDate(metadata, values);
	}*/

}
