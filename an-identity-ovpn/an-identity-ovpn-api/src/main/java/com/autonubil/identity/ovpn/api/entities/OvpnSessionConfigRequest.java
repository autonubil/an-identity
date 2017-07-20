package com.autonubil.identity.ovpn.api.entities;

import java.util.Date;

import com.autonubil.identity.auth.api.util.UsernamePasswordOTPCredentials;

// PAYLOAD=$(echo "{\"source\": \"${SOURCE}\",  \"username\": \"${USERNAME}\", \"password\": \"${PASSWORD}\", \"link_mtu\" : \"${link_mtu}\", \"tun_mtu\": \"${tun_mtu}\". \"local\" : \"${ifconfig_local}\", \"netmask\" : \"${ifconfig_netmask}\"   }")

public class OvpnSessionConfigRequest extends UsernamePasswordOTPCredentials {

	private String vpnPid;
	private String local; 
	private String localNetmask;
	private String remote; 
	private String remoteNetmask;
	private long connected;
	
	public long getConnected() {
		return connected;
	}
	public void setConnected(long connected) {
		this.connected = connected;
	}
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
	public String getVpnPid() {
		return vpnPid;
	}
	public void setVpnPid(String vpnPid) {
		this.vpnPid = vpnPid;
	}
	
	
}
