package com.autonubil.identity.ovpn.api.entities;

public class OvpnIfConfigPush {
	private String local;
	private String remoteNetmask;
	
	@Override
	public String toString() {
		return String.format("ifconfig-push %s %s", this.getLocal(), this.getRemoteNetmask());
	}

	
	
	public String getLocal() {
		return local;
	}
	public void setLocal(String network) {
		this.local = network;
	}
	public String getRemoteNetmask() {
		return remoteNetmask;
	}
	public void getRemoteNetmask(String netmask) {
		this.remoteNetmask = netmask;
	}
}
