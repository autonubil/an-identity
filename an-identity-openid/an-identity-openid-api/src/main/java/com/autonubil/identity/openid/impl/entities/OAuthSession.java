package com.autonubil.identity.openid.impl.entities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.services.AuthService;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OAuthSession {

	public static long APPROVAL_VAILIDITY = 600;
	public static long TOKEN_VAILIDITY = 7200;
	public static long REFRESH_TOKEN_VAILIDITY = 60*60*24*1;

	private String code;
	private String refreshToken;
	private String state;
	private String nonce;
	private String error;
	private String clientId;
	private Date issued;
	private Date expires;
	private OAuthApp application;
	private List<String> scopes;
	private String userName;
	private String userSourceId;

	public OAuthSession() {
	}

	public OAuthSession(String error) {
		this.error = error;
	}

	public OAuthSession(String clientId, String state) {
		this(clientId, state, null, null, null, null);
	}

	public void generateRefreshToken() {
		SecureRandom random =  new SecureRandom();
		this.refreshToken = new BigInteger(130, random).toString(32);
	}

	public OAuthSession(String clientId, String state, String nonce, OAuthApp app, List<String> scopes, User user) {
		this.clientId = clientId;
		SecureRandom random =  new SecureRandom();
		this.code = new BigInteger(130, random).toString(32);
		
		if (nonce == null) {
			this.nonce = new BigInteger(130, random).toString(32);
		} else {
			this.nonce = nonce;
		}

		this.state = state;
		
		if (scopes == null) {
			scopes = new ArrayList<>();
		}
		this.scopes = scopes;
		this.application = app;
		this.issued = new Date();
		this.expires = new Date(this.issued.getTime() + (APPROVAL_VAILIDITY * 1000));
		if (user != null) {
			this.setUser(user);
		}
	}

	public OAuthApp getApplication() {
		return application;
	}

	public void setApplication(OAuthApp application) {
		this.application = application;
	}

	// from approval to token
	public String upgrade() {
		SecureRandom random = new SecureRandom();
		this.code = new BigInteger(130, random).toString(32);
		this.issued = new Date();
		this.expires = new Date(this.issued.getTime() + (TOKEN_VAILIDITY * 1000));
		return this.code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
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

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}

