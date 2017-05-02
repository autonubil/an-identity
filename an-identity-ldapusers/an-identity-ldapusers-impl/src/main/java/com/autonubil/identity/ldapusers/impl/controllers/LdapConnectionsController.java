package com.autonubil.identity.ldapusers.impl.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.entities.LdapConfig;

@RestController
@RequestMapping("/autonubil")
public class LdapConnectionsController {
	
	@Autowired
	private LdapConfigService ldapConfigService;

	@RequestMapping(value = "/api/ldapusers/connections", method = { RequestMethod.GET })
	public List<LdapConfig> listConnections() throws Exception {
		return ldapConfigService.list(null, null, null);
	}

}
