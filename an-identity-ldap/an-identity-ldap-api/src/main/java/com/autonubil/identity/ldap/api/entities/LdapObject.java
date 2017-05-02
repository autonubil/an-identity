package com.autonubil.identity.ldap.api.entities;

import java.util.ArrayList;
import java.util.List;

public class LdapObject {

	private String id;
	private String dn;
	private List<String> objectClasses = new ArrayList<>();
	private List<LdapCustomField> customFields = new ArrayList<>();

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

	public List<String> getObjectClasses() {
		return new ArrayList<>(objectClasses);
	}

	public void setObjectClasses(List<String> objectClasses) {
		this.objectClasses.clear();
		if(objectClasses!=null) {
			this.objectClasses.addAll(objectClasses);
		}
	}

	public void addObjectClass(String objectClass) {
		if(objectClass!=null) {
			this.objectClasses.add(objectClass);
		}
	}

	public List<LdapCustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<LdapCustomField> customFields) {
		this.customFields = customFields;
	}

}
