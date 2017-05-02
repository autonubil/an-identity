package com.autonubil.identity.ldap.api;

import java.util.Date;
import java.util.List;

import javax.naming.directory.DirContext;

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.ldap.api.entities.LdapGroup;
import com.autonubil.identity.ldap.api.entities.LdapObject;
import com.autonubil.identity.ldap.api.entities.LdapUser;

public interface LdapConnection {
	
	
	public LdapUser authenticate(String username, String password, String otp) throws AuthException;
	
	public List<LdapUser> listUsers(String username, String cn, String search, LdapUser user, int offset, int max) throws Exception;
	
	public LdapUser getUserById(String id) throws Exception;

	public LdapUser getUserByName(String username) throws Exception;

	public LdapObject createUser(LdapUser user) throws Exception;

	public List<LdapGroup> listGroups(String search, int offset, int max) throws Exception;
	
	public LdapGroup getGroup(String id) throws Exception;

	public List<LdapGroup> getGroupsForUser(String userId, boolean recursive) throws Exception;
	public List<LdapGroup> getGroupsForGroup(String groupId, boolean recursive) throws Exception;

	public List<LdapUser> getUsersForGroup(String userId) throws Exception;
	
	public void addUserToGroup(String userId, String groupId) throws Exception;
	public void removeUserFromGroup(String userId, String groupId) throws Exception;
	

	void setPasswordExpiryDate(String id, Date date) throws Exception;

	void setUserExpiryDate(String id, Date date) throws Exception;

	public LdapObject saveUser(String id, LdapUser user) throws Exception;

	public void setPassword(String id, String newPassword) throws AuthException;
	
	public String getType();
	
	public DirContext getContext();
	
	public boolean supportsOtp();


}