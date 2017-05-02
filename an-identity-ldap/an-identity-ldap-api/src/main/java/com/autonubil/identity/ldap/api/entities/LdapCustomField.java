package com.autonubil.identity.ldap.api.entities;

import java.util.ArrayList;
import java.util.List;

public class LdapCustomField {

	private String attributeName;
	private String attributeType;
	private String displayName;
	private boolean multi;
	private List<Object> values = new ArrayList<>();

	public LdapCustomField() {
	}

	public LdapCustomField(LdapCustomsFieldConfig lcf) {
		this.attributeName = lcf.getAttributeName();
		this.attributeType = lcf.getAttributeType();
		this.displayName = lcf.getDisplayName();
		this.multi = lcf.isMulti();
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

	public List<Object> getValues() {
		return new ArrayList<>(values);
	}

	public void setValues(List<Object> values) {
		this.values.clear();
		if(values!=null) {
			this.values.addAll(values);
		}
	}

	public void addValue(Object value) {
		if(value!=null) {
			this.values.add(value);
		}
	}
	
}

