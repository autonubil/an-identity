package com.autonubil.identity.auth.api;

import com.autonubil.identity.auth.api.entities.Identity;

public interface AuthenticationListener {

	
	public void userLogin(Credentials c, Identity i);
	
}
