package com.autonubil.identity.secrets.api;

public interface SecretsProvider {
	
	String getId();
	String getName();	
	String getDescription();
	String getSecret(String key);
	void setSecret(String key, String secret);
	void delteSecret(String key);
}

