package com.autonubil.identity.ldap.impl.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.auth.api.AuthenticationProvider;
import com.autonubil.identity.auth.api.Credentials;
import com.autonubil.identity.auth.api.entities.AuthenticationSource;
import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AccountExpiredException;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.AuthenticationFailedException;
import com.autonubil.identity.auth.api.exceptions.PasswordExpiredException;
import com.autonubil.identity.auth.api.util.PasswordReset;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPReset;
import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.mail.api.MailServiceFactory;

@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {

	private static Log log = LogFactory.getLog(LdapAuthenticationProvider.class);
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Autowired
	private MailServiceFactory mailServiceFactory;
	
	@Autowired
	private LdapConfigService ldapConfigService;
 	
	@Autowired
	private ResetTokenService tokenService;
	
	@Override
	public List<AuthenticationSource> getSources() {
		List<AuthenticationSource> out = new ArrayList<>();
		for(LdapConfig lc : ldapConfigService.list(null, null, true)) {
			AuthenticationSource as = new AuthenticationSource();
			as.setSourceName(lc.getName());
			as.setSecondFactor(lc.isUseOtp());
			as.setSourceId(lc.getId());
			out.add(as);
		}
		return out;
	}

	private AuthenticationSource getAuthenticationSource(String sourceId) {
		for(AuthenticationSource as : getSources()) {
			if(as.matches(sourceId)) {
				return as;
			}
		}
		return null;
	}
	
	@Override
	public boolean supportsCredentials(Credentials c) {
		AuthenticationSource as = getAuthenticationSource(c.getSourceId());
		if(as==null) {
			log.info("ldap authentication source with id "+c.getSourceId()+" does not exist!");
			return false;
		}
		c.setSourceId(as.getSourceId());
		if(as.getSourceId().compareTo(c.getSourceId())!=0) {
			log.info("ldap authentication source "+as.getSourceId()+" does not match: "+c.getSourceId());
			return false;
		}
		log.info("ldap authentication source "+as.getSourceId()+" matches: "+c.getSourceId());
		return true;
	}

	@Override
	public boolean supportsReset(PasswordReset pwr) {
		return getAuthenticationSource(pwr.getSourceId())!=null;
	}

	protected LdapUser authenticateInternal(Credentials c) throws Exception  {

		log.warn("authenticating user using ldap connection ... ");
		UsernamePasswordOTPCredentials upc = (UsernamePasswordOTPCredentials)c;
		log.warn("authenticating user using ldap connection: "+c.getSourceId());
		LdapConnection conn = ldapConfigService.connect(c.getSourceId());
		if(conn==null) {
			log.warn("unable to connect to source!");
			return null;
		}
		log.debug("connection is of type: "+conn.getClass());
		LdapUser u = conn.authenticate(upc.getUsername(),upc.getPassword(),upc.getSecondFactor());
		for(Group g : conn.getGroupsForUser(u.getId(), true)) {
			u.addGroup(g);
		}
		return u;
		
		
	}
	
	@Override
	public User authenticate(Credentials c) throws Exception {
		LdapUser lu = authenticateInternal(c);
		if(lu==null) {
			throw new AuthenticationFailedException();
		}
		if(lu.getUserExpires()==null || lu.getUserExpires().getTime()<1) {
			// ignore
		} else if(lu.getUserExpires().before(new Date())) {
			throw new AccountExpiredException("Your account is expired. Please contact your administrator.");
		}
		if(lu.getPasswordExpires()==null || lu.getPasswordExpires().getTime()<1) {
			// ignore
		} else if(lu.getPasswordExpires().before(new Date())) {
			throw new PasswordExpiredException("Your password has expired. Please change it.");
		}
		try {
			return lu;
		} catch (Exception e) {
			log.warn("authentication failed",e);
			throw new AuthenticationFailedException();
		}
	}

	@Override
	public boolean reset(PasswordReset r) throws AuthException {

		
		log.info("ldap auth provider: reset!");
		if(!supportsReset(r)) {
			log.info("ldap auth: unsupported reset: "+r.getClass());
			return false;
		}
		
		UsernamePasswordOTPReset pwr = (UsernamePasswordOTPReset)r;
		
		LdapConnection conn = ldapConfigService.connect(pwr.getSourceId());
		LdapUser lu = null;
		
		try {
			log.info("ldap auth provider: getting user!");
			lu = conn.getUserByName(pwr.getUsername());
		} catch (Exception e) {
			log.error("error finding user: ",e);
			return false;
		}

		if(lu==null) {
			log.info("no such user: "+pwr.getUsername());
		} else {
			log.info("ldap auth provider, user is: "+lu.getDn());
		}
		
		log.info("reset: "+pwr.getSourceId()+" / "+pwr.getUsername());
		if(!StringUtils.isEmpty(pwr.getOldPassword())) {
			log.info("reset with old password ... ");
			try {
				String userId = conn.authenticate(pwr.getUsername(), pwr.getOldPassword(), pwr.getSecondFactor()).getId();
				conn.setPassword(userId,pwr.getNewPassword());
				auditLogger.log("LDAP_AUTH", "PW_RESET", "", "",null, "Password reset with old password succeeded for: "+pwr.getUsername());
				return true;
			} catch (Exception e) {
				log.error("error setting password (with old password): ",e);
				auditLogger.log("LDAP_AUTH", "PW_RESET", "", "",null, "Password reset with old password failed for: "+pwr.getUsername());
				return false;
			}
		} else if(!StringUtils.isEmpty(pwr.getToken())) {
			log.info("reset with token ... ");
			if(tokenService.checkToken(pwr.getSourceId(),lu.getId(),pwr.getToken(), pwr.getEmail())) {
				auditLogger.log("LDAP_AUTH", "PW_RESET", "", "",null, "Password reset with token succeeded for: "+pwr.getUsername());
				try {
					conn.setPassword(lu.getId(),pwr.getNewPassword());
				} catch (Exception e) {
					log.error("error resetting password: ",e);
					throw new AuthException(e.getMessage(), e);
				}
				return true;
			}
			auditLogger.log("LDAP_AUTH", "PW_RESET", "", "",null, "Password reset with token failed for: "+pwr.getUsername()+" (incorrect token)");
			return false;
		} else {
			if(lu==null) {
				auditLogger.log("LDAP_AUTH", "PW_RESET", "", "",null, "User doesn't exist: "+pwr.getUsername());
				return true;
			}
			log.info("reset initiate ... ");
			auditLogger.log("LDAP_AUTH", "PW_RESET", "", "",null, "Password reset initiated for: "+pwr.getUsername());
			Date d = new Date(System.currentTimeMillis()+(30*60000));
			String token = tokenService.createToken(pwr.getSourceId(), lu.getId(), lu.getMail(), d);
			Map<String,Object> params = new HashMap<>();
			params.put("token", token);
			params.put("token_expires", d);
			params.put("user", lu);
			try {
				mailServiceFactory.getDefaultMailService().sendMail(lu.getMail(), "ldapconfig", "passwordReset", null, params);
			} catch (Exception e) {
				throw new AuthenticationFailedException("error sending mail");
			}
			return true;
		}
	}

	@Override
	public List<User> getLinked(User u) throws Exception {
		List<User> out = new ArrayList<>();
		for(LdapConfig lConf : ldapConfigService.list(null, null, null)) {
			LdapConnection lc = ldapConfigService.connect(lConf.getId());
			if(lc!=null) {
				if(u instanceof LdapUser) {
					for(LdapUser lu : lc.listUsers(null, null, null, (LdapUser)u, 0, 1000)) {
						for(Group g : lc.getGroupsForUser(lu.getId(), true)) {
							lu.addGroup(g);
						}
						out.add(lu);
					}
				}
			}
		}
		return out;
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}
	
	@Override
	public User getUser(String sourceId, String username) {
		LdapConnection conn = ldapConfigService.connect(sourceId);
		if(conn==null) {
			log.warn("unable to connect to source!");
			return null;
		}
		try {
			return conn.getUserByName(username);
		} catch (Exception e) {
			// ...
		}
		return null;
	}
	
}
