package com.autonubil.identity.ovpn.api.entities;

import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;

// PAYLOAD=$(echo "{\"source\": \"${SOURCE}\",  \"username\": \"${USERNAME}\", \"password\": \"${PASSWORD}\", \"link_mtu\" : \"${link_mtu}\", \"tun_mtu\": \"${tun_mtu}\". \"local\" : \"${ifconfig_local}\", \"netmask\" : \"${ifconfig_netmask}\"   }")

public class OvpnServerConfigRequest extends UsernamePasswordOTPCredentials {

	private String local; 
	private String localNetmask;
	private String remote; 
	private String remoteNetmask;
	
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getLocalNetmask() {
		return localNetmask;
	}
	public void setLocalNetmask(String localNetmask) {
		this.localNetmask = localNetmask;
	}
	public String getRemote() {
		return remote;
	}
	public void setRemote(String remote) {
		this.remote = remote;
	}
	public String getRemoteNetmask() {
		return remoteNetmask;
	}

	public void setRemoteNetmask(String remoteNetmask) {
		this.remoteNetmask = remoteNetmask;
	}
	
}
