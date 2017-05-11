package com.autonubil.identity.ldapotp.api;

import java.util.Date;

public class OtpToken {

	private String id;
	private String dn;
	private String ownerDn;
	private String secret;
	private Date created;
	private int offsetSeconds;
	private int stepSeconds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getOwnerDn() {
		return ownerDn;
	}

	public void setOwnerDn(String ownerDn) {
		this.ownerDn = ownerDn;
	}



	public int getOffsetSeconds() {
		return offsetSeconds;
	}

	public void setOffsetSeconds(int offsetSeconds) {
		this.offsetSeconds = offsetSeconds;
	}

	public int getStepSeconds() {
		return stepSeconds;
	}

	public void setStepSeconds(int stepSeconds) {
		this.stepSeconds = stepSeconds;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
