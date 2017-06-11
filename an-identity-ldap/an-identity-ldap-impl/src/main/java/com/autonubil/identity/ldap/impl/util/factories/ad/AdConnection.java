package com.autonubil.identity.ldap.impl.util.factories.ad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.api.entities.LdapGroup;
import com.autonubil.identity.ldap.api.entities.LdapObject;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldap.impl.util.factories.AbstractLdapConnection;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.util.TokenGenerator;
import com.autonubil.identity.util.ldap.LdapEncoder;

public class AdConnection extends AbstractLdapConnection {
	
	public static final String[] userObjectClasses = new String[] { "organizationalPerson", "user", "userPrincipalName", "telephoneNumber", "mobileTelephoneNumber", "sn", "person", "top"};
	
	public static final String[] userAttributes = new String[] { "objectClass", "userPrincipalName", "sAMAccountName", "objectGUID", "mail" , "CN", "name", "accountExpires", "ou", "o" };
	public static final String[] groupAttributes = new String[] { "objectClass", "sAMAccountName", "objectGUID", "CN"  };
	
	private static Log log = LogFactory.getLog(AdConnection.class);

	public AdConnection(LdapConfig config, String password, List<LdapCustomsFieldConfig> customFields, MailService mailService) {
		super(config, customFields,mailService);
		this.setContext(connect(config.getAdminBindDn(), password, null));
	}
	
	public Map<String,Object> addConnectionProperties() {
		Hashtable<String, Object> env = new Hashtable<>();
		env.put("java.naming.ldap.attributes.binary", "objectGUID");
		env.put("java.naming.referral", "ignore");
		return env;
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
		return getConfig().getRootDse();
	}
	
	public String getUserByNameFilter(String name) {
		return String.format("(&(objectClass=user)(|(sAMAccountName=%1$s)(mail=%1$s)(userPrincipalName=%1$s)))",LdapEncoder.escapeLDAPSearchFilter(name));
	}

	public String getUserByIdFilter(String id) {
		String uidStr = id.replaceAll("[^a-zA-Z0-9]+", "").replaceAll("([a-zA-Z0-9]{2,2})", "\\\\$1");
		return String.format("(&(objectClass=user)(objectGUID=%1$s))", uidStr);
	}
	
	public String getUserSearchFilter(String username, String cn, String search, LdapUser user) {
		List<String> conditions = new ArrayList<>();
		conditions.add("(&");
		conditions.add("(objectClass=user)");
		if(!StringUtils.isEmpty(username)) {
			conditions.add(String.format("(|(sAMAccountName=%1$s)(userPrincipalName=%1$s))",LdapEncoder.escapeLDAPSearchFilter(username)));
		}
		if(!StringUtils.isEmpty(cn)) {
			conditions.add(String.format("(cn=%1$s)",LdapEncoder.escapeLDAPSearchFilter(cn)));
		}
		if(!StringUtils.isEmpty(search)) {
			conditions.add(String.format("(|(sAMAccountName=%1$s*)(cn=%1$s*)(userPrincipalName=%1$s*))",search));
		}
		if(user!=null && !StringUtils.isEmpty(user.getUsername())) {
			conditions.add(String.format("(|(sAMAccountName=%1$s)(cn=%1$s*)(userPrincipalName=%1$s@*)(mail=%1$s))",LdapEncoder.escapeLDAPSearchFilter(user.getUsername())));
		}
		conditions.add(")");
		return StringUtils.join(conditions,"");
	}
	
	@Override
	public String getGroupSearchBase() {
		return getConfig().getRootDse();
	}
	
	@Override
	public String getGroupGetFilter(String id) {
		String uidStr = id.replaceAll("[^a-zA-Z0-9]+", "").replaceAll("([a-zA-Z0-9]{2,2})", "\\\\$1");
		return String.format("(&(objectClass=group)(objectGUID=%1$s))",uidStr);
	}

	@Override
	public String getGroupSearchFilter(String search) {
		return String.format("(&(objectClass=group)(|(cn=%1$s*)(sAMAccountName=%1$s*)))",LdapEncoder.escapeLDAPSearchFilter(search));
	}
	
	public String getGroupForUserFilter(LdapObject user) {
		return String.format("(&(objectClass=group)(member=%1$s))", LdapEncoder.escapeDn(user.getDn()));
	}

	public String getGroupForGroupFilter(LdapObject user) {
		return String.format("(&(objectClass=group)(member=%1$s))", LdapEncoder.escapeDn(user.getDn()));
	}
	
