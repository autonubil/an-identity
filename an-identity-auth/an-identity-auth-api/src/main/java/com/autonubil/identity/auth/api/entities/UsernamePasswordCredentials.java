package com.autonubil.identity.auth.api.entities;

import com.autonubil.identity.auth.api.Credentials;

public class UsernamePasswordCredentials implements Credentials {

	private String username;
	private String password;
	private String sourceId;

	public UsernamePasswordCredentials() {
	}
	
	public UsernamePasswordCredentials(String sourceId, String username, String password) {
		this.username = username;
		this.password = password;
		this.sourceId = sourceId;
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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

}
