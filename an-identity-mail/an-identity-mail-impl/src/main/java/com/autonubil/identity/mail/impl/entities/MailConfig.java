package com.autonubil.identity.mail.impl.entities;

import java.util.HashMap;
import java.util.Map;

public class MailConfig {

	public enum ENCRYPTION {
		NONE, START_TLS, TLS
	};

	private String id;
	private String name;
	private String description;
	private String host;
	private String sender;
	private String cert;
	private int port;
	private ENCRYPTION encryption;
	private boolean auth;
	private String username;
	private Map<String,Object> params = new HashMap<>();


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public ENCRYPTION getEncryption() {
		return encryption;
	}

	public void setEncryption(ENCRYPTION encryption) {
		this.encryption = encryption;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Map<String,Object> getParams() {
		if(this.params == null) {
			this.params = new HashMap<>();
		}
		return new HashMap<>(params);
	}

	public void setParams(Map<String,Object> params) {
		this.params = params;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

}
