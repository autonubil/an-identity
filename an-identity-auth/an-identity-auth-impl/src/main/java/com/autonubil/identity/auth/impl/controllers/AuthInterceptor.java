package com.autonubil.identity.auth.impl.controllers;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.autonubil.identity.auth.api.IdentityStore;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.util.IdentityHolder;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private IdentityStore identityStore;
	
	@Value("${auth.cookie.name}") 
    private String jwtCookieName;
	
	private static Log log = LogFactory.getLog(AuthInterceptor.class); 
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	if(request.getCookies()==null) {
    		return true;
    	}
    	for(Cookie c : request.getCookies()) {
    		if(c.getName().compareTo(jwtCookieName)==0) {
    			Identity identity = identityStore.getIdentity(c.getValue());
    			if(identity == null) { 
					c.setMaxAge(0);
					c.setPath("/");
					response.addCookie(c);
				} else {
					Date d = new Date(System.currentTimeMillis()+(1000*60*5));
					if(identity.getExpires().before(d)) {
						identityStore.update(c.getValue(),identity);
						int expiry = (int)Math.round((identity.getExpires().getTime() - System.currentTimeMillis())/1000);
						c.setMaxAge(expiry);
						c.setPath("/");
						response.addCookie(c);
					}
					IdentityHolder.set(identity);
				}
    		}
    	}
    	return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		IdentityHolder.clear();
    }
    
}
