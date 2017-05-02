package com.autonubil.identity.ldapusers.impl.controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.entities.LdapGroup;
import com.autonubil.identity.ldap.api.entities.LdapObject;
import com.autonubil.identity.ldap.api.entities.LdapUser;

@RestController
@RequestMapping("/autonubil")
public class LdapUserController {
	
	private static Log log = LogFactory.getLog(LdapUserController.class);

	@Autowired
	private LdapConfigService ldapConfigService;

	@RequestMapping(value="/api/ldapusers/{connectionId}/users",method={RequestMethod.GET})
	public List<LdapUser> listUsers(
			@PathVariable String connectionId, 
			@RequestParam(required=false,defaultValue="") String username, 
			@RequestParam(required=false,defaultValue="") String cn, 
			@RequestParam(required=false,defaultValue="") String search, 
			@RequestParam(required=false,defaultValue="0") int offset, 
			@RequestParam(required=false,defaultValue="100") int max
		) throws Exception {
		
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).listUsers(username, cn, search, null, offset, max);
	}

	@RequestMapping(value="/api/ldapusers/me",method={RequestMethod.PUT})
	public LdapObject updateSelf(
			@RequestBody LdapUser user
			) throws Exception {
		
		AuthUtils.checkLoggedIn();
		
		User ud = IdentityHolder.get().getUser();
		
		LdapUser lu = ldapConfigService.connect(ud.getSourceId()).getUserById(ud.getId());
		
		lu.setPhone(user.getPhone());
		lu.setMobilePhone(user.getMobilePhone());
		
		
		return ldapConfigService.connect(ud.getSourceId()).saveUser(lu.getId(), lu);
	}
	
	@RequestMapping(value="/api/ldapusers/{connectionId}/users",method={RequestMethod.POST})
	public LdapObject createUser(
			@PathVariable String connectionId,
			@RequestBody LdapUser user
		) throws Exception {
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).createUser(user);
	}
	
	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}",method={RequestMethod.GET})
	public LdapObject getUser(@PathVariable String connectionId, @PathVariable String userId) throws Exception {
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).getUserById(userId);
	}
	
	
	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}",method={RequestMethod.PUT})
	public LdapObject updateUser(@PathVariable String connectionId, @PathVariable String userId, @RequestBody LdapUser ldapUser) throws Exception {
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).saveUser(userId, ldapUser);
	}

	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}/groups",method={RequestMethod.GET})
	public List<LdapGroup> getUserGroups(@PathVariable String connectionId, @PathVariable String userId, @RequestParam(required=false,defaultValue="true") boolean recursive) throws Exception {
		AuthUtils.checkAdmin();
		return ldapConfigService.connect(connectionId).getGroupsForUser(userId, recursive);
	}

	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}/groups",method={RequestMethod.POST})
	public void addUserToGroup(@PathVariable String connectionId, @PathVariable String userId, @RequestParam String groupId) throws Exception {
		AuthUtils.checkAdmin();
		ldapConfigService.connect(connectionId).addUserToGroup(userId,groupId);
	}
	
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}/groups",method={RequestMethod.DELETE})
	public void removeUserFromGroup(@PathVariable String connectionId, @PathVariable String userId, @RequestParam String groupId) throws Exception {
		AuthUtils.checkAdmin();
		ldapConfigService.connect(connectionId).removeUserFromGroup(userId,groupId);
	}
	
	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}/expiration_user",method={RequestMethod.PUT})
	public void setUserExpication(@PathVariable String connectionId, @PathVariable String userId, @RequestParam long date) throws Exception {
		AuthUtils.checkAdmin();
		ldapConfigService.connect(connectionId).setUserExpiryDate(userId, new Date(date));
	}
	
	@RequestMapping(value="/api/ldapusers/{connectionId}/users/{userId}/expiration_password",method={RequestMethod.PUT})
	public void setPasswordExpication(@PathVariable String connectionId, @PathVariable String userId, @RequestParam long date) throws Exception {
		AuthUtils.checkAdmin();
		ldapConfigService.connect(connectionId).setPasswordExpiryDate(userId, new Date(date));
	}
	
	
	
}
