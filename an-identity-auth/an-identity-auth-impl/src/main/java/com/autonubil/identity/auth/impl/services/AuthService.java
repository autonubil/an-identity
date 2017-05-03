package com.autonubil.identity.auth.impl.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.AuthenticationListener;
import com.autonubil.identity.auth.api.AuthenticationProvider;
import com.autonubil.identity.auth.api.Credentials;
import com.autonubil.identity.auth.api.entities.AuthenticationSource;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.util.PasswordReset;

@Service
public class AuthService {

	private static Log log = LogFactory.getLog(AuthService.class);

	@Autowired
	private List<AuthenticationProvider> authProviders = new ArrayList<>();
	
	@Autowired(required=false)
	private List<AuthenticationListener> authListeners = new ArrayList<>();
	
	public boolean reset(PasswordReset pwr) throws AuthException {
		long start = System.currentTimeMillis();
		log.info("auth: check password reset ... ");
		try {
			for(AuthenticationProvider ap : authProviders) {
				boolean s = ap.supportsReset(pwr);
				log.info(" --- > "+ap+" supports? "+s);
				if(s) {
					boolean b = ap.reset(pwr);
					log.info("reset successful? "+b);
					return b;
				}
			}
		} catch (AuthException e) {
			log.error("error in reset: ",e);
			throw e;
		} finally {
			try {
				Thread.sleep((start+1000)-System.currentTimeMillis());
			} catch (InterruptedException e) {
			}
		}
		return false;
	}

	
	public Identity authenticate(Credentials c) throws AuthException {
		Identity identity = new Identity();
		User user = null;
		long start = System.currentTimeMillis();
		try {
			log.info("looking for authentication provider for: "+c.getSourceId());
			int count = 0;
			for(AuthenticationProvider ap : authProviders) {
				count++;
				log.debug("looking for authentication provider: "+count+" / "+authProviders.size());
				if(ap.supportsCredentials(c)) {
					log.debug(" >>> authentication provider matches!");
					try {
						user = ap.authenticate(c);
					} catch (Exception e) {
						log.error("error authenticating .... ",e);
					}
					log.info("authenticated: "+(identity==null?"[no]":identity.getUser()));
					break;
				} else {
					log.debug(" >>> authentication provider does not match!");
				}
			}
			if(user!=null) {
				identity.setUser(user);
				for(AuthenticationProvider ap : authProviders) {
					try {
						for(User u : ap.getLinked(user)) {
							identity.addLinked(u);
						}
					} catch (Exception e) {
						log.warn("unable to get linked users: ",e);
					}
				}
				
				/// notify listeners
				if(authListeners!=null) {
					for(AuthenticationListener al : authListeners) {
						try {
							al.userLogin(c, identity);
						} catch (Exception e) {
							log.warn("authentication listener threw an exception: ",e);
						}
					}
				}
				
				
				log.debug("returning: "+identity.getUser());
			} else {
				throw new NotAuthenticatedException();
			}
		} finally {
			try {
				long x = (start+2000)-System.currentTimeMillis();
				if(x>0) {
					Thread.sleep(x);
				}
			} catch (InterruptedException e) {
			}
		}
		return identity;
	}
	
	@PostConstruct
	public void init() {
		log.info("auth service started, got auth providers: ");
		for(AuthenticationProvider ap : authProviders) {
			log.info(" - "+ap.getClass());
		}
	}

	@Autowired
	public List<AuthenticationSource> getSources() {
		List<AuthenticationSource> out = new ArrayList<>();
		for(AuthenticationProvider p : authProviders) {
			out.addAll(p.getSources());
		}
		return out;
	}

	
	
}
