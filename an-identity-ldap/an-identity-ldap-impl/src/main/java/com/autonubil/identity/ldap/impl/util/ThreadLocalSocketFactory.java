package com.autonubil.identity.ldap.impl.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class ThreadLocalSocketFactory extends SocketFactory {

	
	private static ThreadLocal<SocketFactory> local = new ThreadLocal<SocketFactory>();
	
	public ThreadLocalSocketFactory() {
	}
	

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return local.get().createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return local.get().createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		return local.get().createSocket(host, port,localHost,localPort);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return local.get().createSocket(address, port, localAddress, localPort);
	}

	public static void set(SocketFactory sf) {
		local.set(sf);
	}
	
	
	public static SocketFactory getDefault() {
		return new ThreadLocalSocketFactory();
	}
	

}
