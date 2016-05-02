package com.constellio.model.services.schemas.builders;

import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MetadataBuilder_CopyReferencedSearchablesFlagTest extends MetadataBuilderTest {

	@Test
	public void givenCopyReferencedSearchablesFlagUndefinedOnMetadataWithoutInheritanceWhenBuildingThenSingleValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING);

		build();

		assertThat(metadataWithoutInheritance.isCopyReferencedSearchables()).isFalse();
	}

	@Test
	public void givenCopyReferencedSearchablesFlagUndefinedOnMetadataWithoutInheritanceWhenModifyingThenSingleValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isCopyReferencedSearchables()).isFalse();
	}

	@Test
	public void givenCopyReferencedSearchablesFlagSetToFalseOnMetadataWithoutInheritanceWhenBuildingThenSingleValue()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setCopyReferencedSearchables(false);

		build();

		assertThat(metadataWithoutInheritance.isCopyReferencedSearchables()).isFalse();
	}

	@Test
	public void givenCopyReferencedSearchablesFlagSetToFalseOnMetadataWithoutInheritanceWhenModifyingThenSingleValue()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setCopyReferencedSearchables(false);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isCopyReferencedSearchables()).isFalse();
	}

	@Test
	public void givenCopyReferencedSearchablesFlagSetToTrueOnMetadataWithoutInheritanceWhenBuildingThenCopyReferencedSearchables()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setCopyReferencedSearchables(true);

		build();

		assertThat(metadataWithoutInheritance.isCopyReferencedSearchables()).isTrue();
	}

	@Test
	public void givenCopyReferencedSearchablesFlagSetToTrueOnMetadataWithoutInheritanceWhenModifyingThenCopyReferencedSearchables()
			throws Exception {
		metadataWithoutInheritanceBuilder.setType(STRING).setCopyReferencedSearchables(true);

		buildAndModify();

		assertThat(metadataWithoutInheritanceBuilder.isCopyReferencedSearchables()).isTrue();
	}

	@Test
	public void givenMutlivalueFlagOnMetadataWithInheritanceWhenBuildingThenSetToInheritedValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setCopyReferencedSearchables(true);

		build();

		assertThat(metadataWithInheritance.isCopyReferencedSearchables()).isTrue();
	}

	@Test
	public void givenCopyReferencedSearchablesFlagOnMetadataWithInheritanceWhenModifyingThenSetToInheritedValue()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setCopyReferencedSearchables(true);

		buildAndModify();

		assertThat(metadataWithInheritanceBuilder.isCopyReferencedSearchables()).isTrue();
	}

	@Test
	public void givenTrueFlagModifiedInInheritedMetadataBuilderThenModifiedInMetadataWithHeritance()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setCopyReferencedSearchables(true);
		assertThat(metadataWithInheritanceBuilder.isCopyReferencedSearchables()).isTrue();

	}

	@Test
	public void givenFalseFlagModifiedInInheritedMetadataBuilderThenModifiedInMetadataWithHeritance()
			throws Exception {
		inheritedMetadataBuilder.setType(STRING).setCopyReferencedSearchables(false);
		assertThat(metadataWithInheritanceBuilder.isCopyReferencedSearchables()).isFalse();

	}
}
