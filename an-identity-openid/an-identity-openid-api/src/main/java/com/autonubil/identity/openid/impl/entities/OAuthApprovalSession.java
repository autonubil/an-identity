package com.autonubil.identity.openid.impl.entities;

import java.math.BigInteger;
import java.util.List;

public class OAuthApprovalSession extends OAuthSession {

	public static long TOKEN_VAILIDITY = 600;

	private String state;
	private String nonce;

	public OAuthApprovalSession() {
		super.type = "approval";
	}
	
	public OAuthApprovalSession(String  code, String state, String nonce, OAuthApp app, List<String> scopes, String userSourceId, String userName) {
		super(app, scopes, userSourceId, userName);
		super.type = "approval";
		if (code != null) {
			this.token = code;
		}

		if (nonce == null) {
			this.nonce = new BigInteger(130, random).toString(64);
		} else {
			this.nonce = nonce;
		}
		this.state = state;
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


}
