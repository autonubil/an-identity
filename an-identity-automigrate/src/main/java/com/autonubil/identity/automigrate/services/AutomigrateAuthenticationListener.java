package com.autonubil.identity.automigrate.services;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autonubil.identity.auth.api.AuthenticationListener;
import com.autonubil.identity.auth.api.Credentials;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.automigrate.entities.AutomigrateConfig;
import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.entities.LdapUser;

@Component
public class AutomigrateAuthenticationListener implements AuthenticationListener {

	@Autowired
	private AutomigrateConfigService automigrateConfigService; 
	
	@Autowired
	private LdapConfigService ldapConfigService;
	
	@Override
	public void userLogin(Credentials c, Identity i) {
		List<AutomigrateConfig> configs = automigrateConfigService.list(null,i.getUser().getSourceId(),null,0,1000);
		for(AutomigrateConfig config : configs) {
			try {
				LdapConnection lc = ldapConfigService.connect(config.getToLdap());
				LdapUser u = lc.getUserByName(((LdapUser)i.getUser()).getUsername());
				automigrateConfigService.log(config.getFromLdap(), i.getUser().getId(), true, "");
			} catch (Exception e) {
				automigrateConfigService.log(config.getFromLdap(), i.getUser().getId(), false, ExceptionUtils.getStackTrace(e));
			}
		}
	}

}
