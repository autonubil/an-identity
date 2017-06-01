package com.autonubil.identity.ldapusers.impl.controllers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.entities.LdapGroup;

@RestController
@RequestMapping("/autonubil")
public class LdapGroupController {
	
	private static Log log = LogFactory.getLog(LdapGroupController.class);

	@Autowired
	private LdapConfigService ldapConfigService;

	@RequestMapping(value="/api/ldapusers/{connectionId}/groups",method={RequestMethod.GET})
	public List<LdapGroup> listGroups(@PathVariable String connectionId, @RequestParam(required=false,defaultValue="") String search, @RequestParam(required=false,defaultValue="0") int offset, @RequestParam(required=false,defaultValue="25") int max) throws Exception {
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).listGroups(search, 0, 100);
	}
	
	@RequestMapping(value="/api/ldapusers/{connectionId}/groups/{groupId}",method={RequestMethod.GET})
	public LdapGroup getGroup(@PathVariable String connectionId, @PathVariable String groupId) throws Exception {
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).getGroup(groupId);
	}
	
	
}
