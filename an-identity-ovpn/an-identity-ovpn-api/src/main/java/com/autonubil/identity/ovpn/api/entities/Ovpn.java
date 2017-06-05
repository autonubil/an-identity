package com.autonubil.identity.ovpn.api.entities;

import org.springframework.beans.factory.annotation.Autowired;

import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.fasterxml.jackson.databind.JsonNode;

public class Ovpn {
	
	@Autowired
	private OvpnConfigService ovpnConfigService;
	
	private String id;
	private String name;
	private String description;
	private String clientConfigurationProvider;
	private String serverConfigurationProvider;
	private OvpnClientConfigService clientConfigurationService;
	
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
		this.clientConfiguration = clientConfiguration;
	}

	public JsonNode getServerConfiguration() {
		return serverConfiguration;
	}

	public void setServerConfiguration(JsonNode serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

}
