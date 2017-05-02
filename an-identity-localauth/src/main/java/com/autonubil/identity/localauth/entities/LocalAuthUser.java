package com.autonubil.identity.localauth.entities;

public class LocalAuthUser {

	private String id;
	private String username;
	private String email;
	private boolean useOtp = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isUseOtp() {
		return useOtp;
	}

	public void setUseOtp(boolean useOtp) {
		this.useOtp = useOtp;
	}

}
