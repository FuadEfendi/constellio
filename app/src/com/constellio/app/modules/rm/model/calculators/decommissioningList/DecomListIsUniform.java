package com.constellio.app.modules.rm.model.calculators.decommissioningList;

import static java.util.Arrays.asList;

import java.util.List;

import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.wrappers.DecommissioningList;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.schemas.MetadataValueType;

public class DecomListIsUniform implements MetadataValueCalculator<Boolean> {

	LocalDependency<CopyRetentionRule> uniformCopyRuleParam = LocalDependency.toAStructure(DecommissioningList.UNIFORM_COPY_RULE);
	LocalDependency<CopyType> uniformCopyTypeParam = LocalDependency.toAnEnum(DecommissioningList.UNIFORM_COPY_TYPE);
	LocalDependency<String> uniformRuleParam = LocalDependency.toAReference(DecommissioningList.UNIFORM_RULE);
	LocalDependency<String> uniformCategoryParam = LocalDependency.toAReference(DecommissioningList.UNIFORM_CATEGORY);

	@Override
	public Boolean calculate(CalculatorParameters parameters) {
		CopyRetentionRule uniformCopyRule = parameters.get(uniformCopyRuleParam);
		CopyType uniformCopyType = parameters.get(uniformCopyTypeParam);
		String uniformRule = parameters.get(uniformRuleParam);
		String uniformCategory = parameters.get(uniformCategoryParam);

		if (uniformCopyRule != null
				&& uniformCopyType != null
				&& uniformRule != null
				&& uniformCategory != null) {
			return true;
		} else {
			return false;
		}
		//returne true si tous les parametres sont non-nulls, retourne false sinon
	}

	@Override
	public Boolean getDefaultValue() {
		return false;
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.BOOLEAN;
	}

	@Override
	public boolean isMultiValue() {
		return false;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return asList(uniformCopyRuleParam, uniformCopyTypeParam, uniformRuleParam, uniformCategoryParam);
	}
}
