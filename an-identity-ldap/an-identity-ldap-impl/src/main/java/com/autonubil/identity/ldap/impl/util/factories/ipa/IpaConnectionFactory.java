package com.autonubil.identity.ldap.impl.util.factories.ipa;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.LdapConnectionType;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.impl.util.factories.LdapConnectionFactory;
import com.autonubil.identity.mail.api.MailService;

@Component
public class IpaConnectionFactory implements LdapConnectionFactory {

	@Override
	public LdapConnectionType getType() {
		return new LdapConnectionType("IPA","FreeIPA LDAP Server");
	}
	
	@Override
	public LdapConnection connect(LdapConfig config, String username, String password, List<LdapCustomsFieldConfig> customFields, MailService mailService) {
		return new IpaConnection(config, password, customFields,mailService); 
	}

	
	
}
