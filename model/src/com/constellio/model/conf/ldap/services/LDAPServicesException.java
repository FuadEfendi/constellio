package com.constellio.model.conf.ldap.services;

public class LDAPServicesException extends Exception {
	public LDAPServicesException(Throwable e) {
		super(e);
	}

	public LDAPServicesException() {

	}

	public static class CouldNotConnectUserToLDAP extends LDAPServicesException {

		public CouldNotConnectUserToLDAP(Throwable e) {
			super(e);
		}

		public CouldNotConnectUserToLDAP() {
			super();
		}
	}
}
