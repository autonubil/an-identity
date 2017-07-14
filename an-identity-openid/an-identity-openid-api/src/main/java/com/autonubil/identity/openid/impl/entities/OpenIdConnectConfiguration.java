package com.autonubil.identity.openid.impl.entities;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenIdConnectConfiguration {
	private String issuer = "an-identity";
	
	@JsonProperty("authorization_endpoint")
	private String authorizationEndpoint;
	
	@JsonProperty("token_endpoint")
	private String tokenEndpoint;
	
	@JsonProperty("token_endpoint_auth_methods_supported")
	private List<String>  tokenEndpointAuthMethodsSupported = Arrays.asList("client_secret_basic");
	
	@JsonProperty("token_endpoint_auth_signing_alg_values_supported")
	private List<String>  tokenEndpointAuthAlgValuesSupported= Arrays.asList("RS256", "ES256");
	
	@JsonProperty("scopes_supported")
	private List<String>  scopesSupported= Arrays.asList("openid", "profile", "email", "address", "phone", "offline_access");
	
	@JsonProperty("response_types_supported")
	private List<String>  responseTypesSupported= Arrays.asList("code", "code id_token", "id_token", "token id_token");

	public OpenIdConnectConfiguration() {
		
	}
	public OpenIdConnectConfiguration(String issuer) {
		this.setIssuer(issuer);
		this.setTokenEndpoint(issuer +"/oauth/token");
		this.setAuthorizationEndpoint(issuer +"/oauth/authorize");

	}
	
	public OpenIdConnectConfiguration(HttpServletRequest request) {
		String issuer = "http://localhost"; 
		try {
			URI baseTarget;
				baseTarget = new URI(request.getScheme(), null, request.getRemoteHost(), request.getLocalPort(), null, null, null);
			issuer =baseTarget.toString(); 
		} catch (URISyntaxException e) {
		}
		this.setIssuer(issuer);
		this.setTokenEndpoint(issuer +"/oauth/token");
		this.setAuthorizationEndpoint(issuer +"/oauth/authorize");

	}
	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	public List<String> getTokenEndpointAuthMethodsSupported() {
		return tokenEndpointAuthMethodsSupported;
	}

	public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
		this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
	}

	public List<String> getTokenEndpointAuthAlgValuesSupported() {
		return tokenEndpointAuthAlgValuesSupported;
	}

	public void setTokenEndpointAuthAlgValuesSupported(List<String> tokenEndpointAuthAlgValuesSupported) {
		this.tokenEndpointAuthAlgValuesSupported = tokenEndpointAuthAlgValuesSupported;
	}

	public List<String> getScopesSupported() {
		return scopesSupported;
	}

	public void setScopesSupported(List<String> scopesSupported) {
		this.scopesSupported = scopesSupported;
	}

	public List<String> getResponseTypesSupported() {
		return responseTypesSupported;
	}

	public void setResponseTypesSupported(List<String> responseTypesSupported) {
		this.responseTypesSupported = responseTypesSupported;
	}
	
	
	
	/*
	{
		"userinfo_endpoint": "https://server.example.com/connect/userinfo",
		"check_session_iframe": "https://server.example.com/connect/check_session",
		"end_session_endpoint": "https://server.example.com/connect/end_session",
		"jwks_uri": "https://server.example.com/jwks.json",
		"registration_endpoint": "https://server.example.com/connect/register",
		"acr_values_supported": ["urn:mace:incommon:iap:silver",
		"urn:mace:incommon:iap:bronze"],
		"subject_types_supported": ["public", "pairwise"],
		"userinfo_signing_alg_values_supported": ["RS256",		"ES256", 		"HS256"],
		"userinfo_encryption_alg_values_supported": ["RSA1_5", 		"A128KW"],
         "userinfo_encryption_enc_values_supported": ["A128CBC-HS256", 		"A128GCM"],
		"id_token_signing_alg_values_supported": ["RS256",	"ES256",		"HS256"],
		"id_token_encryption_alg_values_supported": ["RSA1_5", 		"A128KW"],
		"id_token_encryption_enc_values_supported": ["A128CBC-HS256", "A128GCM"],
		"request_object_signing_alg_values_supported": ["none", "RS256", "ES256"],
		"display_values_supported": ["page", "popup"],
		"claim_types_supported": ["normal", "distributed"],
		"claims_supported": ["sub", "iss", "auth_time", "acr", "name", "given_name", "family_name", "nickname", "profile", "picture", "website", "email",
		"email_verified",
		"locale",
		"zoneinfo",
		"http://example.info/claims/groups"],
		"claims_parameter_supported": true,
		"service_documentation": "http://server.example.com/connect/service_documentation.html",
		"ui_locales_supported": ["en-US", "en-GB", "en-CA", "fr-FR", "fr-CA"]
	}
	*/
}
