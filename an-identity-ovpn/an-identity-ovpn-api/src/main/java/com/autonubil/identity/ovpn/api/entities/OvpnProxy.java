package com.autonubil.identity.ovpn.api.entities;

public class OvpnProxy {
	private String host;
	private int port;
	private boolean retry = false;
	private long timeout = 5;
	
	@Override
	public String toString() {
		return String.format("%s %d", this.getHost(), this.getPort());
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
	public boolean isRetry() {
		return retry;
	}
	public void setRetry(boolean retry) {
		this.retry = retry;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	
}
