package com.autonubil.identity.ovpn.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OvpnRemote {
	private String host;
	private int port = 1194;
	private String protocol ="udp";
	
	private OvpnProxy proxy;
	
	public OvpnRemote() {
		
	}
	
	@Override
	public String toString() {
		return String.format("%s %d %s", this.getHost(), this.getPort(), this.getProtocol());
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public OvpnProxy getProxy() {
		return proxy;
	}

	public void setProxy(OvpnProxy proxy) {
		this.proxy = proxy;
	}
	
	
}
