package com.autonubil.identity.openid.impl.entities;

import java.util.List;

public class OAuthAccessSession extends OAuthSession {

	public static long TOKEN_VAILIDITY = 7200;

	private String refreshToken;
	
	public OAuthAccessSession() {
		super.type = "access";

	}
	
	public OAuthAccessSession(OAuthApprovalSession approvalSession) {
		this(approvalSession.getApplication(), approvalSession.getScopes(), approvalSession.getUserSourceId(), approvalSession.getUserName() );
	}
 


	public OAuthAccessSession(OAuthApp app, List<String> scopes, String userSourceId, String userName) {
		super(app, scopes, userSourceId, userName);
		super.type = "access";
		this.setExpiresIn(TOKEN_VAILIDITY);
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

 
	
	
 	

}
