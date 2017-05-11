package com.autonubil.identity.ldap.impl.util.factories.ipa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import com.autonubil.identity.auth.api.entities.Notification;
import com.autonubil.identity.auth.api.entities.Notification.LEVEL;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.ldap.api.LdapSearchResultMapper;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomField;
import com.autonubil.identity.ldap.api.entities.LdapGroup;
import com.autonubil.identity.ldap.api.entities.LdapObject;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldap.impl.util.factories.AbstractLdapConnection;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.util.TokenGenerator;
import com.autonubil.identity.util.ldap.LdapEncoder;

public class IpaConnection extends AbstractLdapConnection  {
	
	
	public static final String DNA_MAGIC = "-1";
	
	public static final String[] userObjectClasses = new String[] {
			"ipaobject",
			"person",
			"top",
			"ipasshuser",
			"inetorgperson",
			"organizationalperson",
			"krbticketpolicyaux",
			"krbprincipalaux",
			"inetuser",
			"posixaccount",
			"ipaSshGroupOfPubKeys"
		};
	
	public static final String[] groupObjectClasses = new String[] {
			//"mepManagedEntry",
			"ipaobject",
			"posixgroup",
			"top"
		};

	public static final String[] userAttributes = new String[] { 
			"objectClass", 
			"uid", 
			"ipaUniqueId", 
			"mail" , 
			"cn", 
			"displayName", 
			"krbPrincipalName", 
			"krbLoginFailedCount", 
			"krbLastSuccessfulAuth", 
			"telephoneNumber", 
			"mobileTelephoneNumber", 
			"sn", 
			"krbPrincipalExpiration", 
			"krbPasswordExpiration", 
			"ou", 
			"o", 
			"gidNumber", 
			"uidNumber", 
			"physicalDeliveryOfficeName" 
		};
	
	public static final String[] groupAttributes = new String[] { "ipaUniqueId", "cn", "gidNumber"};
	
	
	public IpaConnection(LdapConfig config, String password, List<LdapCustomsFieldConfig> fields, MailService mailService) {
		super(config, fields,mailService);
		this.ctx = connect(config.getAdminBindDn(), password, null);
	}
	
	@Override
	public String[] getUserObjectClasses() {
		return userObjectClasses;
	}
	
	@Override
	public String[] getUserAttributes() {
		List<String> all = new ArrayList<>();
		for(String s : userAttributes) {
			if(!all.contains(s)) all.add(s);
		}
		for(LdapCustomsFieldConfig f : getCustomFields()) {
			if(!all.contains(f.getAttributeName())) all.add(f.getAttributeName());
		}
		String[] out = new String[all.size()];
		all.toArray(out);
		return out;
	}
	
	@Override
	public String[] getGroupAttributes() {
		List<String> all = new ArrayList<>();
		for(String s : groupAttributes) {
			if(!all.contains(s)) all.add(s);
		}
		for(LdapCustomsFieldConfig f : getCustomFields()) {
			if(!all.contains(f.getAttributeName())) all.add(f.getAttributeName());
		}
		String[] out = new String[all.size()];
		all.toArray(out);
		return out;
	}

	@Override
	public String getUserSearchBase() {
		return "cn=users,cn=accounts,"+getBaseDn();
	}
	
	@Override
	public String getUserSearchFilter(String username, String cn, String search, LdapUser user) {
		List<String> conditions = new ArrayList<>();
		conditions.add("(&");
		conditions.add("(objectClass=inetuser)");
		if(!StringUtils.isEmpty(username)) {
			if(username.indexOf("@")>-1) {
				conditions.add(String.format("(krbPrincipalName=%1$s)",LdapEncoder.escapeLDAPSearchFilter(username)));
			} else {
				conditions.add(String.format("(|(uid=%1$s)(krbPrincipalName=%1$s@*))",LdapEncoder.escapeLDAPSearchFilter(username)));
			}
		}
		if(user!=null && !StringUtils.isEmpty(user.getUsername())) {
			conditions.add(String.format("(|(uid=%1$s)(mail=%2$s))",LdapEncoder.escapeLDAPSearchFilter(user.getUsername()),LdapEncoder.escapeLDAPSearchFilter(user.getMail())));
		}
		if(!StringUtils.isEmpty(cn)) {
			conditions.add(String.format("(cn=%1$s)",LdapEncoder.escapeLDAPSearchFilter(cn)));
		}
		if(!StringUtils.isEmpty(search)) {
			conditions.add(String.format("(|(uid=%1$s*)(mail=%1$s*)(ipaUniqueId=%1$s*)(displayName=%1$s*))",LdapEncoder.escapeLDAPSearchFilter(search)));
		}
		conditions.add(")");
		return StringUtils.join(conditions,"");
	}
	
	@Override
	public String getUserByIdFilter(String uid) {
		return String.format("(&(objectClass=inetuser)(ipaUniqueId=%1$s))",LdapEncoder.escapeLDAPSearchFilter(uid));
	}
	
