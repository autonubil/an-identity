package com.autonubil.identity.ovpn.common.service;

import com.autonubil.identity.ovpn.api.entities.OvpnOptions;

public class DefaultConfiguration {
	private OvpnOptions ovpnOptions;
	
	public OvpnOptions getOvpnOptions() {
		return ovpnOptions;
	}
	public void setOvpnOptions(OvpnOptions ovpnOptions) {
		this.ovpnOptions = ovpnOptions;
	}
	
}

/*

{
"vaultAddress": "http://vault.veb.local:8200",
"authPath" :"ipa",
"username" : "application-intranet".
"password" : "secret"
}

*/