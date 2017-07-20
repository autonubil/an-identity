package com.autonubil.identity.ovpn.api;

import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface OvpnSessionConfigService {
	String getId();
	String getClassName();
	String getDisplayName();
	String getDescription();
	
	JsonNode getConfigruation();
	void setConfigruation(JsonNode  configuration) throws JsonProcessingException;
	String getSessionConfiguration(Ovpn ovpn, User user);
	void setIfConfigInfo(String local, String localNetmask, String remote, String remoteNetmask);
}