	@Override
	public String getUserByNameFilter(String name) {
		return String.format("(&(objectClass=inetuser)(|(uid=%1$s)(mail=%1$s)))",LdapEncoder.escapeLDAPSearchFilter(name));
	}
	
	@Override
	public String getGroupSearchBase() {
		return "cn=groups,cn=accounts,"+getBaseDn();
	}
	
	@Override
	public String getGroupSearchFilter(String search) {
		return String.format("(&(objectClass=ipaUserGroup)(cn=%1$s*))",LdapEncoder.escapeLDAPSearchFilter(search));
	}
	
	@Override
	public String getGroupGetFilter(String id) {
		return String.format("(&(objectClass=ipaUserGroup)(ipaUniqueID=%1$s))",LdapEncoder.escapeLDAPSearchFilter(id));
	}

	public String getGroupForUserFilter(LdapObject user) throws NamingException {
		return String.format("(&(objectClass=groupOfNames)(member=%1$s))", LdapEncoder.escapeDn(user.getDn()));
	}

	public String getGroupForGroupFilter(LdapObject user) throws NamingException {
		return String.format("(&(objectClass=groupOfNames)(member=%1$s))", LdapEncoder.escapeDn(user.getDn()));
	}
	
	@Override
	public LdapUser getUser(LdapUser ou, SearchResult r) throws NamingException {
		super.getUser(ou,r);
    	ou.setMail(getAttribute(r, "mail","")+"");
    	ou.setOrganization(getAttribute(r, "o","")+"");
    	ou.setDepartment(getAttribute(r, "ou","")+"");
    	ou.setId(getAttribute(r, "ipaUniqueId","")+"");
    	ou.setPasswordExpires(getAttribute(r, "krbPasswordExpiration", new Date(-1)));
    	ou.setUserExpires(getAttribute(r, "krbPrincipalExpiration", new Date(-1)));
    	ou.setDisplayName(getAttribute(r, "displayName", ""));
    	ou.setCn(getAttribute(r, "cn", ""));
    	ou.setSn(getAttribute(r, "sn", ""));
    	ou.setPhone(getAttribute(r, "telephoneNumber", ""));
    	ou.setMobilePhone(getAttribute(r, "mobileTelephoneNumber", ""));
    	ou.setUsername(getAttribute(r, "uid","")+"");
    	ou.setAccountName(getAttribute(r, "krbPrincipalName","")+"");
    	
    	if(ou.getPasswordExpires()==null) {
    	} else if(ou.getPasswordExpires().getTime()<1) {
    	} else {
    		long t = ou.getPasswordExpires().getTime();
    		long n = System.currentTimeMillis();
    		if(t - n < (1*24*60*60*1000)) {
    			ou.addNotification(new Notification(LEVEL.ERROR, "The password for this account will expire in less than a day ("+new Date(t)+")"));
    		} else if(t - n < (long)(3*24*60*60*1000)) {
    			ou.addNotification(new Notification(LEVEL.WARN, "The password for this account will expire in less than 3 days ("+new Date(t)+")"));
    		} else if(t - n < (long)(5*24*60*60*1000)) {
    			ou.addNotification(new Notification(LEVEL.INFO, "The password for this account will expire in less than 5 days ("+new Date(t)+")"));
    		}
    	}
    	
    	if(ou.getUserExpires()==null) {
    	} else if(ou.getUserExpires().getTime()<1) {
    	} else {
    		long t = ou.getUserExpires().getTime();
    		long n = System.currentTimeMillis();
    		if(t - n < (long)(7*24*60*60*1000)) {
    			ou.addNotification(new Notification(LEVEL.ERROR, "This account will expire in less than 7 days ("+new Date(t)+")"));
    		} else {
    			ou.addNotification(new Notification(LEVEL.INFO, "This account will expire: "+ou.getUserExpires()));
    		}
    	}
    	
    	{
	    	String s = getAttribute(r, "krbLoginFailedCount", "0");
	    	if(s.compareTo("0")!=0) {
				ou.addNotification(new Notification(LEVEL.WARN, "There have been "+s+" unsuccessful login attempts on this account"));
	    	}
    	}

    	{
	    	Date d = getAttribute(r, "krbLastSuccessfulAuth", new Date(-1));
	    	if(d.getTime()>0) {
	    		ou.addNotification(new Notification(LEVEL.INFO, "Last login: "+(d)));
	    	}
    	}
    	
    	return ou;
	}


	public LdapGroup getGroup(LdapGroup group, SearchResult r) throws NamingException {
		group.setSourceId(config.getId());
		group.setSourceName(config.getName());
		group.setDn(r.getNameInNamespace());
		group.setId(getAttribute(r, "ipaUniqueID", ""));
		group.setDisplayName(getAttribute(r, "cn", "[unnamed group]"));
    	return group;
	}	

	@Override
	public void setUserExpiryDate(String id, Date date) {
		try {
			LdapUser u = getUserById(id);
			updateUserAttribute(u.getDn(), "krbPrincipalExpiration", date, u.getUserExpires()!=null);
		} catch (Exception e) {
			log.error("error setting user expiry", e);
		}
	}
	
