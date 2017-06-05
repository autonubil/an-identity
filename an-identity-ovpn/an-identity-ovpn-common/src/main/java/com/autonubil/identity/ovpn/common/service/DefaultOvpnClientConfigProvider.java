package com.autonubil.identity.ovpn.common.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.common.Renderer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DefaultOvpnClientConfigProvider implements OvpnClientConfigService	 {

	private DefaultConfiguration configuration = new DefaultConfiguration();

	
	@Override
	public String getClassName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String getDisplayName() {
		return "Default Client Config";
	}

	@Override
	public String getDescription() {
		return "Default Server Config Provider";
	}
 
	
	@Override
	public JsonNode getConfigruation() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(this.configuration);
	}

	@Override
	public void setConfigruation(JsonNode configuration) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		this.configuration =   mapper.treeToValue(configuration, DefaultConfiguration.class);
		
	}

	@Override
	public String getClientConfiguration(Ovpn resultVpn, Identity identity) throws IOException {
		Map<String, Object> params = Renderer.ovpnConfigToParamMap(this.configuration.getOvpnOptions());
		

		return  Renderer.renderClientConfig(params);
	}

	@Override
	public String getId() {
		return "cd59bc1a-5ec7-4034-beb3-fff0d2423e81";
	}

}
