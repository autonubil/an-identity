package com.autonubil.identity.ldap.api.entities;

import com.autonubil.identity.auth.api.entities.Group;

public class LdapGroup extends LdapObject implements Comparable<LdapGroup>, Group {

	private String sourceId;
	private String sourceName;
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String source) {
		this.sourceId = source;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public int compareTo(LdapGroup o) {
		if(o==null) {
			return -1;
		}
		return (getDisplayName()+"").compareToIgnoreCase(o.getDisplayName()+"");
	}
	
	@Override
	public String getName() {
		return getDisplayName();
	}
	
	@Override
	public void setName(String name) {
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LdapGroup)) {
			return false;
		}
		return this.getId().compareTo(((LdapGroup)obj).getId())==0;
	}

}