	@Override
	public void setPasswordExpiryDate(String id, Date date) {
		try {
			LdapUser u = getUserById(id);
			updateUserAttribute(u.getDn(), "krbPasswordExpiration", date, u.getPasswordExpires()!=null);
		} catch (Exception e) {
			log.error("error password user expiry", e);
		}
	}
	
	@Override
	public void setPassword(String id, String newPassword) throws AuthException {
		try {
			LdapObject u = getUserById(id);
			
			String x = TokenGenerator.getToken(18);
			updateUserAttribute(u.getDn(), "userPassword", x, true);
			
			DirContext dc = connect(u.getDn(), x, null);
			ModificationItem[] mi =
					new ModificationItem[] {
						new  ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword",newPassword))
				};
			dc.modifyAttributes(u.getDn(), mi);			
			
		} catch (Exception e) {
			log.error("error password", e);
			throw new AuthException(e.getMessage());
		}
	}

	@Override
	public LdapUser createUserInternal(LdapUser user) throws Exception {
		
		if(user.getUsername()==null) {
			throw new RuntimeException("username must not be null");
		}
		
		if(user.getUsername().compareTo(LdapEncoder.escapeLDAPSearchFilter(user.getUsername()))!=0) {
			throw new RuntimeException("invalid username");
		}
		
		if(getUserByName(user.getUsername())!=null) {
			throw new RuntimeException("user already exists");
		}
		
		Map<String,Object> values = new HashMap<>();
		values.put("mail", user.getMail());
		if(!StringUtils.isEmpty(user.getDepartment())) {
			values.put("ou", user.getDepartment());
		}
		if(user.getUserExpires()!=null && user.getUserExpires().getTime()>0) {
			values.put("krbPrincipalExpiration", user.getUserExpires());
		}
		
		values.put("gidNumber", "-1");
		values.put("uidNumber", "-1");
		values.put("displayName", user.getDisplayName());
		values.put("cn", user.getCn());
		if(!StringUtils.isEmpty(user.getSn())) {
			values.put("sn", user.getSn());
		}
		if(!StringUtils.isEmpty(user.getPhone())) {
			values.put("telephoneNumber", user.getPhone());
		}
		if(!StringUtils.isEmpty(user.getMobilePhone())) {
			values.put("mobileTelephoneNumber", user.getMobilePhone());
		}
		// ignore these
		//values.put("uid", user.getUsername());
		//values.put("krbPrincipalName", user.getAccountName());

		String domain = LdapEncoder.getDomainComponents(getBaseDn()).toUpperCase();

		//kerberos shit
		values.put("krbCanonicalName", user.getUsername()+"@"+domain);
		values.put("krbPrincipalName", user.getUsername()+"@"+domain);

		// posix shit
		values.put("loginShell", "/bin/bash");
		values.put("homeDirectory", "/home/"+user.getUsername());
		
		createEntry("uid="+user.getUsername()+","+getUserSearchBase(),getUserObjectClasses(),values);
		
		
		NamingEnumeration<SearchResult> results = null;

		// add user to default group:
    	results = list(getGroupSearchBase(),"(&(objectClass=ipausergroup)(cn=ipausers))", getGroupAttributes());
    	if(results.hasMore()) {
    		Attribute a = new BasicAttribute("member","uid="+user.getUsername()+","+getUserSearchBase());
    		ModificationItem[] items = new ModificationItem[] { new ModificationItem(DirContext.ADD_ATTRIBUTE, a )};
    		modifyEntry(results.next().getNameInNamespace(), items);
    	}
		
	    results = list(getUserSearchBase(),"uid="+user.getUsername(), getUserAttributes());
    	if(results.hasMore()) {
    		SearchResult sr = results.next();
			LdapSearchResultMapper<LdapUser> m = new LdapSearchResultUserMapper();
    		return m.map(sr);
    	}
    	return null;
	}

	@Override
	public LdapObject saveUser(String id, LdapUser user) throws Exception {
		Map<String,Object> values = new HashMap<>();
		values.put("mail", user.getMail());
		values.put("uid", user.getUsername());
		values.put("o", user.getOrganization());
		values.put("ou", user.getDepartment());
		values.put("displayName", user.getDisplayName());
		values.put("cn", user.getCn());
		values.put("sn", user.getSn());
		values.put("telephoneNumber", user.getPhone());
		values.put("mobileTelephoneNumber", user.getMobilePhone());
		for(LdapCustomField lcf : user.getCustomFields()) {
			values.put(lcf.getAttributeName(), lcf.getValues());
		}
		LdapSearchResultMapper<SearchResult> mapper =  new LdapSearchResultMapper<SearchResult>() {public SearchResult map(SearchResult r) {return r;}};
		SearchResult sr = get(getUserSearchBase(),getUserByIdFilter(id), getUserAttributes(), mapper);
		updateEntry(sr,values);
		return getUserById(id);
	}
	
	@Override
	public Map<String, Object> addConnectionProperties() {
		Map<String, Object> out =  super.addConnectionProperties();
		out.put("java.naming.ldap.attributes.binary", "ipatokenOTPkey");
		return out;
	}
	
}
