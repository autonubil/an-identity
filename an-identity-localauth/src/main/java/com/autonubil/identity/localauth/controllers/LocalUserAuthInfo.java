package com.autonubil.identity.localauth.controllers;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.autonubil.identity.util.totp.TotpUtil;

public class LocalUserAuthInfo {

	private String id;
	private String username;
	private String email;
	private String cryptedPassword;
	private boolean useOtp = false;
	private String otpSecret;

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

	public String getCryptedPassword() {
		return cryptedPassword;
	}

	public void setCryptedPassword(String cryptedPassword) {
		this.cryptedPassword = cryptedPassword;
	}

	public String getOtpSecret() {
		return otpSecret;
	}

	public void setOtpSecret(String otpSecret) {
		this.otpSecret = otpSecret;
	}

	public boolean checkPassword(String plaintextPassword) {
		if(cryptedPassword==null) return false;
		if(plaintextPassword==null) return false;
		return BCrypt.checkpw(plaintextPassword, cryptedPassword);
	}

	public boolean checkOtp(String secondFactor) {
		if(!useOtp) return true;
		if(otpSecret==null) return true;
		if(secondFactor==null) return false;
		try {
			return TotpUtil.check(otpSecret, secondFactor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
