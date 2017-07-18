package com.autonubil.identity.aws.federation.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.aws.federation.service.AwsConsoleFederationProxy;
import com.autonubil.identity.openid.impl.entities.OpenIdConnectConfiguration;


@RestController
@RequestMapping("/aws")
@PropertySource("aws.federation.properties")
@ConditionalOnProperty(name="aws.federation.enabled", havingValue="true", matchIfMissing = true)
public class AwsConsoleFederationController {
	
	@Autowired
	AwsConsoleFederationProxy authProxy;

	@RequestMapping(value = "/console/{application}", method = RequestMethod.GET)
	public void openAwsConsole(HttpServletResponse response, HttpServletRequest request) throws AuthException {
		AuthUtils.checkLoggedIn();
		
		Identity identity = IdentityHolder.get();
		try {
			OpenIdConnectConfiguration configuration = new OpenIdConnectConfiguration(request);
			response.sendRedirect(authProxy.getConsoleAccessUrl(configuration.getIssuer(), identity));
		}catch (Exception e) {
			throw new AuthException("Failed to get federation URL", e);
		}
	}
	
	
	
}

