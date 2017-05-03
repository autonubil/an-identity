package com.autonubil.identity.auth.api.util;

import com.autonubil.identity.auth.api.entities.UsernamePasswordCredentials;

public class UsernamePasswordOTPCredentials extends UsernamePasswordCredentials {

	private String secondFactor;

	public UsernamePasswordOTPCredentials() {
	}
	
	public UsernamePasswordOTPCredentials(String sourceId, String username, String password, String secondFactor) {
		super(sourceId,username,password);
		this.secondFactor = secondFactor;
	}

	public String getSecondFactor() {
		return secondFactor;
	}

	public void setSecondFactor(String secondFactor) {
		this.secondFactor = secondFactor;
	}

}
