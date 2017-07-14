package com.autonubil.identity.auth.api.services;

import java.util.List;

import com.autonubil.identity.auth.api.Credentials;
import com.autonubil.identity.auth.api.entities.AuthenticationSource;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.PasswordReset;

public interface AuthService {

	boolean reset(PasswordReset pwr) throws AuthException;

	Identity authenticate(Credentials c) throws AuthException;

	Identity authenticate(Credentials c, boolean getLinked) throws AuthException;

	List<AuthenticationSource> getSources();

	User getUser(String sourceId, String username);

}