package com.autonubil.identity.openid.impl.services;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.openid.impl.entities.OAuthApp;
import com.autonubil.identity.openid.impl.entities.OAuthPermission;
import com.autonubil.identity.openid.impl.entities.OAuthSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Aggregation;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.enums.JoinType;
import de.disk0.db.sqlbuilder.enums.Operator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.JoinableTable;
import de.disk0.db.sqlbuilder.interfaces.JoinedTable;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;
import de.disk0.db.sqlbuilder.interfaces.Update;

@Service
@PropertySource(value="openid.properties")
public class OAuth2ServiceImpl {
	private static Log log = LogFactory.getLog(OAuth2ServiceImpl.class);

	@Value("${openid.master_secret}")
	private String masterSecret;
	
	@Autowired
	@Qualifier("openidDb")
	private DataSource dataSource;

	
	private long PURGE_INTERVALL = 60 * 10 * 1000;
	private long lastPurge = 0;

	private static byte[] iv = {0xc, 0xa, 0xd, 0x1, 0x4, 0x2, 0x2, 0x3, 0xc, 0xa, 0xd, 0x1, 0x4, 0x2, 0x2, 0x3};
	private IvParameterSpec ivspec = new IvParameterSpec( iv);
	/*
	 * @Autowired
	 * 
	 * @Qualifier("oauthDb") private DataSource dataSource;
	 */

	public OAuth2ServiceImpl() {
	}

	public OAuthSession addApproval(String clientId, String code, String state, String nonce, OAuthApp app, List<String> scopes) {
		this.purge();
		OAuthSession approval; 
		if (code != null) {
			approval = this.getApproval(code);
		} else { 
			 approval = new OAuthSession(clientId, code, state, nonce, app, scopes);
			 this.saveSession(approval );
			 
		 }
		 return approval;
	}
	
	public OAuthSession getApproval(String code) {
		return  this.getSession(code);
	}
	

	public String upgradeSession(OAuthSession session) {
		this.purge();
		this.deleteSession(session);
		String token = session.upgrade();
		this.saveSession(session);
		return token;
	}
	
	protected void purge() {

		long now = new Date().getTime();
		if (now > lastPurge + PURGE_INTERVALL) {
			log.debug("Purging old sessions");
			purgeSessions();
			this.lastPurge = now;
		}
		
	}
 
	

	public String autorize() {
		return "";
	}

	
	// Database Access
	public OAuthApp getApplication(String id) {
		 
		if (id == null) {
			throw new NullPointerException("id must not be null");
		}
		
		Select s = SqlBuilderFactory.select();
		Table source = s.fromTable("application");

		s.where(Operator.AND, s.condition(source, "client_id", Comparator.EQ, id));

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		List<OAuthApp> out = new ArrayList<>();
		out = templ.query(s.toSQL(), s.getParams(), new ApplicationSourceRowMapper());
		
		if (out.size() == 1) {
			return out.get(0);
		} else {
			return null;
		}
		
	}
	
	// Database Access
	
	public List<OAuthApp> listApplications(String search) {
		List<OAuthApp> result = new ArrayList<>();
		
		for(OAuthApp app : this.listApplications()) {
			if ((search == null || search.length() == 0) || (app.getName().contains(search)
					|| app.getId().contains(search)  )) {
				result.add(app);
			}
		}
		
		return result;
	}
	
	public List<OAuthApp> listApplications() {
		 
		Select s = SqlBuilderFactory.select();
		Table source = s.fromTable("application");
		s.order(source, "name", true);

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		List<OAuthApp> out = new ArrayList<>();
		out = templ.query(s.toSQL(), s.getParams(), new ApplicationSourceRowMapper());
		return out;
	}
	
 	

