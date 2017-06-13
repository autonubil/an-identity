package com.autonubil.identity.ovpn.common.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.OvpnServerConfigService;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.StoredCertInfo;
import com.autonubil.identity.ovpn.common.Renderer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DefaultOvpnServerConfigProvider implements OvpnServerConfigService	 {

	private DefaultConfiguration configuration = new DefaultConfiguration();

	
	@Override
	public String getClassName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String getDisplayName() {
		return "Default Server Config";
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
	public String getServerConfiguration(Ovpn resultVpn, Identity identity) throws IOException {
		Map<String, Object> params = Renderer.ovpnConfigToParamMap(this.configuration.getOvpnOptions());
		

		return  Renderer.renderServerConfig(params);
	}

	@Override
	public String getId() {
		return "d3562326-5f64-481d-aeea-d37fa46538aa";
	}

	@Override
	public StoredCertInfo getCurrentCert(Ovpn resultVpn, Identity identity) {
		throw new NotImplementedException();
	}

	@Override
	public void deleteServerConfiguration(Ovpn ovpn, Identity identity) {
		throw new NotImplementedException();
		
	}

}
