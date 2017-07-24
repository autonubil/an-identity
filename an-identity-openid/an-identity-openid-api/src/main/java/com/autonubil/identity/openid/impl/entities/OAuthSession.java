package com.autonubil.identity.openid.impl.entities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.services.AuthService;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class OAuthSession {

	public static long DEFAULT_VAILIDITY = 600;

	protected static final SecureRandom random =  new SecureRandom();
	
	protected String type;
	protected String token;
	
	protected Date issued;
	protected Date expires;
	protected OAuthApp application;
	protected List<String> scopes;
	protected String userName;
	protected String userSourceId;

	@JsonIgnore
	protected String error;

	public OAuthSession() {
	}

	
	public OAuthSession(OAuthApp app, List<String> scopes, String userSourceId, String userName) {
		this.application = app;
		
		if (scopes == null) {
			this.scopes = new ArrayList<>();
		} else {
			this.scopes = scopes;
		}
		this.userSourceId = userSourceId;
		this.userName = userName;
		this.generateNewToken();
		this.issued = new Date();
		this.setExpiresIn(DEFAULT_VAILIDITY);
	}
	
	protected void setExpiresIn(long inSeconds) {
		this.expires = new Date(this.issued.getTime() + (inSeconds * 1000));
	}
	
	protected void generateNewToken() {
		
		this.token = new BigInteger(130, random).toString(64);
	}
	
	public OAuthSession(String error) {
		this.error = error;
	}

	
	public OAuthApp getApplication() {
		return application;
	}

	public void setApplication(OAuthApp application) {
		this.application = application;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

 
	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		if (scopes == null) {
			throw new NullPointerException("scopes must not be null");
		}
		this.scopes = scopes;
	}


	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
	}

	@JsonIgnore
	public void setUser(User user) {
		if (user == null) {
			this.userName = null;
			this.userSourceId = null;
		} else {
			this.userName = user.getUsername();
			this.userSourceId = user.getSourceId();
		}
	}

	@JsonIgnore
	public User getUser(AuthService authService) {
		if ( (this.userName == null) || (this.userSourceId == null) ) {
			return null;
		}

		if (authService == null) {
			return null; 
		} else {
			User user = authService.getUser(this.userSourceId, this.userName );
			return user;
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserSourceId() {
		return userSourceId;
	}

	public void setUserSourceId(String userSourceId) {
		this.userSourceId = userSourceId;
	}


	@JsonIgnore
	public String getClientId() {
		return this.application.getId();
	}

 
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
 
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}



}

