/*
package org.springframework.vault.authentication;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

public class LdapAuthentication implements ClientAuthentication {
	private static final Log logger = LogFactory.getLog(LdapAuthentication.class);
	
	private final LdapAuthenticationOptions options;

	private final RestOperations restOperations;

	/**
	 * Create a {@link LdapAuthentication} using {@link LdapAuthenticationOptions} and
	 * {@link RestOperations}.
	 *
	 * @param options must not be {@literal null}.
	 * @param restOperations must not be {@literal null}.
	 * /
	public LdapAuthentication(LdapAuthenticationOptions options,
			RestOperations restOperations) {

		Assert.notNull(options, "LdapAuthenticationOptions must not be null");
		Assert.notNull(restOperations, "RestOperations must not be null");

		this.options = options;
		this.restOperations = restOperations;
	}

	@Override
	public VaultToken login() {
		return createTokenUsingLdap();
	}

	private VaultToken createTokenUsingLdap() {

		Map<String, String> login = getLdapLogin(options.getUsername());

		try {
			VaultResponse response = restOperations.postForObject("/auth/{mount}/login",
					login, VaultResponse.class, options.getPath());

			logger.debug("Login successful using Ldap authentication");

			return LoginTokenUtil.from(response.getAuth());
		}
		catch (HttpStatusCodeException e) {
			throw new VaultException(String.format("Cannot login using ldap: %s",
					VaultResponses.getError(e.getResponseBodyAsString())));
		}
	}

	private Map<String, String> getLdapLogin(String username) {

		Map<String, String> login = new HashMap<String, String>();
		login.put("username", username);
		return login;
}
}
*/