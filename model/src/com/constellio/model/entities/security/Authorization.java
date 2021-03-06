package com.constellio.model.entities.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Authorization {

	AuthorizationDetails detail;

	List<String> grantedToPrincipals = new ArrayList<>();
	List<String> grantedOnRecords = new ArrayList<>();

	public Authorization() {
	}

	public Authorization(AuthorizationDetails detail, List<String> grantedToPrincipals,
			List<String> grantedOnRecords) {
		this.detail = detail;
		this.grantedToPrincipals = grantedToPrincipals;
		this.grantedOnRecords = grantedOnRecords;
	}

	public AuthorizationDetails getDetail() {
		return detail;
	}

	public void setDetail(AuthorizationDetails detail) {
		this.detail = detail;
	}

	public List<String> getGrantedToPrincipals() {
		return grantedToPrincipals;
	}

	public void setGrantedToPrincipals(List<String> grantedToPrincipals) {
		this.grantedToPrincipals = grantedToPrincipals;
	}

	public List<String> getGrantedOnRecords() {
		return grantedOnRecords;
	}

	public void setGrantedOnRecords(List<String> grantedOnRecords) {
		this.grantedOnRecords = grantedOnRecords;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return "Authorization{ " + detail + " granted to" + grantedToPrincipals + " on " + grantedOnRecords + "}";
	}
}
