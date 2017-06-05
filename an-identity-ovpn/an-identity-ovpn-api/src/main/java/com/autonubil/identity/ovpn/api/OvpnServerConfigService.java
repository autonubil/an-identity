package com.autonubil.identity.ovpn.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface OvpnServerConfigService {
	String getId();
	String getClassName();
	String getDisplayName();
	String getDescription();
	
	JsonNode getConfigruation();
	void setConfigruation(JsonNode  configuration) throws JsonProcessingException;
}
