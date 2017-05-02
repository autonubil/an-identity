package com.autonubil.identity.automigrate.entities;

public class AutomigrateGroupMapping {

	private String fromGroup;
	private String toGroup;
	
	public AutomigrateGroupMapping() {
	}

	public AutomigrateGroupMapping(String fromGroup, String toGroup) {
		this.fromGroup = fromGroup;
		this.toGroup = toGroup;
	}
	
	public String getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}

	public String getToGroup() {
		return toGroup;
	}

	public void setToGroup(String toGroup) {
		this.toGroup = toGroup;
	}

}
