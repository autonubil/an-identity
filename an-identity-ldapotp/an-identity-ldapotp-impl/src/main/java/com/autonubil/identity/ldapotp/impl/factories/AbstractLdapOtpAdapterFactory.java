package com.autonubil.identity.ldapotp.impl.factories;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapter;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapterFactory;

public abstract class AbstractLdapOtpAdapterFactory implements LdapOtpAdapterFactory {

	private static Log log = LogFactory.getLog(AbstractLdapOtpAdapterFactory.class);
	
	public abstract String[] getSupported();
	
	public abstract Class<? extends LdapOtpAdapter> getAdapterClass();
	
	public boolean supports(LdapConnection connection) {
		if(connection.getType()==null) {
			return false;
		}
		if(getSupported().length == 0) {
			return false;
		}
		for(String s : getSupported()) {
			if(s.equalsIgnoreCase(connection.getType())) return true;
		}
		return false;
	}
	
	
}
