package com.autonubil.identity.ovpn.common.service;

import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.ovpn.api.OvpnSessionConfigService;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.api.entities.OvpnPushOption;
import com.autonubil.identity.ovpn.api.entities.OvpnSessionConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DefaultOvpnSessionConfigProvider implements OvpnSessionConfigService {

	private OvpnSessionConfig configuration;
	private String local; 
	private String localNetmask;
	private String remote; 
	private String remoteNetmask;

	@Override
	public String getClassName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String getDisplayName() {
		return "Default Session Config";
	}

	@Override
	public String getDescription() {
		return "Default Session Config Provider";
	}

	@Override
	public JsonNode getConfigruation() {
		return null;
	}

	@Override
	public void setConfigruation(JsonNode configuration) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configuration = mapper.treeToValue(configuration, OvpnSessionConfig.class);

	}
	
	
	@Override
	public String getId() {
		return "a025b0c8-bb69-456f-855c-3ae5e46601d5";
	}

	@Override
	public String getSessionConfiguration(Ovpn ovpn,  User user) {
		StringBuilder sb = new StringBuilder();
		
		boolean routeSet = false;
		boolean topology = false;
		
		
		if (this.configuration.getPush() != null) {
			for (OvpnPushOption pushOption : this.configuration.getPush()) {
				if (pushOption.getValue().equals("topology")) {
					topology = true;
				}
				if (pushOption.getValue().equals("route-gateway")) {
					routeSet = true;
				}
			}
			
			if (!routeSet) {
				this.configuration.getPush().add(0, new OvpnPushOption("route-gateway", this.getLocal() ));
			}
 
			if (!topology) {
				this.configuration.getPush().add(0, new OvpnPushOption("topology", "subnet" ));
			}
		}
		
		
		
		sb.append(this.configuration.toString());
		return sb.toString();
	}

	 

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getLocalNetmask() {
		return localNetmask;
	}

	public void setLocalNetmask(String localNetmask) {
		this.localNetmask = localNetmask;
	}

	public String getRemote() {
		return remote;
	}

	public void setRemote(String remote) {
		this.remote = remote;
	}

	public String getRemoteNetmask() {
		return remoteNetmask;
	}

	public void setRemoteNetmask(String remoteNetmask) {
		this.remoteNetmask = remoteNetmask;
	}

	@Override
	public void setIfConfigInfo(String local, String localNetmask, String remote, String remoteNetmask) {
		this.local = local;
		this.localNetmask = localNetmask;
		this.remote = remote;
		this.remoteNetmask = remoteNetmask;
	}



}
