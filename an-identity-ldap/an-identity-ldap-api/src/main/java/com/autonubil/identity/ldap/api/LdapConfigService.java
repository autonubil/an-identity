package com.autonubil.identity.ldap.api;

import java.util.List;

import com.autonubil.identity.ldap.api.entities.LdapConfig;

public interface LdapConfigService {

	List<LdapConfig> list(String id, String order, Boolean useAsAuth);

	LdapConnection connect(String id);

}
