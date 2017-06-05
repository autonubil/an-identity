package com.autonubil.identity.ovpn.vault.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.common.Renderer;
import com.autonubil.identity.ovpn.vault.entities.VaultConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class VaultOvpnConfigService implements OvpnClientConfigService {

	@Autowired
	private OvpnConfigService ovpnConfigService;

	
	private VaultConfiguration configuration = new VaultConfiguration();
	
	@Override
	public String getClassName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String getDisplayName() {
		return "Hashicorp Vault Secret Backend";
	}

	@Override
	public String getDescription() {
		return "Uess vault to issue Client Certificates";
	}

	@Override
	public JsonNode getConfigruation() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(this.configuration);
	}

	@Override
	public void setConfigruation(JsonNode configuration) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		this.configuration =   mapper.treeToValue(configuration, VaultConfiguration.class);
		
	}

	@Override
	public String getClientConfiguration(Ovpn resultVpn, Identity identity) throws IOException {
		Map<String, Object> params = Renderer.ovpnConfigToParamMap(this.configuration.getOvpnOptions());
		

		return  Renderer.renderClientConfig(params);
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "65425941-7eaa-445a-a602-4cb385e2db57";
	}
}
