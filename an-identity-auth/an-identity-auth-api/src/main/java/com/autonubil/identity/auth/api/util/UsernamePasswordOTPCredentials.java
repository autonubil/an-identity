package com.autonubil.identity.auth.api.util;

import com.autonubil.identity.auth.api.Credentials;

public class UsernamePasswordOTPCredentials implements Credentials {

	private String sourceId;
	private String username;
	private String password;
	private String secondFactor;

	public UsernamePasswordOTPCredentials() {
	}
	
	public UsernamePasswordOTPCredentials(String sourceId, String username, String password, String secondFactor) {
		super();
		this.sourceId = sourceId;
		this.username = username;
		this.password = password;
		this.secondFactor = secondFactor;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecondFactor() {
		return secondFactor;
	}

	public void setSecondFactor(String secondFactor) {
		this.secondFactor = secondFactor;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