	public OAuthApp saveApplication(OAuthApp application) {
		if (application == null) {
			throw new NullPointerException("application must not be null");
		}
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		Insert i = SqlBuilderFactory.insert("application");
		SecureRandom random = new SecureRandom();
		
		// set applicatio ID and secret 
		application.setId( new BigInteger(130, random).toString(32));
		String rawSecret = new BigInteger(130, random).toString(32);
		application.setSecret(rawSecret);
		
		i.addField("name", application.getName());
		i.addField("client_id",application.getId());
		i.addField("scopes", String.join(",", application.getScopes()));
		if ( (application.getLinkedAppId() != null) && (application.getLinkedAppId().length() > 0) ) {
			i.addField("linked_app_id", application.getLinkedAppId());
		}
		i.addField("trusted_app", !application.isUserApprovalRequired());
		
		byte[] encryptedSecret =encrypt(application.getSecret()) ; 
		
		i.addField("secret",encryptedSecret);
		
		templ.update(i.toSQL(), i.getParams());
		return getApplication(application.getId());
	}
	
	
	public OAuthApp updateApplication(OAuthApp application) {
		if (application == null) {
			throw new NullPointerException("application must not be null");
		}
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		Update u = SqlBuilderFactory.update("application");
		
		// u.set("client_id", application.getId());
		u.set("scopes", String.join(",", application.getScopes()));
		if (application.getLinkedAppId() != null) {
			u.set("linked_app_id", application.getLinkedAppId());
		}
		u.set("trusted_app", !application.isUserApprovalRequired());
		u.set("secret", encrypt(application.getSecret()) );
		
		u.where(Operator.AND, u.condition(u.getTable(), "client_id", Comparator.EQ, application.getId()));
		
		templ.update(u.toSQL(), u.getParams());
		
		return getApplication(application.getId());
	}
	
	
	
	public void deleteApplication(OAuthApp application) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		{
			Delete d = SqlBuilderFactory.delete("application_permission");
			d.where(Operator.AND, d.condition(d.getTable(), "client_id", Comparator.EQ, application.getId()));
			templ.update(d.toSQL(), d.getParams());
		}

