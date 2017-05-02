package com.autonubil.identity.ldapotp.api;

import com.autonubil.identity.ldap.api.LdapConnection;

public interface LdapOtpAdapterFactory {
	
	public LdapOtpAdapter create(LdapConnection connection); 
	
}
