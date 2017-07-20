package com.autonubil.identity.openid.impl.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.autonubil.identity.apps.api.entities.App;
import com.autonubil.identity.apps.api.service.AppsService;
import com.autonubil.identity.auth.api.entities.ExpiringUser;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.services.AuthService;
import com.autonubil.identity.auth.api.util.IdentityHolder;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.openid.impl.entities.OAuthApp;
import com.autonubil.identity.openid.impl.entities.OAuthApprovalRequest;
import com.autonubil.identity.openid.impl.entities.OAuthAuthorizationErrorResponse;
import com.autonubil.identity.openid.impl.entities.OAuthSession;
import com.autonubil.identity.openid.impl.entities.OAuthToken;
import com.autonubil.identity.openid.impl.entities.OpenIdConnectConfiguration;
import com.autonubil.identity.openid.impl.entities.WebfingerResponse;
import com.autonubil.identity.openid.impl.services.OAuth2ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/")
public class OpenIdConnectController {

	private static Log log = LogFactory.getLog(OpenIdConnectController.class);

	@Autowired
	private OAuth2ServiceImpl oauthService;

	@Autowired
	private AuthService authService;

	@Autowired
	private AppsService appsService;

	// OAUTH2

	@RequestMapping(value = { "/.well-known/webfinger" }, method = { RequestMethod.GET })
	public WebfingerResponse webfinger(HttpServletRequest request,
			@RequestParam(name = "resource", required = true) String ressource,
			@RequestParam(name = "rel", required = true) String rel) throws AuthException {
		OpenIdConnectConfiguration configuration = new OpenIdConnectConfiguration(request);

		if ("http://openid.net/specs/connect/1.0/issuer".equals(rel)) {
			log.debug("Webfinger - Issuer request from: " + request.getRemoteHost());
			return new WebfingerResponse(ressource, rel, configuration.getIssuer());
		} else {
			log.warn("Unknown webfinger request " + rel + " / " + ressource);
		}

		return null;
	}

	@RequestMapping(value = { "/.well-known/openid-configuration" }, method = { RequestMethod.GET })
	public OpenIdConnectConfiguration getConfiguration(HttpServletRequest request) throws AuthException {
		log.debug("Configuration request from: " + request.getRemoteHost());
		OpenIdConnectConfiguration configuration = new OpenIdConnectConfiguration(request);
		return configuration;
	}

	@RequestMapping(value = { "/.well-known/jwks.json" }, method = { RequestMethod.GET })
	public ObjectNode getJwks(HttpServletRequest request) {
		log.debug("JWKS request from: " + request.getRemoteHost());
		return this.oauthService.getJwks();

	}
	// http://tutorials.jenkov.com/oauth2/authorization-code-request-response.html