		{		
			Delete d = SqlBuilderFactory.delete("application");
			d.where(Operator.AND, d.condition(d.getTable(), "client_id", Comparator.EQ, application.getId()));
			templ.update(d.toSQL(), d.getParams());
		}
	}
	
	
	
	public OAuthSession getSession(String id) {
 
		if (id == null) {
			throw new NullPointerException("id must not be null");
		}
		
		Select s = SqlBuilderFactory.select();
		Table source = s.fromTable("session");

		s.where(Operator.AND, s.condition(source, "id", Comparator.EQ, id));
		s.where(Operator.AND, s.condition(source, "expires", Comparator.LT, new Date().getTime()));

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		List<OAuthSession> out = new ArrayList<>();
		out = templ.query(s.toSQL(), s.getParams(), new SessionSourceRowMapper());
		
		if (out.size() == 1) {
			return out.get(0);
		} else {
			return null;
		}
		
	}
	

	public void saveSession(OAuthSession session) {
		if (session == null) {
			throw new NullPointerException("session must not be null");
		}
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		Insert i = SqlBuilderFactory.insert("session");
		ObjectMapper mapper = new ObjectMapper(); 
		
		i.addField("id", session.getCode());
		i.addField("expires", session.getExpires().getTime());
		try {
			i.addField("definition", mapper.writeValueAsString(session));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Failed to serialize session", e);
		}
		templ.update(i.toSQL(), i.getParams());
	}
	
	
	public void deleteSession(OAuthSession session) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Delete d = SqlBuilderFactory.delete("session");
		d.where(Operator.AND, d.condition(d.getTable(), "id", Comparator.EQ, session.getCode()));
		templ.update(d.toSQL(), d.getParams());
	}
	
	public void purgeSessions() {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Delete d = SqlBuilderFactory.delete("session");
		d.where(Operator.AND, d.condition(d.getTable(), "expires", Comparator.LT, new Date().getTime()));
		templ.update(d.toSQL(), d.getParams());
	}
	
	// encryyption
	public byte[] encrypt(String input){

	    try {

	        MessageDigest sha = MessageDigest.getInstance("SHA-1");
	        
	        if (input.length() % 8 != 0) {
	        	input = input + "        ".substring(0,input.length() % 8);
	        }
	        
	        byte key[] = sha.digest(this.masterSecret.getBytes());
	        key = Arrays.copyOf(key, 16); // use only first 128 bit
	        SecretKeySpec secret = new SecretKeySpec(key, "AES");
	        
	        byte[] crypted = null;
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secret,ivspec);
	        byte[] ptext = input.getBytes("UTF-8");
	        crypted = cipher.doFinal(ptext);

	        return crypted;
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return null;
	}
	public String decrypt(byte[] input) throws Exception {

        byte[] output = null;
        
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte key[] = sha.digest(this.masterSecret.getBytes());
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        SecretKeySpec secret = new SecretKeySpec(key, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); //Change here
        cipher.init(Cipher.DECRYPT_MODE, secret,ivspec);
        output = cipher.doFinal(input);
        if(output==null){
            throw new Exception();
        }

        // return new String[]{new String(output),iv};
        return new String(output,"UTF-8").trim();
	}
	
	// Row Mappers
	public class SessionSourceRowMapper  implements RowMapper<OAuthSession> {

		
		@Override
		public OAuthSession mapRow(ResultSet rs, int rowNum) throws SQLException {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return  mapper.readValue(rs.getString("definition"), OAuthSession.class);
			} catch (IOException e) {
				throw new SQLException("Failed to read definition");
			}
		}

	}


	private OAuthApp appFromResultSet(ResultSet rs) throws SQLException {
		OAuthApp out= new OAuthApp();
		out.setName(rs.getString("name"));
		out.setId(rs.getString("client_id"));
		try {
			out.setSecret(decrypt(rs.getBytes("secret")));
		} catch (Exception e) {
			throw new SQLException("Failed to decrypt secret", e);
		}
		out.setScopes(Arrays.asList(rs.getString("scopes").split(",")));
		out.setUserApprovalRequired(!rs.getBoolean("trusted_app"));
		String linkedAppId = rs.getString("linked_app_id");
		out.setLinkedAppId(linkedAppId);
		return out;
	}


	public class ApplicationSourceRowMapper  implements RowMapper<OAuthApp> {

		
		@Override
		public OAuthApp mapRow(ResultSet rs, int rowNum) throws SQLException {
			OAuthApp out  = appFromResultSet(rs);
			return out;
		}
	}
	

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#listPermissions(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public List<OAuthPermission> listPermissions(String clientId, String source, String groupId) {
		List<OAuthPermission> out = new ArrayList<>();
		Select s = SqlBuilderFactory.select();
		Table app = s.fromTable("application_permission");

		if (clientId != null) {
			s.where(Operator.AND, s.condition(app, "client_id", Comparator.EQ, clientId));
		}
		if (source != null) {
			s.where(Operator.AND, s.condition(app, "source", Comparator.EQ, source));
		}
		if (groupId != null) {
			s.where(Operator.AND, s.condition(app, "group_id", Comparator.EQ, groupId));
		}

		s.order(app, "name", true);

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		out = templ.query(s.toSQL(), s.getParams(), new OAuthPermissionRowMapper());
		return out;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.autonubil.identity.ovpn.common.service.test#addPermission(com.
	 * autonubil.identity.ovpn.api.entities.OAuthPermission)
	 */
	public void addPermission(OAuthPermission permission) {
		List<OAuthPermission> ap = listPermissions(permission.getClientId(), permission.getSourceId(),
				permission.getGroupId());
		if (permission.getClientId() == null || permission.getSourceId() == null || permission.getGroupId() == null) {
			throw new InvalidParameterException("ovpn, source and group must not be null");
		}
		if (ap.size() > 0) {
			removePermission(permission.getClientId(), permission.getSourceId(), permission.getGroupId());
		}
		Insert i = SqlBuilderFactory.insert("application_permission");
		i.addField("client_id", permission.getClientId());
		i.addField("source", permission.getSourceId());
		i.addField("group_id", permission.getGroupId());
		i.addField("name", permission.getName());
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		templ.update(i.toSQL(), i.getParams());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#removePermission(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	public void removePermission(String appId, String source, String groupId) {
		List<OAuthPermission> ap = listPermissions(appId, source, groupId);

		if (ap.size() > 0) {
			Delete d = SqlBuilderFactory.delete("application_permission");
			d.where(Operator.AND, d.condition(d.getTable(), "client_id", Comparator.EQ, appId));
			d.where(Operator.AND, d.condition(d.getTable(), "source", Comparator.EQ, source));
			d.where(Operator.AND, d.condition(d.getTable(), "group_id", Comparator.EQ, groupId));
			NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
			templ.update(d.toSQL(), d.getParams());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.autonubil.identity.ovpn.common.service.test#listOAuthAppsForGroups(java.
	 * util.List, java.lang.String)
	 */
	public List<OAuthApp> listOAuthAppsForGroups(List<Group> groups, String search) {

		if (groups == null || groups.size() == 0) {
			return new ArrayList<>();
		}

		List<String> groupIds = new ArrayList<>();
		for (Group g : groups) {
			groupIds.add(g.getId());
		}

		Select s = SqlBuilderFactory.select();
		JoinableTable application = s.fromTable("application");
		s.select(Aggregation.NONE, application, "client_id", "client_id");
		s.select(Aggregation.NONE, application, "name", "name");
		s.select(Aggregation.NONE, application, "secret", "secret");
		s.select(Aggregation.NONE, application, "scopes", "scopes");
		s.select(Aggregation.NONE, application, "linked_app_id", "linked_app_id");
		s.select(Aggregation.NONE, application, "trusted_app", "trusted_app");
		

		if ( (search != null) && (search.length() > 0)) {
			s.where(Operator.AND, s.condition(application, "name", Comparator.LIKE, "%" + search.toLowerCase() + "%"));
			s.where(Operator.OR, s.condition(application, "client_id", Comparator.EQ, search));
		}

		JoinedTable vpnPerm = application.joinTable(JoinType.LEFT, "application_permission");
		s.select(Aggregation.NONE, vpnPerm, "source", "source");
		s.select(Aggregation.NONE, vpnPerm, "group_id", "group_id");

		vpnPerm.on(s.condition(application, "client_id", Comparator.EQ, vpnPerm, "client_id")
				.and(s.condition(vpnPerm, "group_id", Comparator.IN, groupIds)));

		s.order(application, "name", true);

		log.warn("listing groups: (" + s.toSQL() + " / " + s.getParams() + ")");

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);

		OAuthPermissionCallbackHandler ch = new OAuthPermissionCallbackHandler(groups);
		try {
			templ.query(s.toSQL(), s.getParams(), ch);
		} catch (Exception e) {
			log.warn("error in statement (" + s.toSQL() + " / " + s.getParams() + ") --- ", e);
			throw new RuntimeException("error in statement (" + s.toSQL() + " / " + s.getParams() + ") --- ", e);
		}
		return ch.getOAuthApps();
	}

 
	private class OAuthPermissionRowMapper implements RowMapper<OAuthPermission> {

		@Override
		public OAuthPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
			OAuthPermission out = new OAuthPermission();
			out.setClientId(rs.getString("client_id"));
			out.setSourceId(rs.getString("source"));
			out.setGroupId(rs.getString("group_id"));
			out.setName(rs.getString("name"));
			return out;
		}

	}

	private class OAuthPermissionCallbackHandler implements RowCallbackHandler {

		private List<OAuthApp> ovpns = new ArrayList<>();
		private Map<String, Group> groups;
		private List<String> found = new ArrayList<>();

		public OAuthPermissionCallbackHandler(List<Group> groups) {
			this.groups = new HashMap<>();
			for (Group g : groups) {
				this.groups.put(g.getSourceId() + ":" + g.getId(), g);
			}
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException {

			String id = rs.getString("client_id");
			if (found.contains(id)) {
				return;
			}

			String group = rs.getString("source") + ":" + rs.getString("group_id");


			if ("null:null". equals(group)) {
				// the "any" group
				group="any";
			} else {
				if(groups!=null && groups.get(group)==null) {
					return;
				}
			}
			found.add(id);
			
			
			found.add(id);
			OAuthApp out  = appFromResultSet(rs);
			getOAuthApps().add(out);
		}

		public List<OAuthApp> getOAuthApps() {
			return ovpns;
		}

	}

}
