package com.autonubil.identity.openid.impl.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;

@RestController
@RequestMapping("/")
public class OpenIdConnectController {

	@RequestMapping(value = "/openid/authorization", method = { RequestMethod.GET })
	public void autorize() throws AuthException {
	}
	
	@RequestMapping(value = "/openid/revocation", method = { RequestMethod.GET })
	public void revoke() throws AuthException {
	}
	
	@RequestMapping(value = "/openid/token", method = { RequestMethod.GET })
	public void getToken() throws AuthException {
	}

	@RequestMapping(value = "/openid/jwks", method = { RequestMethod.GET })
	public void getJwks() throws AuthException {
	}

	@RequestMapping(value = "/.well-known/openid-configuration", method = { RequestMethod.GET })
	public void getConfiguration() throws AuthException {
	}

}