	// this one only rediretcs to the Ui
	@RequestMapping(value = { "/oauth/authorize" }, method = { RequestMethod.GET })
	public OAuthAuthorizationErrorResponse oauthAuthorize(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "client_id") String clientId, @RequestParam(name = "redirect_uri") String redirectUrl,
			@RequestParam(name = "response_type", required = false) String responseType,
			@RequestParam(name = "scope", required = false) String scope,
			@RequestParam(name = "nonce", required = false) String nonce,
			@RequestParam(name = "state", required = false) String state,
			@PathVariable(name = "appname", required = false) String appname)
			throws AuthException, IOException, URISyntaxException {

		// default to code
		if (responseType == null) {
			responseType = "code";
		}

		// angular does not like the fragment after the query params, so build it
		// manually...
		String newTarget = new OpenIdConnectConfiguration(request).getIssuer();

		if ((redirectUrl == null) || (redirectUrl.length() == 0)) {
			OAuthApp app = oauthService.getApplication(clientId);
			if (app == null) {
				return new OAuthAuthorizationErrorResponse("invalid_request", "Unknown Client", state);
			}
			redirectUrl = app.getCallbackUrl();
		}

		UriComponentsBuilder queryBuilder = UriComponentsBuilder.fromPath("/oauth/authorize")
				.queryParam("redirect_uri", redirectUrl).queryParam("client_id", clientId)
				.queryParam("response_type", responseType);

		if (nonce != null) {
			queryBuilder.queryParam("nonce", nonce);
		}

		if (scope != null) {
			queryBuilder.queryParam("scope", scope);
		}

		if (state != null) {
			queryBuilder.queryParam("state", state);
		}

		String fragmentWithQueryParams = queryBuilder.toUriString();
		String fullPath = UriComponentsBuilder.fromPath("/").fragment(fragmentWithQueryParams).toUriString();

		response.sendRedirect(newTarget + fullPath);

		log.debug("Authorizeation request from: " + request.getRemoteHost());
		return null;
	}

	// called from the ui
	@RequestMapping(value = { "/oauth/approve" }, method = { RequestMethod.GET })
	public Object oauthApprove(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("client_id") String clientId, @RequestParam(name = "scope", required = false) String scope,
			@RequestParam(name = "response_type", required = false) String responseType,
			@RequestParam(name = "nonce", required = false) String nonce,
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state", required = false) String state)
			throws AuthException, IOException, URISyntaxException {

		log.debug("approve request from: " + request.getRemoteHost());

		// https://www.docusign.com/p/RESTAPIGuide/Content/OAuth2/OAuth2%20Response%20Codes.htm
		if (!"code".equals(responseType)) {
			return new OAuthAuthorizationErrorResponse("invalid_request", "Only code response type is supported",
					state);
		}
		OAuthApp app = oauthService.getApplication(clientId);

		List<String> scopes;
		if (scope != null) {
			scope = scope.replace("%20", " ");
			if (scope.startsWith("[")) {
				scope = scope.substring(1, scope.length() - 2).replace("%20", " ");
				scopes = Arrays.asList(scope.split(" "));
			} else {
				scopes = Arrays.asList(scope.split(" "));
			}
		} else {
			scopes = new ArrayList<>();
		}

		for (String requestedScope : scopes) {
			if (!app.getScopes().contains(requestedScope)) {
				return new OAuthAuthorizationErrorResponse("invalid_scope",
						String.format("The scope %s is not permitted", requestedScope), state);
			}
		}
		
	

		Identity i = IdentityHolder.get();
		boolean authenticated = (i != null);
		if (authenticated) {

			// linked app allowed
			if ((app.getLinkedAppId() != null) && (app.getLinkedAppId().length() > 0)) {
				List<App> linkedApps = appsService.listAppsForGroups(i.getUser().getGroups(), app.getLinkedAppId());
				boolean allowed = false;
				for (App userApp : linkedApps) {
					if (userApp.getId().equals(app.getLinkedAppId())) {
						allowed = true;
						break;
					}
				}

				if (!allowed) {
					authenticated = false;
				}
			}

			if (authenticated) {
				// group permissions match?
				List<OAuthApp> userApps = oauthService.listOAuthAppsForGroups(i.getUser().getGroups(), app.getId());
				if (userApps.size() > 0) {
					boolean allowed = false;
					for (OAuthApp userApp : userApps) {
						if (userApp.getId().equals(userApp.getId())) {
							allowed = true;
							break;
						}
					}

					if (!allowed) {
						authenticated = false;
					}
				}
			}

		}
		OAuthSession session = null;
		if (authenticated) {
			session = this.oauthService.addApproval(clientId, code, state, nonce, app, scopes, i.getUser(), scopes.contains("offline_access"));
		} else {
			session = this.oauthService.addApproval(clientId, code, state, nonce, app, scopes, null, scopes.contains("offline_access"));
		}
		if (session == null) {
			// https://www.docusign.com/p/RESTAPIGuide/Content/OAuth2/OAuth2%20Response%20Codes.htm
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_grant", "Invalid session", null);
		}

		log.info("approve request from: " + request.getRemoteHost() + " for " + app.getName());

		return new OAuthApprovalRequest(session, authenticated);

	}

	@RequestMapping(value = { "/oauth/token" }, method = { RequestMethod.POST })
	public Object oauthToken(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam(name = "code") String code, @RequestParam(name = "grant_type") String grantType,
			@RequestParam(name = "redirect_uri") String redirectUrl,
			@RequestParam(name = "client_id", required = false) String clientId,
			@RequestParam(name = "refresh_token", required = false) String refreshToken,
			@RequestParam(name = "client_secret", required = false) String clientSecret)

			throws AuthException, IllegalArgumentException, UnsupportedEncodingException {

		OAuthSession session = this.oauthService.getApproval(code);

			
		if (session == null) {
			// https://www.docusign.com/p/RESTAPIGuide/Content/OAuth2/OAuth2%20Response%20Codes.htm
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_grant", "Invalid session", null);
		}

		// check if user is still valid
		User user = session.getUser(this.authService);

		if (user == null) {
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_grant", "No user associated", null);
		}

		if (user instanceof ExpiringUser) {
			ExpiringUser expiringUser = (ExpiringUser) user;
			if (expiringUser.isExpired()) {
				response.setStatus(403);
				return new OAuthAuthorizationErrorResponse("invalid_grant", "User is expired", null);
			}
			// token will not be longertvalid as the user itself
			if (expiringUser.getUserExpires().getTime() < session.getExpires().getTime()) {
				session.setExpires(expiringUser.getUserExpires());

			}
		}
		
		
		
		if ("authorization_code".equals(grantType)) {
			response.setContentType("application/jwt");
			OAuthToken token = oauthService.getToken(session, this.getConfiguration(request).getIssuer(), user.getUsername());
			log.info("token request from: " + request.getRemoteHost() + " for " + session.getApplication().getName());
			return token;
		} else if ("refresh_token".equals(grantType)) {
			response.setContentType("application/jwt");
			session.upgrade();
			OAuthToken token = oauthService.getToken(session, this.getConfiguration(request).getIssuer(), user.getUsername());
			log.info("token request from: " + request.getRemoteHost() + " for " + session.getApplication().getName());
			return token;	
		} else {
			log.info(grantType + " request from: " + request.getRemoteHost() + " for " + session.getApplication().getName());
			return new OAuthAuthorizationErrorResponse("invalid_grant", "Unsupported grant type", null);
		}


		
	}

	@RequestMapping(value = { "/oauth/tokeninfo", "/oauth/token-info" }, method = { RequestMethod.GET })
	public Object oauthTokeninfo(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "Authorization") String authorization,
			@RequestParam(name = "access_token", required = false) String accessToken)
			throws IllegalArgumentException, UnsupportedEncodingException, AuthException {

		OAuthSession session = null;
		if ((authorization != null) && (authorization.startsWith("Bearer "))) {
			String tokenHash = authorization.substring(7);
			session = this.oauthService.getSession(tokenHash);
		} else if (accessToken != null) {
			session = this.oauthService.getSession(accessToken);
		}

		if (session == null) {
			// https://www.docusign.com/p/RESTAPIGuide/Content/OAuth2/OAuth2%20Response%20Codes.htm
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_token", "The Access Token expired", null);
		}


		// check if user is still valid
		User user = session.getUser(this.authService);

		if (user == null) {
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_grant", "No user associated", null);
		}
		
		log.info("tokeninfo request from: " + request.getRemoteHost() + " for " + session.getClientId());
		
		response.setContentType("application/jwt");
		return oauthService.getToken(session, this.getConfiguration(request).getIssuer(), user.getUsername());
		
	}

	@RequestMapping(value = { "/oauth/profile" }, method = { RequestMethod.GET })
	public Object oauthProfile(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "Authorization") String authorization,
			@RequestParam(name = "access_token", required = false) String accessToken)
			throws IllegalArgumentException, UnsupportedEncodingException {

		OAuthSession session = null;
		if ((authorization != null) && (authorization.startsWith("Bearer "))) {
			String tokenHash = authorization.substring(7);
			session = this.oauthService.getSession(tokenHash);
		} else if (accessToken != null) {
			session = this.oauthService.getSession(accessToken);
		}

		if (session == null) {
			// https://www.docusign.com/p/RESTAPIGuide/Content/OAuth2/OAuth2%20Response%20Codes.htm
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_token", "The Access Token expired", null);
		}

		// check if user is still valid
		User user = session.getUser(this.authService);
		if (user instanceof ExpiringUser) {
			ExpiringUser expiringUser = (ExpiringUser) user;
			if (expiringUser.isExpired()) {
				response.setStatus(403);
				return new OAuthAuthorizationErrorResponse("invalid_grant", "User is expired", null);
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode profile = mapper.createObjectNode();

		profile.put("iss", new OpenIdConnectConfiguration(request).getIssuer());
		profile.put("aud", session.getClientId());
		profile.put("sub", user.getUsername());
		// profile.put("iat", new Date().getTime());

		if (session.getNonce() != null) {
			profile.put("nonce", session.getNonce());
		}

		LdapUser ldapUser = null;
		if (user instanceof LdapUser) {
			ldapUser = (LdapUser) user;
		}
		
		

		if (session.getScopes().contains("profile")) {
			// name, family_name, given_name, middle_name, nickname, preferred_username,
			// profile, picture, website, gender, birthdate, zoneinfo, locale, and
			// updated_at.
			profile.put("name", user.getDisplayName());
			profile.put("nickname", user.getUsername());
			if (ldapUser != null) {
				if (ldapUser.getSn() != null) {
					profile.put("family_name", ldapUser.getSn());
				}
				if (ldapUser.getGivenName() != null) {
					profile.put("given_name", ldapUser.getGivenName());
				}
			}
		}
		if (session.getScopes().contains("email")) {
			if (ldapUser != null) {
				if (ldapUser.getMail() != null) {
					profile.put("email", ldapUser.getMail());
					profile.put("email_verified", true);
				}
			}
		}
		if (session.getScopes().contains("phone")) {
			if (ldapUser != null) {
				if (ldapUser.getPhone() != null) {
					profile.put("phone", ldapUser.getPhone());
				} else if (ldapUser.getMobilePhone() != null) {
					profile.put("phone", ldapUser.getMobilePhone());
				}
			}
		}

		response.setContentType("application/json");
		
		log.info("profile request from: " + request.getRemoteHost() + " for " + session.getClientId());
		
		
		return profile.toString();
	}


	@RequestMapping(value = { "/oauth/userinfo", "/api/v3/userinfo" }, method = { RequestMethod.GET })
	public Object getUserInfo(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "Authorization") String authorization,
			@RequestParam(name = "access_token", required = false) String accessToken)
			throws IllegalArgumentException, UnsupportedEncodingException {

		OAuthSession session = null;
		if ((authorization != null) && (authorization.startsWith("Bearer "))) {
			String tokenHash = authorization.substring(7);
			session = this.oauthService.getSession(tokenHash);
		}else if (accessToken != null) {
			session = this.oauthService.getSession(accessToken);
		}

		if (session == null) {
			// https://www.docusign.com/p/RESTAPIGuide/Content/OAuth2/OAuth2%20Response%20Codes.htm
			response.setStatus(401);
			return new OAuthAuthorizationErrorResponse("invalid_token", "The Access Token expired", null);
		}

		// check if user is still valid
		User user = session.getUser(this.authService);
		if (user instanceof ExpiringUser) {
			ExpiringUser expiringUser = (ExpiringUser) user;
			if (expiringUser.isExpired()) {
				response.setStatus(403);
				return new OAuthAuthorizationErrorResponse("invalid_grant", "User is expired", null);
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode profile = mapper.createObjectNode();

		profile.put("iss", new OpenIdConnectConfiguration(request).getIssuer());
		profile.put("aud", session.getClientId());
		profile.put("id", user.getId());

		LdapUser ldapUser = null;
		if (user instanceof LdapUser) {
			ldapUser = (LdapUser) user;
		}

		if (session.getScopes().contains("profile")) {
			// name, family_name, given_name, middle_name, nickname, preferred_username,
			// profile, picture, website, gender, birthdate, zoneinfo, locale, and
			// updated_at.
			profile.put("name", user.getDisplayName());
			profile.put("nickname", user.getUsername());
			if (ldapUser != null) {
				if (ldapUser.getSn() != null) {
					profile.put("first_name", ldapUser.getSn());
				}
				if (ldapUser.getGivenName() != null) {
					profile.put("last_name", ldapUser.getGivenName());
				}
			}
		}
		if (session.getScopes().contains("email")) {
			if (user instanceof LdapUser) {
				if (ldapUser.getMail() != null) {
					profile.put("email", ldapUser.getMail());
				}
			}
		}
		if (session.getScopes().contains("phone")) {
			if (user instanceof LdapUser) {
				if (ldapUser.getPhone() != null) {
					profile.put("phone", ldapUser.getPhone());
				} else if (ldapUser.getMobilePhone() != null) {
					profile.put("phone", ldapUser.getMobilePhone());
				}
			}
		}

		log.info("userinfo request from: " + request.getRemoteHost() + " for " + session.getClientId());
		
		response.setContentType("application/json");
		return profile.toString();
	}
}

/*
 * gitlab (from source:
 * https://docs.gitlab.com/ce/integration/omniauth.html#using-custom-omniauth-
 * providers) /opt/gitlab/embedded/service/gitlab-rails/Gemfile gem
 * 'omniauth-openid-connect', '~> 0.2.0' sudo -u git -H bundle install --without
 * development test mysql --path vendor/bundle --no-deployment
 * 
 * 
 * omnibus: sudo -u git -H /opt/gitlab/embedded/bin/gem install
 * omniauth-openid-connect chmod o+w
 * /opt/gitlab/embedded/service/gitlab-rails/Gemfile.lock
 * 
 * /opt/gitlab/embedded/lib/ruby/gems/2.3.0/gems/omniauth-openid-connect-0.2.3/
 * lib/omniauth/strategies/openid_connect.rb -> when :HS256, :HS384, :HS512,
 * "HS256", "HS384", "HS512" -> when :RS256, :RS384, :RS512, "RS256", "RS384",
 * "RS512"
 * 
 * /opt/gitlab/embedded/lib/ruby/gems/2.3.0/gems/openid_connect-0.9.2/lib/
 * openid_connect/response_object/id_token.rb exp.to_i > Time.now.utc.to_i &&
 * 
 * 
 * 
 */
