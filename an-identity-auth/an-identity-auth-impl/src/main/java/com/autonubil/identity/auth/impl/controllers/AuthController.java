package com.autonubil.identity.auth.impl.controllers;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.auth.api.IdentityStore;
import com.autonubil.identity.auth.api.entities.AuthenticationSource;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.AuthenticationFailedException;
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPReset;
import com.autonubil.identity.auth.impl.services.AuthService;

@RestController
@RequestMapping("/autonubil/api/authentication")
public class AuthController {
	
	private static Log log = LogFactory.getLog(AuthController.class);

	@Autowired
	private AuthService authService;

	@Value("${auth.cookie.name}") 
    private String cookieName;
	
	@Autowired
	private IdentityStore identityStore;
	
	@Autowired
	private AuditLogger auditLogger;
	
	
	@RequestMapping(value="/sources",method={RequestMethod.GET})
	public List<AuthenticationSource> listSource() {
		return authService.getSources();
	}
	
	@RequestMapping(value="/user",method={RequestMethod.GET})
	public User getUser(@RequestParam String sourceId, @RequestParam String username) {
		return authService.getUser(sourceId,username);
	}
	
	@RequestMapping(value="/authenticate",method={RequestMethod.POST})
	public Identity authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody UsernamePasswordOTPCredentials credentials) throws AuthException {
		// clear all cookies
		if(request.getCookies()!=null) {
	    	for(Cookie c : request.getCookies()) {
	    		if(c.getName().compareTo(cookieName)==0) {
					c.setMaxAge(0);
					c.setPath("/");
					response.addCookie(c);
	    		}
	    	}
		}

    	// authenticate and (if successful) set new cookie
		Identity i = authService.authenticate(credentials);
		if(i!=null) {
			String sessionId = identityStore.putIdentity(i);
			auditLogger.log("AUTH", "LOGIN_SUCCESS", sessionId, "", i.getUser().getSourceName()+":"+i.getUser().getDisplayName(), "Login succeeded");
			Cookie c = new Cookie(cookieName, sessionId);
			int expiry = (int)Math.round((i.getExpires().getTime() - System.currentTimeMillis())/1000);
			c.setMaxAge(expiry);
			c.setPath("/");
			response.addCookie(c);
		} else {
			auditLogger.log("AUTH", "LOGIN_FAILED", "", "", "[unknown]", "Login failed");
		}
		return i;
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/reset",method={RequestMethod.POST})
	public void reset(HttpServletRequest request, HttpServletResponse response, @RequestBody UsernamePasswordOTPReset pwr) throws AuthException {
		
		log.info("password reset ... ");

		// clear all cookies
		if(request.getCookies()!=null) {
			for(Cookie c : request.getCookies()) {
				if(c.getName().compareTo(cookieName)==0) {
					c.setMaxAge(0);
					c.setPath("/");
					response.addCookie(c);
				}
			}
		}

		if(pwr.getSourceId()==null) {
			log.info("password reset ... NULL source");
			throw new AuthenticationFailedException();
		}
		
		if(!authService.reset(pwr)) {
			log.info("password reset ... calling auth service ... ");
			throw new AuthenticationFailedException();
		}
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/authenticate",method={RequestMethod.DELETE})
	public void authenticate(HttpServletRequest request, HttpServletResponse response) {
		if(request.getCookies()!=null) {
			for(Cookie c : request.getCookies()) {
				if(c.getName().compareTo(cookieName)==0) {
					Identity i = identityStore.getIdentity(c.getValue());
					if(i!=null) {
						auditLogger.log("AUTH", "LOGOUT", c.getValue(), "", i.getUser().getSourceName()+":"+i.getUser().getDisplayName(), "Logout succeeded");
					} else {
						auditLogger.log("AUTH", "LOGOUT", c.getValue(), "", "[null]", "Logout succeeded");
					}
					identityStore.removeIdentity(c.getValue());
					c.setMaxAge(0);
					c.setPath("/");
					response.addCookie(c);
				}
			}
		}
		
	}
	
	@RequestMapping(value="/authenticate",method={RequestMethod.GET})
	public Identity authentication() throws AuthException {
		if(IdentityHolder.get()==null) {
			throw new NotAuthenticatedException();
		}
		Identity i = IdentityHolder.get();
		return i;
	}
	

}
