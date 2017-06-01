package com.autonubil.identity.ovpn.vault.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class VaultConfiguration {
	private String vaultAddress;
	private String autPath;
	private String username;
	private JsonNode options;
	
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
	public JsonNode getOptions() {
		return options;
	}
	public void setOptions(JsonNode options) {
		this.options = options;
	}
	
}
