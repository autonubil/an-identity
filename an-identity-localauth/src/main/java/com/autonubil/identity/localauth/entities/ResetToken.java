package com.autonubil.identity.localauth.entities;

import java.util.Date;

public class ResetToken {

	private String userId;
	private String token;
	private Date tokenExpires;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getTokenExpires() {
		return tokenExpires;
	}

	public void setTokenExpires(Date tokenExpires) {
		this.tokenExpires = tokenExpires;
	}

}