	@Override
	public LdapUser getUser(LdapUser user, SearchResult r) throws NamingException {
		super.getUser(user, r);
    	user.setOrganization(getAttribute(r, "o",""));
    	user.setDepartment(getAttribute(r, "ou",""));
		byte[] b = (byte[])r.getAttributes().get("objectGUID").get();
		user.setId(Hex.encodeHexString(b));
		user.setDisplayName(getAttribute(r, "name", ""));
		user.setDn(r.getNameInNamespace());
		user.setDisplayName(getAttribute(r, "displayName", ""));
		user.setUsername(getAttribute(r, "sAMAccountName",""));
		user.setAccountName(getAttribute(r, "userPrincipalName",""));
    	user.setPhone(getAttribute(r, "telephoneNumber", ""));
    	user.setMobilePhone(getAttribute(r, "mobileTelephoneNumber", ""));
		user.setPasswordExpires(new Date(-1));
		
		long accountExpires = getAttribute(r, "accountExpires", 0l);
		
		log.info("account expires is: "+accountExpires);
		
		Date d = new Date(-1);
		if(accountExpires > 0) {
			d = winEpochToDate(accountExpires);
		}

		log.info("expires"+d);
		user.setUserExpires(d);
		user.setMail(getAttribute(r, "mail", ""));
		return user;
	}
	
	public LdapGroup getGroup(LdapGroup group, SearchResult r) throws NamingException {
		group.setDn(r.getNameInNamespace());
		byte[] b = (byte[])r.getAttributes().get("objectGUID").get();
		group.setId(Hex.encodeHexString(b));
		group.setSourceId(config.getId());
		group.setDisplayName(getAttribute(r, "CN", "[unnamed group]"));
    	return group;
	}
	
	public static Date winEpochToDate(long epoch) {
		GregorianCalendar adEpoch = new GregorianCalendar(1601,Calendar.JANUARY,1);
		adEpoch.set(Calendar.HOUR, 0);
		adEpoch.set(Calendar.MINUTE, 0);
		adEpoch.set(Calendar.SECOND, 0);
		Date d1 = adEpoch.getTime();
		long s = epoch / 10000;
		d1.setTime(d1.getTime()+s);
		return d1;
	}
	
	public static long dateToWinEpoch(Date date) {
		GregorianCalendar adEpoch = new GregorianCalendar(1601,Calendar.JANUARY,1);
		adEpoch.set(Calendar.HOUR, 0);
		adEpoch.set(Calendar.MINUTE, 0);
		adEpoch.set(Calendar.SECOND, 0);
		long base = adEpoch.getTimeInMillis();
		long t = date.getTime() - base;
		return t * 10000;
	}

	@Override
	public void setPassword(String id, String otp, String newPassword) throws AuthException {
		try {
			LdapObject u = getUserById(id);
			
			String x = TokenGenerator.getToken(18);
			{
				ModificationItem[] items = new ModificationItem[] {
						new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", ("\""+x+"\"").getBytes("UTF-16LE")))
					};
				this.getContext().modifyAttributes(u.getDn(), items);
			}
			
			DirContext dc = connect(u.getDn(), x, null);
			ModificationItem[] mi =
					new ModificationItem[] {
						new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", ("\""+newPassword+"\"").getBytes("UTF-16LE")))
				};
			dc.modifyAttributes(u.getDn(), mi);			
			
			log.info("unicodePassword updated successfully!");
			
		} catch (Exception e) {
			log.error("error password", e);
		}
		try {
			LdapObject u = getUserById(id);
			ModificationItem[] items = new ModificationItem[] {
				new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", ("\""+newPassword+"\"").getBytes("UTF-16LE")))
			};
			modifyEntry(u.getDn(), items);
		} catch (Exception e) {
			log.error("error password user expiry", e);
			throw new AuthException(e.getMessage());
		}
	}

	@Override
	public void setPasswordExpiryDate(String id, Date date) {
		// invalid operation
	}

	@Override
	public void setUserExpiryDate(String id, Date date) {
		try {
			LdapObject u = getUserById(id);
			updateUserAttribute(u.getDn(), "accountExpires", String.valueOf(dateToWinEpoch(date)), true);
		} catch (Exception e) {
			log.error("error setting user expiry", e);
		}
	}
	
	@Override
	public LdapUser createUserInternal(LdapUser user) throws Exception {
		
		throw new UnsupportedOperationException("cannot create user in Active Directory");
		
		/**
		Map<String,String> values = new HashMap<>();
		values.put("mail", user.getMail());
		values.put("o", user.getCompany());
		values.put("ou", user.getDepartment());
		values.put("CN", user.getDisplayName());
		values.put("sAMAccountName", user.getUsername());
		createUser("CN="+user.getDisplayName()+","+getUserSearchBase(),values);
		
		SearchControls searchControls = new SearchControls();
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    searchControls.setReturningAttributes(getUserAttributes());
    	NamingEnumeration<SearchResult> results = ctx.search(getUserSearchBase(),"CN="+user.getUsername(),searchControls);
    	if(results.hasMore()) {
    		SearchResult sr = results.next();
    		return getUser(sr);
    	}
    	return null;
    	**/
		
	}

	@Override
	public LdapObject saveUser(String id, LdapUser user) {
		return null;
	}
	
}
