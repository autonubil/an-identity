package com.autonubil.identity.ldap.api.entities;

public class LdapCustomsFieldConfig {

	private String id;
	private String sourceId;
	private String objectClass;
	private String attributeName;
	private String attributeType;
	private String displayName;
	private boolean multi;
	private boolean adminEditable;
	private boolean userEditable;

	public LdapCustomsFieldConfig() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isAdminEditable() {
		return adminEditable;
	}

	public void setAdminEditable(boolean adminEditable) {
		this.adminEditable = adminEditable;
	}

	public boolean isUserEditable() {
		return userEditable;
	}

	public void setUserEditable(boolean userEditable) {
		this.userEditable = userEditable;
	}

}
