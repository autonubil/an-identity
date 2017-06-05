package com.autonubil.identity.ovpn.api;

import java.io.IOException;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface OvpnClientConfigService {
	String getId();
	String getClassName();
	String getDisplayName();
	String getDescription();
	
	JsonNode getConfigruation();
	
	void setConfigruation(JsonNode  configuration) throws JsonProcessingException;
	String getClientConfiguration(Ovpn resultVpn, Identity identity)  throws IOException;
}
