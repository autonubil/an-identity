package com.autonubil.identity.automigrate.entities;

import java.util.ArrayList;
import java.util.List;

public class AutomigrateConfig {

	private String id;
	private String fromLdap;
	private String toLdap;
	
	private List<AutomigrateGroupMapping> groupMappings = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromLdap() {
		return fromLdap;
	}

	public void setFromLdap(String fromLdap) {
		this.fromLdap = fromLdap;
	}

	public String getToLdap() {
		return toLdap;
	}

	public void setToLdap(String toLdap) {
		this.toLdap = toLdap;
	}

	public List<AutomigrateGroupMapping> getGroupMappings() {
		return new ArrayList<>(groupMappings);
	}

	public void setGroupMappings(List<AutomigrateGroupMapping> groupMappings) {
		this.groupMappings.clear();
		if(groupMappings!=null) {
			this.groupMappings.addAll(groupMappings);
		}
	}
	
	public void addGroupMapping(String fromGroup, String toGroup) {
		this.groupMappings.add(new AutomigrateGroupMapping(fromGroup,toGroup));
	}
	
	
}
