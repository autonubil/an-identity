package com.autonubil.identity.ldap.impl.util.factories.apacheds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import com.autonubil.identity.ldap.api.UnsupportedOperation;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.api.entities.LdapGroup;
import com.autonubil.identity.ldap.api.entities.LdapObject;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldap.impl.util.factories.AbstractLdapConnection;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.util.TokenGenerator;
import com.autonubil.identity.util.ldap.LdapEncoder;

public class ApacheDsConnection extends AbstractLdapConnection  {
	
	public static final String[] userObjectClasses = new String[] {
			"ipaobject",
			"person",
			"top",
			"inetorgperson",
			"organizationalperson",
			"krbticketpolicyaux",
			"krbprincipalaux",
			"inetuser",
			"posixaccount",
			"ipaSshGroupOfPubKeys",
			"mepOriginEntry" 
		};
	public static final String[] userAttributes = new String[] { "entryUUID",  "uid", "mail" , "cn",  "vebLoginName", "telephoneNumber", "mobileTelephoneNumber", "sn", "displayName", "ou", "o", "vebLoginName" };
	public static final String[] groupAttributes = new String[] { "entryUUID", "cn" };
	
	
	
	
	public ApacheDsConnection(LdapConfig config, String password, List<LdapCustomsFieldConfig> fields, MailService mailService) {
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

	public String getUserSearchBase() {
		return config.getRootDse();
	}
	
	public String getUserSearchFilter(String username, String cn, String search, LdapUser user) {
		List<String> conditions = new ArrayList<>();
		conditions.add("(&");
		conditions.add("(objectClass=inetOrgPerson)");
		if(!StringUtils.isEmpty(username)) {
			conditions.add(String.format("(uid=%1$s)",LdapEncoder.escapeLDAPSearchFilter(username)));
		}
		if(!StringUtils.isEmpty(cn)) {
			conditions.add(String.format("(cn=%1$s)",LdapEncoder.escapeLDAPSearchFilter(cn)));
		}
		if(!StringUtils.isEmpty(search)) {
			conditions.add(String.format("(|(uid=%1$s*)(mail=%1$s*)(entryUUID=%1$s*)(displayName=%1$s*))",search));
		}
		if(user!=null && !StringUtils.isEmpty(user.getUsername())) {
			conditions.add(String.format("(|(uid=%1$s)(mail=%2$s))",LdapEncoder.escapeLDAPSearchFilter(user.getUsername()),LdapEncoder.escapeLDAPSearchFilter(user.getMail())));
		}
		conditions.add(")");
		return StringUtils.join(conditions,"");
	}
	
	public String getUserByIdFilter(String uid) {
		return String.format("(&(objectClass=inetOrgPerson)(entryUUID=%1$s))",LdapEncoder.escapeLDAPSearchFilter(uid));
	}
	
	@Override
	public String getUserByNameFilter(String name) {
		return String.format("(&(objectClass=inetOrgPerson)(|(uid=%1$s)(mail=%1$s)))",LdapEncoder.escapeLDAPSearchFilter(name));
	}
	
	public String getGroupSearchBase() {
		return config.getRootDse();
	}
	
	public String getGroupSearchFilter(String search) {
		return String.format("(&(objectClass=groupOfNames)(cn=%1$s*))",LdapEncoder.escapeLDAPSearchFilter(search));
	}
	
	public String getGroupGetFilter(String id) {
		return String.format("(&(objectClass=groupOfNames)(entryUUID=%1$s))",LdapEncoder.escapeLDAPSearchFilter(id));
	}

	public String getGroupForUserFilter(LdapObject user) {
		return String.format("(&(objectClass=groupOfNames)(member=%1$s))", LdapEncoder.escapeDn(user.getDn()));
	}

	public String getGroupForGroupFilter(LdapObject user) {
		return String.format("(&(objectClass=groupOfNames)(member=%1$s))", LdapEncoder.escapeDn(user.getDn()));
	}
	
	@Override
	public LdapUser getUser(LdapUser user, SearchResult r) throws NamingException {
		super.getUser(user, r);
    	user.setMail(getAttribute(r, "mail","")+"");
    	user.setOrganization(getAttribute(r, "o","")+"");
    	user.setDepartment(getAttribute(r, "ou","")+"");
    	user.setId(getAttribute(r, "entryUUID","")+"");
    	user.setPasswordExpires(getAttribute(r, "krbPasswordExpiration", new Date(-1)));
    	user.setUserExpires(new Date(-1));
    	user.setDisplayName(getAttribute(r, "displayName",""));
    	user.setCn(getAttribute(r, "cn",""));
    	user.setSn(getAttribute(r, "sn",""));
    	user.setPhone(getAttribute(r, "telephoneNumber", ""));
    	user.setMobilePhone(getAttribute(r, "mobileTelephoneNumber", ""));
    	user.setUsername(getAttribute(r, "uid",""));
    	return user;
	}

	public LdapGroup getGroup(LdapGroup group, SearchResult r) throws NamingException {
		group.setSourceId(config.getId());
		group.setSourceName(config.getName());
		group.setDn(r.getNameInNamespace());
		group.setId(getAttribute(r, "entryUUID", ""));
		group.setDisplayName(getAttribute(r, "cn", "[unnamed group]"));
    	return group;
	}	

	@Override
	public void setUserExpiryDate(String id, Date date) throws UnsupportedOperation {
		throw new UnsupportedOperation("cannot create user in ApacheDS Directory");
	}
	
	@Override
	public void setPasswordExpiryDate(String id, Date date) throws UnsupportedOperation {
		throw new UnsupportedOperation("cannot save user in ApacheDS Directory");
	}
	
	@Override
	public void setPassword(String id, String newPassword) {
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
		}
	}
	
	@Override
	public LdapUser createUserInternal(LdapUser user) throws Exception {
		throw new UnsupportedOperation("cannot create user in ApacheDS Directory");
	}
	
	@Override
	public LdapObject saveUser(String id, LdapUser user) throws Exception {
		throw new UnsupportedOperation("cannot save user in ApacheDS Directory");
	}
	
}
