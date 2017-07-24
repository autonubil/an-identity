package com.autonubil.identity.openid.impl.entities;

import java.util.List;

public class OAuthRefreshSession extends OAuthSession {
	public static long REFRESH_TOKEN_VAILIDITY = 60*60*24*1;

	
	public OAuthRefreshSession() {
		super.type = "refresh";
	}

	
	public OAuthRefreshSession(OAuthAccessSession accessSession) {
		this(accessSession.getApplication(), accessSession.getScopes(), accessSession.getUserSourceId(), accessSession.getUserName() );
	}
 


	public OAuthRefreshSession(OAuthApp app, List<String> scopes, String userSourceId, String userName) {
		super(app, scopes, userSourceId, userName);
		super.type = "refresh";
		this.setExpiresIn(REFRESH_TOKEN_VAILIDITY);
	}
 	

}
