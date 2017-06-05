package com.autonubil.identity.ovpn.vault.entities;

import com.autonubil.identity.ovpn.api.entities.OvpnOptions;

public class VaultConfiguration {
	private String vaultAddress;
	private String autPath;
	private String username;
	private OvpnOptions ovpnOptions;
	
	public String getVaultAddress() {
		return vaultAddress;
	}
	public void setVaultAddress(String vaultAddress) {
		this.vaultAddress = vaultAddress;
	}
	public String getAutPath() {
		return autPath;
	}
	public void setAutPath(String autPath) {
		this.autPath = autPath;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
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