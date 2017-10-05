package com.autonubil.identity.localauth.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.auth.api.Credentials;
import com.autonubil.identity.auth.api.entities.BasicGroup;
import com.autonubil.identity.auth.api.entities.BasicUser;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.AuthenticationFailedException;
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPReset;
import com.autonubil.identity.localauth.LocalAuthProvider;
import com.autonubil.identity.localauth.controllers.LocalUserAuthInfo;
import com.autonubil.identity.localauth.entities.LocalAuthUser;
import com.autonubil.identity.localauth.entities.ResetToken;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.mail.api.MailServiceFactory;
import com.autonubil.identity.util.TokenGenerator;
import com.autonubil.identity.util.totp.TotpUtil;

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
public class LocalAuthUserService {

	private static Log log = LogFactory.getLog(LocalAuthUserService.class);
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Qualifier("localauth")
	@Autowired
	private DataSource dataSource;

	@Autowired
	private MailServiceFactory mailServiceFactory;
	
	private MailService mailService;
	
	private String hostname;
	
	public List<LocalAuthUser> list(String id, String search, String username, String order, int offset, int limit) {
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("admin_user");
		s.order(t, "username", true);
		if(id!=null) {
			s.where(Operator.AND, s.condition(t,"id",Comparator.EQ,id));
		}
		if(search!=null && search.length()>0) {
			s.where(Operator.AND, s.condition(t,"username",Comparator.LIKE,search+"%"));
		}
		if(username!=null && username.length()>0) {
			s.where(Operator.AND, s.condition(t,"username",Comparator.EQ,username));
		}
		if(order!=null && order.length()>0) {
			s.order(t, order, true);
		}
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		return templ.query(s.toSQL(), s.getParams(),new LocalAuthUserResultSetExtractor(offset, limit));
	}

