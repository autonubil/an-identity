package com.autonubil.identity.ovpn.api.entities;

public class ConfigProvider {
	
	private String id;
	private String className;
	private String displayName;
	private String description;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String name) {
		this.className = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
