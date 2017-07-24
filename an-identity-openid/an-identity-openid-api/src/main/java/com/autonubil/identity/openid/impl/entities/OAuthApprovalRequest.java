package com.autonubil.identity.openid.impl.entities;

import java.util.List;

public class OAuthApprovalRequest {
	private String code;
	private String state;
	private String nonce;
	private List<String> scopes;
	private boolean authenticated;
	
	public OAuthApprovalRequest(OAuthApprovalSession session, boolean authenticated) {
		this.code = session.getToken();
		this.state = session.getState();
		this.nonce = session.getNonce();
		this.scopes = session.getScopes();
		this.authenticated = authenticated;
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

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
 
	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}
	
	
	public boolean isAuthenticated() {
		return this.authenticated;
	}
	
	public boolean setAuthenticated(boolean authenticated) {
		return this.authenticated = authenticated;
	}

	
}

