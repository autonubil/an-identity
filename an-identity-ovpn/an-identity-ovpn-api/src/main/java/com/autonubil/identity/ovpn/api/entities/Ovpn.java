package com.autonubil.identity.ovpn.api.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class OvpnSource {
	private String id;
	private String name;
	private String description;
	private String clientConfigurationProvider;
	private String serverConfigurationProvider;
	private JsonNode configuration;
	
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
  
	
	
	public JsonNode getConfiguration() {
		return configuration;
	}

	public void setConfiguration(JsonNode configuration) {
		this.configuration = configuration;
	}

	

}
