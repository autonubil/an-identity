package com.autonubil.identity.auth.impl.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;
import com.autonubil.identity.auth.impl.services.AuthService;

@Component
public class HeaderAuthInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private AuthService authService;
	
    private static final String headerNameUser = "x-auth-user";
    private static final String headerNamePassword = "x-auth-password";
    private static final String headerNameSource = "x-auth-source";
	
	private static Log log = LogFactory.getLog(HeaderAuthInterceptor.class); 
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	if(
    		request.getHeader(headerNameUser)!=null && 
    		request.getHeader(headerNamePassword)!=null &&     		
    		request.getHeader(headerNameSource)!=null     		
    	) {
    		UsernamePasswordOTPCredentials c = new UsernamePasswordOTPCredentials(request.getHeader(headerNameSource), request.getHeader(headerNameUser), request.getHeader(headerNamePassword),null);
    		IdentityHolder.set(authService.authenticate(c));
    	}
    	return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		IdentityHolder.clear();
    }
    
}
