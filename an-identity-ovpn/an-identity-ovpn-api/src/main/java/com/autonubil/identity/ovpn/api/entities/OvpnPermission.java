package com.autonubil.identity.ovpn.api.entities;

public class OvpnPermission {

	private String ovpnId;
	private String sourceId;
	private String groupId;
	private String name;

	public String getOvpnId() {
		return ovpnId;
	}

	public void setOvpnId(String ovpnId) {
		this.ovpnId = ovpnId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
