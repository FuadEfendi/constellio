package com.constellio.model.services.schemas.calculators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.schemas.builders.CommonMetadataBuilder;

public class TokensCalculator2 implements MetadataValueCalculator<List<String>> {

	LocalDependency<List<String>> allAuthorizationsParam = LocalDependency
			.toAStringList(CommonMetadataBuilder.ALL_AUTHORIZATIONS);
	LocalDependency<List<String>> manualTokensParam = LocalDependency.toAStringList(CommonMetadataBuilder.MANUAL_TOKENS);

	public static List<String> getTokensForAuthorizationIds(List<String> authorizationIds, List<String> manualTokens) {
		List<String> calculatedTokens = new ArrayList<>();
		for (String auth : authorizationIds) {
			if (auth != null && !auth.startsWith("-")) {
				String[] authSplitted = auth.split("_");
				String accessCode = authSplitted[0];
				String roles = authSplitted[1];
				String authId = authSplitted[2];
				if (accessCode.length() <= 1) {
					calculatedTokens.add(accessCode + "_" + roles + "_" + authId);

				} else {
					for (int i = 0; i < accessCode.length(); i++) {
						calculatedTokens.add(accessCode.charAt(i) + "_" + roles + "_" + authId);
					}

				}
			}
		}
		calculatedTokens.addAll(manualTokens);

		return calculatedTokens;
	}

	@Override
	public List<String> calculate(CalculatorParameters parameters) {
		return getTokensForAuthorizationIds(parameters.get(allAuthorizationsParam), parameters.get(manualTokensParam));
	}

	@Override
	public List<String> getDefaultValue() {
		return Collections.emptyList();
	}

	@Override
	public MetadataValueType getReturnType() {
		return MetadataValueType.STRING;
	}

	@Override
	public boolean isMultiValue() {
		return true;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(allAuthorizationsParam, manualTokensParam);
	}
}
