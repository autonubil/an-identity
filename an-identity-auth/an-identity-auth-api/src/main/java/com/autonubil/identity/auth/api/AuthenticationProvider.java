package com.autonubil.identity.auth.api;

import java.util.List;

import com.autonubil.identity.auth.api.entities.AuthenticationSource;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.PasswordReset;

public interface AuthenticationProvider {

	public List<AuthenticationSource> getSources();
	
	public boolean supportsCredentials(Credentials c);
	public boolean supportsReset(PasswordReset pwr);
	
	public User authenticate(Credentials c) throws AuthException, Exception;
	public boolean reset(PasswordReset pwr) throws AuthException;
	
	public List<User> getLinked(User u) throws Exception;

}
