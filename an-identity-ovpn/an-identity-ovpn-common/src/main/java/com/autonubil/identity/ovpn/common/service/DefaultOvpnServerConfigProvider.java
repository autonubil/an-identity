package com.autonubil.identity.ovpn.common.service;

import org.springframework.stereotype.Service;

import com.autonubil.identity.ovpn.api.OvpnServerConfigService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class DefaultOvpnServerConfigProvider implements OvpnServerConfigService {

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
		return null;
	}

	@Override
	public void setConfigruation(JsonNode configuration) {
		
	}
	
	
	@Override
	public String getId() {
		return "a025b0c8-bb69-456f-855c-3ae5e46601d5";
	}


}
