package com.autonubil.identity.localauth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.autonubil.identity.auth.api.AuthenticationProvider;
import com.autonubil.identity.auth.api.Credentials;
import com.autonubil.identity.auth.api.entities.AuthenticationSource;
import com.autonubil.identity.auth.api.entities.BasicUser;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.PasswordReset;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPReset;
import com.autonubil.identity.localauth.entities.LocalAuthUser;
import com.autonubil.identity.localauth.services.LocalAuthUserService;

@Component
@PropertySource(value = { "localauth.properties" })
public class LocalAuthProvider implements AuthenticationProvider {

	private static Log log = LogFactory.getLog(LocalAuthProvider.class);
	
	public static final String SOURCE_NAME = "LOCAL";
	public static final String ADMIN_ROLE = "admin";
	
	@Qualifier("localauth")
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private LocalAuthUserService localAuthUserService;
	
	@Value("${auth.defaults.adminpw}") 
	private String defaultPassword;

	private static final AuthenticationSource localAuthSource = new AuthenticationSource("LOCAL","Local User","","Username",true,true);
			
	@Override
	public List<AuthenticationSource> getSources() {
		return Collections.singletonList(localAuthSource);
	}
	
	@Override
	public boolean supportsCredentials(Credentials c) {
		if(c==null) return false;
		if(!(c instanceof UsernamePasswordOTPCredentials)) return false;
		if(c.getSourceId() == null) return false;
		if(c.getSourceId().compareTo(SOURCE_NAME)!=0) return false;
		return true;
	}
	
	@Override
	public boolean supportsReset(PasswordReset pwr) {
		if(pwr==null) return false;
		if(!(pwr instanceof UsernamePasswordOTPReset)) return false;
		if(pwr.getSourceId() == null) return false;
		if(pwr.getSourceId().compareTo(SOURCE_NAME)!=0) return false;
		return true;
	}
	
	@Override
	public boolean reset(PasswordReset pwr) throws AuthException {
		if(!supportsReset(pwr)) {
			log.info("local auth: unsupported reset: "+pwr.getClass());
			return false;
		}
		UsernamePasswordOTPReset r = (UsernamePasswordOTPReset)pwr;
		log.info("reset: "+r.getUsername());
		return localAuthUserService.resetPassword(r);
	}

	@Override
	public User authenticate(Credentials c) throws AuthException {
		return localAuthUserService.authenticate(c);
	}

	@PostConstruct
	public void init() {
		NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(dataSource);
		Integer i = t.queryForObject("SELECT count(*) FROM admin_user", new HashMap<>(), Integer.class);
		if(i == 0) {
			log.info("creating local user: admin, password: "+defaultPassword);
			// create new admin user
			Map<String,Object> params = new HashMap<>();
			params.put("id", UUID.randomUUID().toString());
			params.put("username", "admin");
			params.put("password", defaultPassword);
			t.update("insert into admin_user (id,username) values (:id,:username)",  params);
			params.put("admin_user_id", params.get("id"));
			params.put("password", BCrypt.hashpw(defaultPassword, BCrypt.gensalt()));
			t.update("insert into admin_user_password (admin_user_id,password) values (:admin_user_id,:password)",  params);
		}
	}
	
	@Override
	public List<User> getLinked(User u) {
		return new ArrayList<>();
	}
	
	
	@Override
	public String toString() {
		return getClass().getName();
	}
	
	
	@Override
	public User getUser(String sourceId, String username) {
		if(sourceId.compareTo(SOURCE_NAME)==0) {
			try {
				BasicUser u = new BasicUser();
				LocalAuthUser lau = localAuthUserService.list(null, null, username, null, 0, 1).get(0);
				u.setDisplayName(lau.getUsername());
				u.setAdmin(true);
				u.setUsername(lau.getUsername());
				return u;
			} catch (Exception e) {
				// none found?
			}
		}
		return null;
	}
		
	
}
