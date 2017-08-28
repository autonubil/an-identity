package com.autonubil.identity.ldap.impl.util.factories;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autonubil.identity.audit.api.AuditLoggerHelper;
import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.AuthenticationFailedException;
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.LdapSearchResultMapper;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapConfig.ENCRYPTION;
import com.autonubil.identity.ldap.api.entities.LdapCustomField;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.api.entities.LdapGroup;
import com.autonubil.identity.ldap.api.entities.LdapObject;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldap.impl.services.TrustManagerDelegate;
import com.autonubil.identity.ldap.impl.util.ThreadLocalSocketFactory;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.util.ldap.LdapEncoder;
import com.autonubil.identity.util.ssl.CertUtil;
import com.autonubil.identity.util.ssl.CustomTrustManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class AbstractLdapConnection implements LdapConnection {

	private MailService mailService;

	protected static Log log = LogFactory.getLog(AbstractLdapConnection.class);

	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss\\Z");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

	protected LdapConfig config;
	private static final ThreadLocal<DirContext> localCtx = new ThreadLocal<>();
	private DirContext ctx;

	private Hashtable<String, Object> env;

	private List<LdapCustomsFieldConfig> customFields;

	public AbstractLdapConnection(LdapConfig config, List<LdapCustomsFieldConfig> customFields,
			MailService mailService) {
		this.config = config;
		this.mailService = mailService;
		this.setCustomFields(customFields);
	}

	@Override
	public String getBaseDn() {
		return config.getRootDse();
	}

	@Override
	public void addEnv(String envName, Object envValue) {
		this.env.put(envName, envValue);
	}

	@Override
	public Date parseDate(String in) throws ParseException {
		try {
			return sdf1.parse(in);
		} catch (Exception e) {
		}
		try {
			return sdf2.parse(in);
		} catch (Exception e) {
		}
		try {
			return sdf2.parse(in.substring(0, in.length() - 1));
		} catch (Exception e) {
		}
		throw new ParseException("could not parse as date: " + in, 0);
	}

	@Override
	public String formatDate(Date attValue) {
		if (attValue == null)
			return null;
		return sdf2.format(attValue) + "Z";
	}

	public abstract String[] getUserObjectClasses();

	public Map<String, Object> addConnectionProperties() {
		Hashtable<String, Object> env = new Hashtable<>();
		return env;
	}

	public DirContext connect(String username, String password, String otp) {

		this.env = new Hashtable<>();
		try {

			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_PRINCIPAL, username);
			String pw = password + (otp == null ? "" : otp);

			env.put(Context.SECURITY_CREDENTIALS, pw);
			env.put("java.naming.ldap.version", "3");

			if (config.getEncryption() == ENCRYPTION.SSL) {
				env.put(Context.PROVIDER_URL, "ldaps://" + config.getHost() + ":" + config.getPort());
				env.put(Context.SECURITY_PROTOCOL, "ssl");

				env.put("java.naming.ldap.factory.socket", ThreadLocalSocketFactory.class.getName());

				X509Certificate[] certs = new X509Certificate[0];

				if (!StringUtils.isEmpty(config.getCert())) {
					certs = new X509Certificate[] { CertUtil.getCertificate(config.getCert()) };
				}

				TrustManager tm = new CustomTrustManager(certs);
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { tm }, new SecureRandom());
				SocketFactory sf = sslContext.getSocketFactory();

				ThreadLocalSocketFactory.set(sf);

			} else {
				env.put(Context.PROVIDER_URL, "ldap://" + config.getHost() + ":" + config.getPort());
				ThreadLocalSocketFactory.set(SocketFactory.getDefault());
			}

			env.putAll(addConnectionProperties());

			env.put(Context.SECURITY_AUTHENTICATION, config.getAuth().name());

			LdapContext ctx = new InitialLdapContext(env, null);

			if (config.getEncryption() == ENCRYPTION.START_TLS) {
				StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				TrustManager tm = new TrustManagerDelegate();
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { tm }, new SecureRandom());
				tls.negotiate(sslContext.getSocketFactory());
			}

			return ctx;

		} catch (Exception e) {
			log.warn("error connecting to LDAP:", e);
			try {
				new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValue(System.err, env);
			} catch (Exception e2) {
			}

			throw new RuntimeException("unable to connect", e);
		}
	}

	public abstract String[] getUserAttributes();

	public abstract String getUserSearchBase();

	public abstract String getUserByIdFilter(String uid);

	public abstract String getUserByNameFilter(String name);

	public abstract String getUserSearchFilter(String username, String cn, String search, LdapUser user);

	public LdapUser getUser(LdapUser user, SearchResult r) throws NamingException {
		user.setSourceId(config.getId());
		user.setSourceName(config.getName());
		user.setDn(r.getNameInNamespace());
		return user;
	}

	public abstract String[] getGroupAttributes();

	public abstract String getGroupSearchBase();

	public abstract String getGroupGetFilter(String id);

	public abstract String getGroupSearchFilter(String search);

	public abstract String getGroupForUserFilter(LdapObject user) throws NamingException;

	public abstract String getGroupForGroupFilter(LdapObject user) throws NamingException;

	public abstract LdapGroup getGroup(LdapGroup ou, SearchResult r) throws NamingException;

	@Override
	public abstract void setPassword(String id, String otp, String newPassword) throws AuthException;

	@Override
	public abstract void setPasswordExpiryDate(String id, Date date) throws Exception;

	@Override
	public abstract void setUserExpiryDate(String id, Date date) throws Exception;

	public List<SearchResult> getList(String base, String filter, String[] attributes) throws NamingException {
		log.info(base + " / " + filter);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		if (attributes != null && attributes.length > 0) {
			searchControls.setReturningAttributes(attributes);
		}
		String dn = LdapEncoder.escapeDn(base);
		NamingEnumeration<SearchResult> results = this.getContext().search(dn, filter, searchControls);
		List<SearchResult> out = new ArrayList<>();
		try {
			while (results.hasMore()) {
				out.add(results.next());
				log.trace("result: +1 ---> " + out.size());
			}
		} catch (Exception e) {
			log.warn("following referrals might yield more results, filter was: " + filter);
		}
		log.info("result: " + out.size());
		return out;
	}

	public SearchResult get(String base, String filter, String[] attributes) throws NamingException {
		log.info(base + " / " + filter);
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setReturningAttributes(attributes);
		String dn = LdapEncoder.escapeDn(base);
		NamingEnumeration<SearchResult> results = this.getContext().search(dn, filter, searchControls);
		try {
			if (results.hasMore()) {
				return results.next();
			}
		} catch (Exception e) {
			log.warn("following referrals might yield more results, filter was: " + filter);
		}
		return null;
	}

	@Override
	public <T> List<T> getList(String base, String filter, String[] attributes, LdapSearchResultMapper<T> mapper)
			throws NamingException {
		List<T> out = new ArrayList<>();
		for (SearchResult sr : getList(base, filter, attributes)) {
			out.add(mapper.map(sr));
		}
		log.info(base + " / " + filter + " ----> " + out.size());
		return out;
	}

	@Override
	public <T> T get(String base, String filter, String[] attributes, LdapSearchResultMapper<T> mapper)
			throws NamingException {
		SearchResult sr = get(base, filter, attributes);
		if (sr != null) {
			return mapper.map(sr);
		}
		return null;
	}

	public NamingEnumeration<SearchResult> list(String base, String filter, String[] attributes)
			throws NamingException {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setReturningAttributes(attributes);
		String dn = LdapEncoder.escapeDn(base);
		NamingEnumeration<SearchResult> results = this.getContext().search(dn, filter, searchControls);
		log.debug("list: {base=" + base + ", filter=" + filter + " }");

		return results;
	}

	public <T> List<T> list(String base, String filter, LdapSearchResultMapper<T> mapper, String[] attributes)
			throws NamingException {
		List<T> out = new ArrayList<>();
		NamingEnumeration<SearchResult> results = list(base, filter, attributes);
		try {
			while (results.hasMore()) {
				T t = mapper.map(results.next());
				out.add(t);
			}
		} catch (PartialResultException e) {
			log.warn("ignored referral might lead to incomplete search result");
		}
		return out;
	}

	public void modifyEntry(String dn, ModificationItem[] mods) throws NamingException {
		dn = LdapEncoder.escapeDn(dn);

		try {
			Identity i = IdentityHolder.get();
			String user = "[anonymous]";

			Map<String, List<Object>> changes = new HashMap<>();

			for (ModificationItem mi : mods) {
				Attribute a = (Attribute) mi.getAttribute();

				String key;
				switch (mi.getModificationOp()) {
				case DirContext.ADD_ATTRIBUTE:
					key = a.getID() + ":ADD";
					break;
				case DirContext.REMOVE_ATTRIBUTE:
					key = a.getID() + ":DEL";
					break;
				case DirContext.REPLACE_ATTRIBUTE:
					key = a.getID() + ":REPLACE";
					break;
				default:
					key = a.getID() + ":OTHER";
					break;
				}

				List<Object> os = changes.get(key);
				if (os == null) {
					os = new ArrayList<>();
					changes.put(key, os);
				}

				NamingEnumeration<?> nev = a.getAll();
				while (nev.hasMore()) {
					if (a.getID().startsWith("userPassword:")) {
						os.add("XXXXXXXXXXXXXX");
					} else {
						os.add(nev.next() + "");
					}
				}

			}
			if (i != null) {
				user = i.getUser().getSourceId() + ":" + i.getUser().getId() + ":" + i.getUser().getDisplayName();
			}

			AuditLoggerHelper.log("LDAP", "MODIFY", "", user, config.getName() + ":" + dn, "LDAP UPDATE: "
					+ new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(changes));
		} catch (Exception e) {
			log.error("error writing audit log", e);
			throw new NamingException("failed to write audit log");
		}

		this.getContext().modifyAttributes(dn, mods);
	}

	protected ModificationItem createItem(SearchResult r, String attributeName, Object newAttributeValue) {
		Object oldValue = r.getAttributes().get(attributeName);
		int op = DirContext.ADD_ATTRIBUTE;
		if (newAttributeValue instanceof List) {
			op = DirContext.REPLACE_ATTRIBUTE;
			BasicAttribute ba = new BasicAttribute(attributeName);
			for (Object o : (List<?>) newAttributeValue) {
				if (o != null && !StringUtils.isEmpty(newAttributeValue.toString())) {
					ba.add(o);
				}
			}
			return new ModificationItem(op, ba);
		}
		if (oldValue == null) {
			if (newAttributeValue == null || StringUtils.isEmpty(newAttributeValue.toString())) {
				return null;
			} else {
				return new ModificationItem(DirContext.ADD_ATTRIBUTE,
						new BasicAttribute(attributeName, newAttributeValue));
			}
		} else {
			if (newAttributeValue == null || StringUtils.isEmpty(newAttributeValue.toString())) {
				log.debug("remove att: " + attributeName);
				return new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(attributeName, oldValue));
			} else {
				log.debug("replace att: " + attributeName);
				return new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute(attributeName, newAttributeValue));
			}
		}
	}

	protected ModificationItem[] createItems(SearchResult r, Map<String, Object> values) {
		ArrayList<ModificationItem> items = new ArrayList<>();
		for (Map.Entry<String, Object> e : values.entrySet()) {
			ModificationItem mi = createItem(r, e.getKey(), e.getValue());
			if (mi != null) {
				items.add(mi);
			}
		}
		ModificationItem[] x = new ModificationItem[items.size()];
		items.toArray(x);
		return x;
	}

	protected void updateEntry(SearchResult r, Map<String, Object> values) throws NamingException {
		ModificationItem[] items = createItems(r, values);
		log.debug("updating entry: " + r.getNameInNamespace());
		try {
			log.debug(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(values));
		} catch (JsonProcessingException e) {
		}
		modifyEntry(r.getNameInNamespace(), items);
	}

	@Override
	public void createEntry(String dn, String[] classes, Map<String, Object> attributes) throws NamingException {
		Attribute oc = new BasicAttribute("objectClass");
		for (String s : classes) {
			oc.add(s);
		}

		Attributes entry = new BasicAttributes();
		entry.put(oc);
		System.err.println(attributes.size());
		for (Map.Entry<String, Object> e : attributes.entrySet()) {
			String attId = e.getKey();
			Object attValue = e.getValue();
			if (attValue instanceof Date) {
				attValue = formatDate((Date) attValue);
			}
			Attribute a = new BasicAttribute(attId, attValue);

			System.err.println(attId + ": " + attValue);

			entry.put(a);
		}
		dn = LdapEncoder.escapeDn(dn);

		try {
			Identity i = IdentityHolder.get();
			String user = "[anonymous]";

			Map<String, List<Object>> changes = new HashMap<>();

			NamingEnumeration<?> ne = entry.getAll();
			while (ne.hasMore()) {
				Attribute a = (Attribute) ne.next();

				List<Object> os = changes.get(a.getID());
				if (os == null) {
					os = new ArrayList<>();
					changes.put(a.getID(), os);
				}

				NamingEnumeration<?> nev = a.getAll();
				while (nev.hasMore()) {
					if (a.getID().startsWith("userPassword:")) {
						os.add("XXXXXXXXXXXXXX");
					} else {
						os.add(nev.next());
					}
				}

			}

			if (i != null) {
				user = i.getUser().getSourceId() + ":" + i.getUser().getId() + ":" + i.getUser().getDisplayName();
			}
			AuditLoggerHelper.log("LDAP", "CREATE", "", user, config.getName() + ":" + dn, "LDAP UPDATE: "
					+ new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(changes));
		} catch (Exception e) {
			log.error("error writing audit log", e);
			throw new NamingException("failed to write audit log");
		}

		this.getContext().createSubcontext(dn, entry);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(SearchResult r, String attribute, T def) throws NamingException {
		if (r.getAttributes().get(attribute) == null) {
			return def;
		}
		if (r.getAttributes().get(attribute).get() == null) {
			return def;
		}

		if (def instanceof Date) {
			try {
				return (T) parseDate((String) r.getAttributes().get(attribute).get());
			} catch (ParseException e) {
				log.warn("unparseable date: " + r.getAttributes().get(attribute).get(), e);
				return def;
			}
		} else if (def instanceof Integer) {
			try {
				return (T) new Integer(Integer.parseInt((String) r.getAttributes().get(attribute).get()));
			} catch (Exception e) {
				log.warn("unparseable int: " + r.getAttributes().get(attribute).get());
				return def;
			}
		} else if (def instanceof Long) {
			try {
				return (T) new Long(Long.parseLong((String) r.getAttributes().get(attribute).get()));
			} catch (Exception e) {
				log.warn("unparseable long: " + r.getAttributes().get(attribute).get());
				return def;
			}
		}

		return (T) r.getAttributes().get(attribute).get();
	}

	@Override
	public LdapUser authenticate(String username, String password, String otp) throws AuthException {
		LdapUser u = null;
		log.warn("authenticating user using ldap connection ... ");
		try {
			u = getUserByName(username);
		} catch (Exception e) {
			log.warn("error authenticating: error finding user", e);
			throw new AuthenticationFailedException();
		}
		if (u == null) {
			log.warn("error authenticating: user not found");
			throw new NotAuthenticatedException();
		}
		try {
			log.debug("authenticating user: " + u.getDn());
			connect(u.getDn(), password, otp);
			return u;
		} catch (Exception e) {
			log.error(e);
			throw new AuthenticationFailedException("");
		}
	}

	@Override
	public List<LdapUser> listUsers(String username, String cn, String search, LdapUser user, int offset, int max) {
		String filter = getUserSearchFilter(username, cn, search, user);
		List<LdapUser> out = null;
		try {
			LdapSearchResultMapper<LdapUser> m = new LdapSearchResultUserMapper();
			log.info("listing users with filter: " + filter);
			out = list(getUserSearchBase(), filter, m, getUserAttributes());
			log.info("listing users with filter .... sorting ...");
			Collections.sort(out);
			log.info("listing users with filter .... sorting ... DONE!");
			if (offset > -1) {
				out = out.subList(Math.min(out.size(), offset), out.size());
			}
			if (max >= -1 && max < out.size()) {
				out = out.subList(0, max);
			}
		} catch (Exception e) {
			log.error("unable to search for users, filter: " + filter, e);
		}
		return out;
	}

	
	 
	
	@Override
	public LdapUser getUserById(String id) throws Exception {
		LdapSearchResultUserMapper m = new LdapSearchResultUserMapper();
		String filter = getUserByIdFilter(id);
		log.info("filter: " + filter);
		return get(getBaseDn(), filter, getUserAttributes(), m);
		  
	}

	@Override
	public LdapUser getUserByName(String username) throws Exception {
		LdapSearchResultUserMapper m = new LdapSearchResultUserMapper();
		String filter = getUserByNameFilter(username);
		return get(getBaseDn(), filter, getUserAttributes(), m);
	}

	public void updateUserAttribute(String dn, String attribute, Object attributeValue, boolean replace)
			throws NamingException {
		if (attributeValue instanceof Date) {
			attributeValue = formatDate((Date) attributeValue);
		}
		ModificationItem[] mi = new ModificationItem[] {
				new ModificationItem(replace ? DirContext.REPLACE_ATTRIBUTE : DirContext.ADD_ATTRIBUTE,
						new BasicAttribute(attribute, attributeValue)) };
		modifyEntry(dn, mi);
	}

	@Override
	public List<LdapGroup> listGroups(String search, int offset, int max) {

		String filter = getGroupSearchFilter(search);
		List<LdapGroup> out = null;
		try {
			LdapSearchResultGroupMapper m = new LdapSearchResultGroupMapper();
			out = list(getGroupSearchBase(), filter, m, getGroupAttributes());
			Collections.sort(out);
			if (offset > -1) {
				out = out.subList(Math.min(out.size(), offset), out.size());
			}
			if (max >= -1 && max < out.size()) {
				out = out.subList(0, max);
			}

		} catch (Exception e) {
			log.error("unable to search for groups, filter: " + filter, e);
		}
		return out;
	}

	@Override
	public LdapGroup getGroup(String id) throws Exception {
		String filter = getGroupGetFilter(id);
		LdapSearchResultGroupMapper m = new LdapSearchResultGroupMapper();
		return get(getGroupSearchBase(), filter, getGroupAttributes(), m);
	}

	protected void getGroupsForGroup(List<LdapGroup> found, LdapGroup group, boolean recursive) throws Exception {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setReturningAttributes(getGroupAttributes());
		String filter = getGroupForGroupFilter(group);
		try {
			LdapSearchResultGroupMapper m = new LdapSearchResultGroupMapper();
			for (LdapGroup g : list(getGroupSearchBase(), filter, m, getGroupAttributes())) {
				boolean added = false;
				// may be threaded
				synchronized (found) {
					if (!found.contains(g)) {
						found.add(g);
						added = true;
					}
				}
				if (recursive && added) {
					getGroupsForGroup(found, g, recursive);
				}

			}
			// may be threaded
			synchronized (found) {
				Collections.sort(found);
			}
			if (log.isDebugEnabled())
				log.debug("looking for user groups. filter: " + filter + " / " + found.size() + " results");
		} catch (Exception e) {
			log.error("error looking for user groups filter: " + filter, e);
		}
	}

	public List<LdapGroup> getGroupsForGroup(String groupId, boolean recursive) throws Exception {
		List<LdapGroup> groups = new ArrayList<>();
		LdapGroup lg = getGroup(groupId);
		if (lg != null) {
			getGroupsForGroup(groups, lg, recursive);
		}
		return groups;
	}

	@Override
	public List<LdapGroup> getGroupsForUser(String userId, boolean recursive) throws Exception {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setReturningAttributes(getGroupAttributes());
		List<LdapGroup> groups = new ArrayList<>();
		LdapUser lu = getUserById(userId);
		if (lu == null) {
			return groups;
		}
		String filter = getGroupForUserFilter(lu);
		try {
			LdapSearchResultGroupMapper m = new LdapSearchResultGroupMapper();
			groups = list(getGroupSearchBase(), filter, m, getGroupAttributes());
			if (log.isDebugEnabled())
				log.debug("looking for user groups. filter: " + filter + " / " + groups.size() + " results");
		} catch (Exception e) {
			log.error("error looking for user groups filter: " + filter, e);
		}
		if (recursive) {
			ExecutorService taskExecutor = Executors.newFixedThreadPool(8);

			for (LdapGroup g : new ArrayList<>(groups)) {
				taskExecutor.execute(new SearchGroupsRunner(this, groups, g));
			}

			taskExecutor.shutdown();
			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				throw new RuntimeException("Exception caught in wait", e);
			}
		}
		Collections.sort(groups);
		return groups;
	}

	class SearchGroupsRunner implements Runnable {
		LdapConnection conn;
		List<LdapGroup> groups;
		LdapGroup group;

		public SearchGroupsRunner(LdapConnection conn, List<LdapGroup> groups, LdapGroup g) {
			this.conn = conn;
			this.groups = groups;
			this.group = g;
		}

		@Override
		public void run() {
			try {
				this.conn.initContextForSubThread();
				getGroupsForGroup(this.groups, this.group, true);
			} catch (Exception e) {
				throw new RuntimeException("Exception caught in groups search", e);
			}
		}

	}

	@Override
	public List<LdapUser> getUsersForGroup(String groupId) throws Exception {

		SearchResult sr = get(getGroupSearchBase(), getGroupGetFilter(groupId), new String[] { "member" });

		List<LdapUser> users = new ArrayList<>();

		if (sr.getAttributes() == null) {
			return users;
		}

		if (sr.getAttributes().get("member") == null) {
			return users;
		}

		LdapSearchResultMapper<LdapUser> m = new LdapSearchResultUserMapper();

		NamingEnumeration<?> values = sr.getAttributes().get("member").getAll();
		while (values.hasMore()) {
			String udn = values.next().toString();
			LdapUser u = get(udn, "(objectClass=person)", getUserAttributes(), m);
			if (u != null) {
				users.add(u);
			}
		}
		return users;
	}

	public abstract LdapUser createUserInternal(LdapUser user) throws Exception;

	public LdapObject createUser(LdapUser user) throws Exception {
		user = createUserInternal(user);
		if (!StringUtils.isEmpty(user.getMail())) {
			Map<String, Object> params = new HashMap<>();
			params.put("user", user);
			mailService.sendMail(user.getMail(), "ldapconfig", "userCreated", null, params);
		}
		return user;
	}

	@Override
	public void addUserToGroup(String userId, String groupId) throws Exception {

		LdapObject u = getUserById(userId);
		LdapGroup g = getGroup(groupId);
		if (u == null || g == null)
			return;

		Attribute a = new BasicAttribute("member", u.getDn());
		ModificationItem[] items = new ModificationItem[] { new ModificationItem(DirContext.ADD_ATTRIBUTE, a) };
		modifyEntry(g.getDn(), items);
	}

	@Override
	public void removeUserFromGroup(String userId, String groupId) throws Exception {

		LdapObject u = getUserById(userId);
		LdapGroup g = getGroup(groupId);
		if (u == null || g == null)
			return;

		SearchResult sr = get(getGroupSearchBase(), getGroupGetFilter(groupId), new String[] { "member" });
		if (sr == null)
			return;
		if (sr.getAttributes() == null)
			return;
		if (sr.getAttributes().get("member") == null)
			return;
		NamingEnumeration<?> values = sr.getAttributes().get("member").getAll();

		Attribute a = new BasicAttribute("member");
		while (values.hasMore()) {
			String udn = values.next().toString();
			if (udn.compareTo(u.getDn()) != 0) {
				a.add(udn);
			}
		}
		ModificationItem[] items = new ModificationItem[] { new ModificationItem(DirContext.REPLACE_ATTRIBUTE, a) };
		modifyEntry(g.getDn(), items);

	}

	@Override
	public LdapConfig getConfig() {
		return config;
	}

	public void setConfig(LdapConfig config) {
		this.config = config;
	}

	public List<LdapCustomsFieldConfig> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<LdapCustomsFieldConfig> customFields) {
		this.customFields = customFields;
	}

	protected class LdapSearchResultUserMapper implements LdapSearchResultMapper<LdapUser> {

		public LdapSearchResultUserMapper() {
		}

		@Override
		public LdapUser map(SearchResult r) {
			LdapUser ou = new LdapUser();

			List<LdapCustomsFieldConfig> customFieldConfigs = new ArrayList<>();

			try {
				Attribute a = r.getAttributes().get("objectClass");
				NamingEnumeration<?> oca = a.getAll();
				while (oca.hasMore()) {
					String oc = (String) oca.next();
					ou.addObjectClass(oc);
					for (LdapCustomsFieldConfig lcf : getCustomFields()) {
						if (lcf.getObjectClass().equalsIgnoreCase(oc)) {
							if (!customFieldConfigs.contains(lcf)) {
								customFieldConfigs.add(lcf);
							}
						}
					}
				}
			} catch (Exception e) {
			}

			List<LdapCustomField> customFields = new ArrayList<LdapCustomField>();

			for (LdapCustomsFieldConfig lcf : customFieldConfigs) {
				LdapCustomField customField = new LdapCustomField(lcf);
				try {
					Attribute a = r.getAttributes().get(lcf.getAttributeName());
					NamingEnumeration<?> oca = a.getAll();
					while (oca.hasMore()) {
						customField.addValue(oca.next());
					}
				} catch (Exception e) {
				}
				customFields.add(customField);
			}

			ou.setCustomFields(customFields);
			try {
				return getUser(ou, r);
			} catch (Exception e) {
			}
			return null;
		}
	}

	protected class LdapSearchResultGroupMapper implements LdapSearchResultMapper<LdapGroup> {

		public LdapSearchResultGroupMapper() {
		}

		@Override
		public LdapGroup map(SearchResult r) {
			LdapGroup ou = new LdapGroup();

			List<LdapCustomsFieldConfig> customFieldConfigs = new ArrayList<>();

			try {
				Attribute a = r.getAttributes().get("objectClass");
				NamingEnumeration<?> oca = a.getAll();
				while (oca.hasMore()) {
					String oc = (String) oca.next();
					log.info("objectClass: " + oc);
					ou.addObjectClass(oc);
					for (LdapCustomsFieldConfig lcf : getCustomFields()) {
						if (lcf.getObjectClass().equalsIgnoreCase(oc)) {
							if (!customFieldConfigs.contains(lcf)) {
								customFieldConfigs.add(lcf);
							}
						}
					}
				}
			} catch (Exception e) {
			}

			List<LdapCustomField> customFields = new ArrayList<LdapCustomField>();

			for (LdapCustomsFieldConfig lcf : customFieldConfigs) {
				LdapCustomField customField = new LdapCustomField(lcf);
				try {
					Attribute a = r.getAttributes().get(lcf.getAttributeName());
					NamingEnumeration<?> oca = a.getAll();
					while (oca.hasMore()) {
						customField.addValue(oca.next());
					}
				} catch (Exception e) {
				}
				customFields.add(customField);
			}

			ou.setCustomFields(customFields);
			try {
				return getGroup(ou, r);
			} catch (Exception e) {
			}
			return null;
		}
	};

	@Override
	public String getType() {
		return config.getServerType();
	}

	@Override
	public DirContext getContext() {
		synchronized (this.ctx) {
			if (localCtx.get() != null) {
				return localCtx.get();
			} else {
				return this.ctx;
			}
		}
	}
	
	protected void setContext(DirContext context) {
		this.ctx = context;
	}

	@Override
	public boolean supportsOtp() {
		return config.isUseOtp();
	}

	@Override
	public void initContextForSubThread() {
		if (this.ctx == null)  {
			log.warn("Cannot dereive context for thread since context ist null");
			return;
		}
		synchronized (this.ctx) {
			try {
				if (localCtx.get() == null) {
					DirContext subContext = (DirContext) this.ctx.lookup(""); 
					this.localCtx.set(subContext);
				}
			} catch (NamingException e) {
				localCtx.set(null);
				log.warn("Feailed to dereive context for thread" ,e );
				throw new RuntimeException("Lookup of sub context failed", e);
			}
		}
	}

}
