package com.autonubil.identity.ldapotp.api;

public class OtpToken {

	private String id;
	private String dn;
	private String ownerDn;
	private byte[] keyBytes;
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

	public byte[] getKeyBytes() {
		return keyBytes;
	}

	public void setKeyBytes(byte[] keyBytes) {
		this.keyBytes = keyBytes;
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

}
