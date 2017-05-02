package com.autonubil.identity.auth.api;

import com.autonubil.identity.auth.api.entities.Identity;

public interface IdentityStore {

	
	public Identity getIdentity(String token);
	public void removeIdentity(String token);
	public String putIdentity(Identity identity);
	public void update(String value, Identity identity);
	
	
	
}
