package com.autonubil.identity.ldap.impl.util.factories;

import java.util.List;

import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.LdapConnectionType;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.mail.api.MailService;

public interface LdapConnectionFactory {

	public LdapConnectionType getType();

	public LdapConnection connect(LdapConfig config, String username, String password, List<LdapCustomsFieldConfig> customFields, MailService mailService);

}
