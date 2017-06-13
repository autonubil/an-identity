package com.autonubil.identity.ovpn.api;

import java.io.IOException;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.StoredCertInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface OvpnServerConfigService {
	String getId();
	String getClassName();
	String getDisplayName();
	String getDescription();
	
	JsonNode getConfigruation();
	
	void setConfigruation(JsonNode  configuration) throws JsonProcessingException;
	String getServerConfiguration(Ovpn resultVpn, Identity identity)  throws IOException;
	void deleteServerConfiguration(Ovpn ovpn, Identity identity) throws IOException;
	StoredCertInfo getCurrentCert(Ovpn resultVpn, Identity identity);
}
