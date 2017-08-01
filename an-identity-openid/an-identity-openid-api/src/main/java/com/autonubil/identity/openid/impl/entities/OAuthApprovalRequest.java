package com.autonubil.identity.openid.impl.entities;

import java.util.List;

import com.autonubil.identity.apps.api.entities.App;

public class OAuthApprovalRequest {
	private String code;
	private String state;
	private String nonce;
	private List<String> scopes;
	private boolean authenticated;
	
	private App linkedApplication;
	
	public OAuthApprovalRequest(OAuthApprovalSession session, boolean authenticated, App linkedApplication) {
		this.code = session.getToken();
		this.state = session.getState();
		this.nonce = session.getNonce();
		this.scopes = session.getScopes();
		this.authenticated = authenticated;
		this.linkedApplication = linkedApplication;
	}

	public OAuthApprovalRequest(OAuthApprovalSession session, boolean authenticated) {
		this(session, authenticated, null);
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

	public App getLinkedApplication() {
		return linkedApplication;
	}

	public void setLinkedApplication(App linkedApplication) {
		this.linkedApplication = linkedApplication;
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

