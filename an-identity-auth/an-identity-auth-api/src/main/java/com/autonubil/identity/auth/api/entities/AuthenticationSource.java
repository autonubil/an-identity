package com.autonubil.identity.auth.api.entities;

public class AuthenticationSource {

	private String sourceId;
	private String sourceName;
	private String description;
	private String usernameField;
	private boolean secondFactor;
	private boolean allowReset;
	
	public AuthenticationSource() {
	}

	public AuthenticationSource(String sourceId, String name, String description, String usernameField, boolean secondFactor, boolean allowReset) {
		super();
		this.sourceId = sourceId;
		this.sourceName = name;
		this.description = description;
		this.usernameField = usernameField;
		this.secondFactor = secondFactor;
		this.allowReset = allowReset;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String name) {
		this.sourceName = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUsernameField() {
		return usernameField;
	}

	public void setUsernameField(String usernameField) {
		this.usernameField = usernameField;
	}

	public boolean isSecondFactor() {
		return secondFactor;
	}

	public void setSecondFactor(boolean secondFactor) {
		this.secondFactor = secondFactor;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public boolean isAllowReset() {
		return allowReset;
	}

	public void setAllowReset(boolean allowReset) {
		this.allowReset = allowReset;
	}
	
}