	public LocalAuthUser save(LocalAuthUser lau) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if(lau.getId()==null) {
			auditLogger.log("LOCAL_AUTH", "ADD_USER", "", "", IdentityHolder.get().getUser().getSourceId()+":"+IdentityHolder.get().getUser().getDisplayName(), "User added: "+lau.getUsername());
			Insert i = SqlBuilderFactory.insert("admin_user");
			lau.setId(UUID.randomUUID().toString());
			i.addField("id",lau.getId());
			i.addField("username",lau.getUsername());
			i.addField("use_otp",lau.isUseOtp());
			i.addField("email",lau.getEmail());
			templ.update(i.toSQL(), i.getParams());
		} else {
			auditLogger.log("LOCAL_AUTH", "UPDATE_USER", "", "", IdentityHolder.get().getUser().getSourceId()+":"+IdentityHolder.get().getUser().getDisplayName(), "User updated: "+lau.getUsername());
			Update u = SqlBuilderFactory.update("admin_user");
			u.set("username", lau.getUsername());
			u.set("use_otp",lau.isUseOtp());
			u.set("email",lau.getEmail());
			u.where(Operator.AND,u.condition(u.getTable(), "id", Comparator.EQ, lau.getId()));
			templ.update(u.toSQL(), u.getParams());
		}
		return get(lau.getId());
	}

	
	public String generateSecret(String id) throws UnknownHostException {
		LocalAuthUser u = get(id);
		if(u==null) return null;
		auditLogger.log("LOCAL_AUTH", "UPDATE_OTP", "", "", IdentityHolder.get().getUser().getSourceId()+":"+IdentityHolder.get().getUser().getDisplayName(), "OTP secret reset: "+u.getUsername());
		log.info("generate secret ... ");
		String secret = TotpUtil.generateSecret();

		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		log.info("delete old secret ... ");
		Delete d = SqlBuilderFactory.delete("admin_user_otp");
		d.where(Operator.AND,d.condition(d.getTable(), "admin_user_id", Comparator.EQ, id));
		templ.update(d.toSQL(), d.getParams());

		log.info("insert new secret ... ");
		Insert i = SqlBuilderFactory.insert("admin_user_otp");
		i.addField("admin_user_id",u.getId());
		i.addField("secret",secret);
		templ.update(i.toSQL(), i.getParams());
		
		log.info("create url ... ");
		return TotpUtil.generateSecretUrl(secret, u.getUsername()+"@"+getHostname(), "intranet@"+getHostname());
	}


	public LocalAuthUser get(String id) {
		List<LocalAuthUser> l = list(id,null,null, null, 0, 1);
		if(l.size()==1) {
			return l.get(0);
		}
		return null;
	}
	
	public String getHostname() throws UnknownHostException {
		if(hostname == null) {
			hostname = InetAddress.getLocalHost().getHostName();
		}
		return hostname;
	}
	
	
	public void delete(String id) {
		LocalAuthUser u = get(id);
		if(u==null) return;

		auditLogger.log("LOCAL_AUTH", "DELETE_USER", "", "", IdentityHolder.get().getUser().getSourceId()+":"+IdentityHolder.get().getUser().getDisplayName(), "User deleted: "+u.getUsername());
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		for(String dep : new String[] { "admin_user_password", "admin_user_token", "admin_user_otp"})
		{
			Delete d = SqlBuilderFactory.delete(dep);
			d.where(Operator.AND,d.condition(d.getTable(), "admin_user_id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
		{
			Delete d = SqlBuilderFactory.delete("admin_user");
			d.where(Operator.AND,d.condition(d.getTable(), "id", Comparator.EQ, id));
			templ.update(d.toSQL(), d.getParams());
		}
	}

	private ResetToken getToken(String userId) {
		
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		// delete all expired tokens
		Delete d = SqlBuilderFactory.delete("admin_user_token");
		d.where(Operator.AND,d.condition(d.getTable(), "token_expires", Comparator.LT, System.currentTimeMillis()));
		templ.update(d.toSQL(), d.getParams());
		
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("admin_user_token");
		s.where(Operator.AND, s.condition(t,"admin_user_id",Comparator.EQ,userId));

		List<ResetToken> tokens = templ.query (s.toSQL(), s.getParams(), new RowMapper<ResetToken>() {
			@Override
			public ResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResetToken rt = new ResetToken();
				rt.setUserId(rs.getString("admin_user_id"));
				rt.setToken(rs.getString("token"));
				rt.setTokenExpires(new Date(rs.getLong("token_expires")));
				return rt;
			}
		});
		
		if(tokens.size() > 0) {
			return tokens.get(0);
		}
		return null;
	}
	
	public void setPassword(String userId, String password) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		// delete existing password
		Delete d = SqlBuilderFactory.delete("admin_user_password");
		d.where(Operator.AND,d.condition(d.getTable(), "admin_user_id", Comparator.EQ, userId));
		templ.update(d.toSQL(), d.getParams());
		
		Insert i = SqlBuilderFactory.insert("admin_user_password");
		i.addField("admin_user_id", userId);
		i.addField("password",BCrypt.hashpw(password, BCrypt.gensalt()));
		templ.update(i.toSQL(), i.getParams());

	}

	private void setToken(String userId, String token, long expires) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		
		// delete existing password
		Delete d = SqlBuilderFactory.delete("admin_user_token");
		d.where(Operator.AND,d.condition(d.getTable(), "admin_user_id", Comparator.EQ, userId));
		templ.update(d.toSQL(), d.getParams());
		
		Insert i = SqlBuilderFactory.insert("admin_user_token");
		i.addField("admin_user_id", userId);
		i.addField("token",token);
		i.addField("token_expires",expires);
		templ.update(i.toSQL(), i.getParams());
		
	}
	
	public LocalUserAuthInfo getUserAuthInfo(String username) {

		List<LocalUserAuthInfo> users = new ArrayList<>();

		try {
			
			Select s = SqlBuilderFactory.select();
			JoinableTable user = s.fromTable("admin_user");
			
			JoinedTable password = user.joinTable(JoinType.LEFT, "admin_user_password");
			password.on(s.condition(user,"id",Comparator.EQ,password,"admin_user_id"));

			JoinedTable otp = user.joinTable(JoinType.LEFT_OUTER, "admin_user_otp");
			otp.on(s.condition(user,"id",Comparator.EQ,otp,"admin_user_id"));
			
			s.where(Operator.AND,s.condition(user,"username",Comparator.EQ,username));
			s.select(Aggregation.NONE, user, "id", "id");
			s.select(Aggregation.NONE, user, "username", "username");
			s.select(Aggregation.NONE, user, "email", "email");
			s.select(Aggregation.NONE, user, "use_otp", "use_otp");
			s.select(Aggregation.NONE, password, "password", "password");
			s.select(Aggregation.NONE, otp, "secret", "secret");
			
			NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(dataSource);
			users.addAll(t.query(s.toSQL(),s.getParams(),new RowMapper<LocalUserAuthInfo>() {
				@Override
				public LocalUserAuthInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
					LocalUserAuthInfo out = new LocalUserAuthInfo();
					out.setId(rs.getString("id"));
					out.setUsername(rs.getString("username"));
					out.setEmail(rs.getString("email"));
					out.setUseOtp(rs.getBoolean("use_otp"));
					out.setCryptedPassword(rs.getString("password"));
					out.setOtpSecret(rs.getString("secret"));
					return out;
				}
			}));

			if(users.size() > 0) {
				return users.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	public User authenticate(Credentials c) throws AuthException {
		
		UsernamePasswordOTPCredentials upc = (UsernamePasswordOTPCredentials)c;
		
		LocalUserAuthInfo i = getUserAuthInfo(upc.getUsername());
		if(i==null) {
			throw new NotAuthenticatedException(); 
		}
		
		if(!i.checkPassword(upc.getPassword())) {
			throw new NotAuthenticatedException(); 
		}

		if(!i.checkOtp(upc.getSecondFactor())) {
			throw new NotAuthenticatedException(); 
		}
		
		BasicUser out = new BasicUser();
		out.setSourceId(LocalAuthProvider.SOURCE_NAME);
		out.setSourceName(LocalAuthProvider.SOURCE_NAME);
		out.setUsername(i.getUsername());
		out.setDisplayName(i.getUsername());
		out.setId(i.getId());
		out.setAdmin(true);
		out.addGroup(new BasicGroup(LocalAuthProvider.SOURCE_NAME,LocalAuthProvider.SOURCE_NAME,LocalAuthProvider.ADMIN_ROLE));
		return out;
	}

	
	public boolean resetPassword(UsernamePasswordOTPReset pwr) throws AuthenticationFailedException {

		if(pwr==null) return false;
		
		if(pwr.getUsername()==null) return false;

		LocalUserAuthInfo i = getUserAuthInfo(pwr.getUsername());
		
		if(!StringUtils.isEmpty(pwr.getOldPassword())) {
			
			log.info("reset: old password ... ");

			if(i==null) {
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with previous password - no such user: "+pwr.getUsername());
				return false; 
			}
			
			if(!i.checkPassword(pwr.getOldPassword())) {
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with previous password - wrong password: "+pwr.getUsername());
				log.info("reset: old password ... no match");
				return false; 
			}
			if(!i.checkOtp(pwr.getSecondFactor())) {
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with previous password - wrong OTP: "+pwr.getUsername());
				log.info("reset: old password ... wrong OTP");
				return false; 
			}
			log.info("reset: old password ... updating!");
			auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with previous password - SUCCESS! "+pwr.getUsername());
			setPassword(i.getId(),pwr.getNewPassword());
			return true;
		} else if(!StringUtils.isEmpty(pwr.getToken())) {

			log.info("reset: token ... token!");

			if(i==null) {
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with token: no such user: "+pwr.getUsername());
				return false; 
			}

			if(!i.checkOtp(pwr.getSecondFactor())) {
				log.info("reset: token ... wrong OTP");
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with token: incorrect OTP: "+pwr.getUsername());
				return false;
			}
			
			ResetToken token = getToken(i.getId());
			if(token == null) {
				log.info("reset: token ... no existing token to compare");
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with token: incorrect token: "+pwr.getUsername());
				return false;
			}
			if(token.getToken() == null) {
				log.info("reset: token ... no existing token to compare (null)");
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with token: incorrect token: "+pwr.getUsername());
				return false;
			}
			if(token.getToken().compareTo(pwr.getToken())!=0) {
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with token: incorrect token: "+pwr.getUsername());
				log.info("reset: token ... no existing token incorrect");
				return false;
			}
			auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset with token: SUCCESS! "+pwr.getUsername());
			
			log.info("reset: token ... updating!");
			setPassword(i.getId(), pwr.getNewPassword());
			return true;
		} else if(StringUtils.isEmpty(pwr.getToken())) {
			
			log.info("reset: nothing ... initiate!");

			if(i==null) {
				auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset initiated: no such user: "+pwr.getUsername());
				return true; 
			}
			auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", pwr.getUsername(), "Password reset initiated for: "+pwr.getUsername());
			
			String nt = TokenGenerator.getToken(24);
			setToken(i.getId(), nt, System.currentTimeMillis()+(30*60*1000));
			Map<String,Object> params = new HashMap<String, Object>();
			log.info("reset: nothing ... new token: "+nt);
			
			params.put("id", i.getId());
			params.put("username", i.getUsername());
			params.put("email", i.getEmail());
			params.put("token", nt);
			try {
				mailService.sendMail(i.getEmail(), "localauth",  "passwordReset" , null, params);
			} catch (Exception e) {
				throw new AuthenticationFailedException("error sending mail");
			}
			return true;

		}
		return false;
	}

	public boolean resetPassword(String id) throws AuthenticationFailedException {
		LocalAuthUser user = get(id);
		if(user==null) return false;
		auditLogger.log("LOCAL_AUTH", "PW_RESET", "INIATE", "", IdentityHolder.get().getUser().getSourceId()+":"+IdentityHolder.get().getUser().getDisplayName(), "Password reset initiated: "+user.getUsername());
		// initiate reset
		String nt = TokenGenerator.getToken(24);
		setToken(id, nt, System.currentTimeMillis()+(30*60*1000));
		//send mail:
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("username", user.getUsername());
		params.put("id", user.getId());
		params.put("token", nt);
		try {
			mailService.sendMail(user.getEmail(), "localauth",  "adminPasswordReset" , null, params);
		} catch (Exception e) {
			throw new AuthenticationFailedException("error sending mail");
		}
		return true;
	}
	
	@PostConstruct
	public void init() {
		this.mailService = mailServiceFactory.getDefaultMailService();
	}
	
	
	
}
