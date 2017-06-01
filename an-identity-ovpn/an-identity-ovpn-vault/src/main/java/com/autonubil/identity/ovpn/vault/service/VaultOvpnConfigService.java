package com.autonubil.identity.ovpn.vault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.vault.entities.VaultConfiguration;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class VaultOvpnConfigService implements OvpnClientConfigService {

	@Autowired
	private OvpnConfigService ovpnConfigService;

	
	private VaultConfiguration configuration = new VaultConfiguration();
	
	@Override
	public String getName() {
		return "vault";
	}

	@Override
	public String getDisplayName() {
		return "Vault";
	}

	@Override
	public String getDescription() {
		return "Uess vault to issue Client Certificates and read Client template";
	}

	@Override
	public JsonNode getConfigruation() {
		// return new ObjectMapper();
		return null;
	}

	@Override
	public void setConfigruation(JsonNode configuration) {
		// TODO Auto-generated method stub
		
	}
}
