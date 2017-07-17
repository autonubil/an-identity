package com.autonubil.identity.auth.impl.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.services.AuthService;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

	public static final String X_SOURCE = "x-auth-source";
	public static final String X_USERNAME = "x-auth-username";
	public static final String X_PASSWORD = "x-auth-password";
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AuditLogger auditLogger;
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String s = request.getHeader(X_SOURCE);
		String u = request.getHeader(X_USERNAME);
		String p = request.getHeader(X_PASSWORD);
		if(u!=null && p!=null) {
			Identity i = authService.authenticate(new UsernamePasswordOTPCredentials(s,u,p,null));
			auditLogger.log("AUTH", "LOGIN_SUCCESS", "[request-only]", "", i.getUser().getSourceName()+":"+i.getUser().getDisplayName(), "Login succeeded");
			IdentityHolder.set(i);
		}
		return super.preHandle(request, response, handler);
	}
	
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		IdentityHolder.clear();
    }

	
}
