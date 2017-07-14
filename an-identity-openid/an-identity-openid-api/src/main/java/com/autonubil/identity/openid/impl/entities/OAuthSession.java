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

	private String code;
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

	public OAuthSession(String clientId, String code, String state) {
		this(clientId, code, state, null, null, null);
	}

	public OAuthSession(String clientId, String code, String state, String nonce, OAuthApp app, List<String> scopes) {
		this.clientId = clientId;
		SecureRandom random = null;
		if (code == null) {
			random = new SecureRandom();
			code = new BigInteger(130, random).toString(32);
		}
		if (nonce == null) {
			if (random == null) {
				random = new SecureRandom();
			}
			nonce = new BigInteger(130, random).toString(32);
		}
		this.code = code;
		this.state = state;
		this.nonce = nonce;
		if (scopes == null) {
			scopes = new ArrayList<>();
		}
		this.scopes = scopes;
		this.application = app;
		this.issued = new Date();
		this.expires = new Date(this.issued.getTime() + (APPROVAL_VAILIDITY * 1000));
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

}

