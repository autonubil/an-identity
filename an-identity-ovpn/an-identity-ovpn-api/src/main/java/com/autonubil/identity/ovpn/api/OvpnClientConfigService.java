package com.autonubil.identity.ovpn.api;

import com.fasterxml.jackson.databind.JsonNode;

public interface OvpnClientConfigService {
	String getName();
	String getDisplayName();
	String getDescription();
	
	JsonNode getConfigruation();
	void setConfigruation(JsonNode  configuration);
}
