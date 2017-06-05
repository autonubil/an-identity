package com.autonubil.identity.ovpn.api.entities;

public class OvpnRemote {
	private String host;
	private int port = 1194;
	private String protocol ="udp";
	
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
	
}
