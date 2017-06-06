package com.autonubil.identity.ovpn.vault.entities;

import com.autonubil.identity.ovpn.api.entities.OvpnOptions;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)

public class VaultConfiguration {
	private String vaultAddress;
	private String authPath ="approle";
	private String pkiPath ="pki";
	private String authMethod = "approle";
	private String username ="application-intranet";
	private String password;
	
	private String roleId;
	private String secretId;
	
	private String dnPrefix = "C=DE,L=Hamburg,ST=Hamburg,O=autonubil System GmbH,OU=Development";
	
	
	private OvpnOptions ovpnOptions = new OvpnOptions();
	
	public VaultConfiguration () {
		
	}
	
	public String getVaultAddress() {
		return vaultAddress;
	}
	public void setVaultAddress(String vaultAddress) {
		this.vaultAddress = vaultAddress;
	}
	public String getAuthPath() {
		return authPath;
	}
	public void setAuthPath(String autPath) {
		this.authPath = autPath;
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
	public String getAuthMethod() {
		return authMethod;
	}
	public void setAuthMethod(String autMethod) {
		this.authMethod = autMethod;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getPkiPath() {
		return pkiPath;
	}

	public void setPkiPath(String pkiPath) {
		this.pkiPath = pkiPath;
	}

	public String getDnPrefix() {
		return dnPrefix;
	}

	public void setDnPrefix(String dnPrefix) {
		this.dnPrefix = dnPrefix;
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