package com.autonubil.identity.ovpn.api.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class Ovpn {
	private String id;
	private String name;
	private String description;
	private String clientConfigurationProvider;
	private String serverConfigurationProvider;
	private JsonNode clientConfiguration;
	private JsonNode serverConfiguration;

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

	public String getServerConfigurationProvider() {
		return serverConfigurationProvider;
	}

	public void setServerConfigurationProvider(String serverConfigurationProvider) {
		this.serverConfigurationProvider = serverConfigurationProvider;
	}

	public JsonNode getClientConfiguration() {
		return clientConfiguration;
	}

	public void setClientConfiguration(JsonNode clientConfiguration) {
		if ((clientConfiguration != null) && (!clientConfiguration.isObject()))
			throw new IllegalArgumentException("clientConfiguration must be an JSON Object");
		this.clientConfiguration = clientConfiguration;
	}

	public JsonNode getServerConfiguration() {
		return serverConfiguration;
	}

	public void setServerConfiguration(JsonNode serverConfiguration) {
		if ((serverConfiguration != null) && (!serverConfiguration.isObject()))
			throw new IllegalArgumentException("serverConfiguration must be an JSON Object");
		this.serverConfiguration = serverConfiguration;
	}

}
