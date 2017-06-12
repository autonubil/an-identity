package com.autonubil.identity.ovpn.api.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class Ovpn {
	private String id;
	private String name;
	private String description;
	private String clientConfigurationProvider;
	private String sessionConfigurationProvider;
	private JsonNode clientConfiguration;
	private JsonNode sessionConfiguration;
	

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

	public String getClientConfigurationProvider() {
		return clientConfigurationProvider;
	}

	public void setClientConfigurationProvider(String clientConfigurationProvider) {
		this.clientConfigurationProvider = clientConfigurationProvider;
	}

	public String getSessionConfigurationProvider() {
		return sessionConfigurationProvider;
	}

	public void setSessionConfigurationProvider(String serverConfigurationProvider) {
		this.sessionConfigurationProvider = serverConfigurationProvider;
	}

	public JsonNode getClientConfiguration() {
		return clientConfiguration;
	}

	public void setClientConfiguration(JsonNode clientConfiguration) {
		if ((clientConfiguration != null) && (!clientConfiguration.isObject()))
			throw new IllegalArgumentException("clientConfiguration must be an JSON Object");
		this.clientConfiguration = clientConfiguration;
	}

	public JsonNode getSessionConfiguration() {
		return sessionConfiguration;
	}

	public void setSessionConfiguration(JsonNode sessionConfiguration) {
		if ((sessionConfiguration != null) && (!sessionConfiguration.isObject()))
			throw new IllegalArgumentException("serverConfiguration must be an JSON Object");
		this.sessionConfiguration = sessionConfiguration;
	}

}
