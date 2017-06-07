package com.autonubil.identity.ovpn.api.entities;

public class OpvnIroute {
	private String network;
	private String netmask;
	
	@Override
	public String toString() {
		return String.format("iroute %s %s", this.getNetwork(), this.getNetmask());
	}

	
	
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	
	
}
