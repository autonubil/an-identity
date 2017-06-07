package com.autonubil.identity.ovpn.api;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface OvpnServerConfigService {
	String getId();
	String getClassName();
	String getDisplayName();
	String getDescription();
	
	JsonNode getConfigruation();
	void setConfigruation(JsonNode  configuration) throws JsonProcessingException;
	String getServerConfiguration(Ovpn ovpn, Identity i);
	void setIfConfigInfo(String local, String localNetmask, String remote, String remoteNetmask);
}
