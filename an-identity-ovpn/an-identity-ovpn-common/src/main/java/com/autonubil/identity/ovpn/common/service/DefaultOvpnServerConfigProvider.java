package com.autonubil.identity.ovpn.common.service;

import org.springframework.stereotype.Service;

import com.autonubil.identity.ovpn.api.OvpnServerConfigService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class DefaultOvpnServerConfigProvider implements OvpnServerConfigService {

	@Override
	public String getName() {
		return "default";
	}

	@Override
	public String getDisplayName() {
		return "Default";
	}

	@Override
	public String getDescription() {
		return "Default Server Config Provider";
	}

	@Override
	public JsonNode getConfigruation() {
		return null;
	}

	@Override
	public void setConfigruation(JsonNode configuration) {
		
	}

}
