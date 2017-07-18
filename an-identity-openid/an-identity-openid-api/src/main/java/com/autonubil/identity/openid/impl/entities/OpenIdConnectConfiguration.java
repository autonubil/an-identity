
package com.autonubil.identity.openid.impl.entities;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenIdConnectConfiguration {

	// https://openid.net/specs/openid-connect-discovery-1_0.html

	/**
	 * REQUIRED. URL using the https scheme with no query or fragment component that
	 * the OP asserts as its Issuer Identifier. If Issuer discovery is supported
	 * (see Section 2), this value MUST be identical to the issuer value returned by
	 * WebFinger. This also MUST be identical to the iss Claim value in ID Tokens
	 * issued from this Issuer.
	 */
	private String issuer = "an-identity";

	/**
	 * REQUIRED. URL of the OP's OAuth 2.0 Authorization Endpoint [OpenID.Core].
	 */
	@JsonProperty("authorization_endpoint")
	private String authorizationEndpoint;

	/**
	 * RECOMMENDED. URL of the OP's UserInfo Endpoint [OpenID.Core]. This URL MUST
	 * use the https scheme and MAY contain port, path, and query parameter
	 * components.
	 */
	@JsonProperty("userinfo_endpoint")
	private String userinfoEndpointUri;

	/**
	 * REQUIRED. URL of the OP's JSON Web Key Set [JWK] document. This contains the
	 * signing key(s) the RP uses to validate signatures from the OP. The JWK Set
	 * MAY also contain the Server's encryption key(s), which are used by RPs to
	 * encrypt requests to the Server. When both signing and encryption keys are
	 * made available, a use (Key Use) parameter value is REQUIRED for all keys in
	 * the referenced JWK Set to indicate each key's intended usage. Although some
	 * algorithms allow the same key to be used for both signatures and encryption,
	 * doing so is NOT RECOMMENDED, as it is less secure. The JWK x5c parameter MAY
	 * be used to provide X.509 representations of keys provided. When used, the
	 * bare key values MUST still be present and MUST match those in the
	 * certificate.
	 */
	@JsonProperty("jwks_uri")
	private String jwksUri;

	@JsonProperty("token_endpoint")
	private String tokenEndpoint;

	/**
	 * OPTIONAL. JSON array containing a list of Client Authentication methods
	 * supported by this Token Endpoint. The options are client_secret_post,
	 * client_secret_basic, client_secret_jwt, and private_key_jwt, as described in
	 * Section 9 of OpenID Connect Core 1.0 [OpenID.Core]. Other authentication
	 * methods MAY be defined by extensions. If omitted, the default is
	 * client_secret_basic -- the HTTP Basic Authentication Scheme specified in
	 * Section 2.3.1 of OAuth 2.0 [RFC6749].
	 */
	@JsonProperty("token_endpoint_auth_methods_supported")
	private List<String> tokenEndpointAuthMethodsSupported = Arrays.asList("client_secret_basic");



	@JsonProperty("scopes_supported")
	private List<String> scopesSupported = Arrays.asList("openid", "profile", "email", "address", "phone",
			"offline_access");

	/**
	 * REQUIRED. JSON array containing a list of the OAuth 2.0 response_type values
	 * that this OP supports. Dynamic OpenID Providers MUST support the code,
	 * id_token, and the token id_token Response Type values.
	 */
	@JsonProperty("response_types_supported")
	private List<String> responseTypesSupported = Arrays.asList("code"); // , "code id_token", "id_token", "token
																			// id_token"

	/**
	 * REQUIRED. JSON array containing a list of the Subject Identifier types that
	 * this OP supports. Valid types include pairwise and public.
	 */
	@JsonProperty("subject_types_supported")
	private List<String> subjectTypesSupported = Arrays.asList("public"); // , "pairwise");

	/**
	 * OPTIONAL. JSON array containing a list of the OAuth 2.0 response_mode values
	 * that this OP supports, as specified in OAuth 2.0 Multiple Response Type
	 * Encoding Practices [OAuth.Responses]. If omitted, the default for Dynamic
	 * OpenID Providers is ["query", "fragment"].
	 */
	@JsonProperty("response_modes_supported")
	private List<String> responseModesSupported = Arrays.asList("query", "fragment");

	/**
	 * OPTIONAL. JSON array containing a list of the JWS signing algorithms (alg
	 * values) supported by the Token Endpoint for the signature on the JWT [JWT]
	 * used to authenticate the Client at the Token Endpoint for the private_key_jwt
	 * and client_secret_jwt authentication methods. Servers SHOULD support RS256.
	 * The value none MUST NOT be used.
	 */
	@JsonProperty("token_endpoint_auth_signing_alg_values_supported")
	private List<String> tokenEndpointAuthAlgValuesSupported = Arrays.asList("RS256", "HS256");
	
	/**
	 * REQUIRED. JSON array containing a list of the JWS signing algorithms (alg
	 * values) supported by the OP for the ID Token to encode the Claims in a JWT
	 * [JWT]. The algorithm RS256 MUST be included. The value none MAY be supported,
	 * but MUST NOT be used unless the Response Type used returns no ID Token from
	 * the Authorization Endpoint (such as when using the Authorization Code Flow).
	 */
	@JsonProperty("id_token_signing_alg_values_supported")
	private List<String> idTokenSigningAlgValuesSupported = Arrays.asList("RS256", "HS256");

	/**
	 * OPTIONAL. JSON array containing a list of the Claim Types that the OpenID
	 * Provider supports. These Claim Types are described in Section 5.6 of OpenID
	 * Connect Core 1.0 [OpenID.Core]. Values defined by this specification are
	 * normal, aggregated, and distributed. If omitted, the implementation supports
	 * only normal Claims.
	 */
	@JsonProperty("claim_types_supported")
	private List<String> claimTypesSupported = Arrays.asList("normal");

	@JsonProperty("claims_supported")
	private List<String> claimsSupported = Arrays.asList("iss", "sub", "aud", "exp", "iat", "name", "nickname", "email",
			"profile");

	public OpenIdConnectConfiguration() {

	}

	public OpenIdConnectConfiguration(String issuer) {
		this.setIssuer(issuer);
		initFromBaseUrl(issuer);
	}

	public OpenIdConnectConfiguration(HttpServletRequest request) {
		String issuer = "http://localhost";
		try {
			URI baseTarget;
			// allways HTTPS
			int port = request.getLocalPort();
			boolean isSSL = "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"));
			if (!isSSL) {
				isSSL = "on".equalsIgnoreCase(request.getHeader("SSL"));
			}

			if (request.getHeader("X-Forwarded-For") != null) {
				if (request.getHeader("X-Forwarded-Port") != null) {
					port = Integer.parseInt(request.getHeader("X-Forwarded-Port"));
				} else {
					if (isSSL)
						port = 443;
					else
						port = 80;

				}
			}

			baseTarget = new URI(isSSL ? "https" : "http", null, request.getHeader("Host"), port == 443 || port == 80 ?  -1 : port, null, null, null);
			issuer = baseTarget.toString();
		} catch (URISyntaxException e) {
		}
		this.setIssuer(issuer);
		initFromBaseUrl(issuer);

	}

	private void initFromBaseUrl(String baseUrl) {
		this.setTokenEndpoint(baseUrl + "/oauth/token");
		this.setAuthorizationEndpoint(baseUrl + "/oauth/authorize");
		this.setUserinfoEndpointUri(baseUrl + "/oauth/userinfo");
		this.setJwksUri(baseUrl + "/.well-known/jwks.json");
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

	public String getUserinfoEndpointUri() {
		return userinfoEndpointUri;
	}

	public void setUserinfoEndpointUri(String userinfoEndpointUri) {
		this.userinfoEndpointUri = userinfoEndpointUri;
	}

	public String getJwksUri() {
		return jwksUri;
	}

	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}

	public List<String> getSubjectTypesSupported() {
		return subjectTypesSupported;
	}

	public void setSubjectTypesSupported(List<String> subjectTypesSupported) {
		this.subjectTypesSupported = subjectTypesSupported;
	}

	public List<String> getIdTokenSigningAlgValuesSupported() {
		return idTokenSigningAlgValuesSupported;
	}

	public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
		this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
	}

	/*
	 * { "userinfo_endpoint": "https://server.example.com/connect/userinfo",
	 * "check_session_iframe": "https://server.example.com/connect/check_session",
	 * "end_session_endpoint": "https://server.example.com/connect/end_session",
	 * "jwks_uri": "https://server.example.com/jwks.json", "registration_endpoint":
	 * "https://server.example.com/connect/register", "acr_values_supported":
	 * ["urn:mace:incommon:iap:silver", "urn:mace:incommon:iap:bronze"],
	 * "subject_types_supported": ["public", "pairwise"],
	 * "userinfo_signing_alg_values_supported": ["RS256", "ES256", "HS256"],
	 * "userinfo_encryption_alg_values_supported": ["RSA1_5", "A128KW"],
	 * "userinfo_encryption_enc_values_supported": ["A128CBC-HS256", "A128GCM"],
	 * "id_token_signing_alg_values_supported": ["RS256", "ES256", "HS256"],
	 * "id_token_encryption_alg_values_supported": ["RSA1_5", "A128KW"],
	 * "id_token_encryption_enc_values_supported": ["A128CBC-HS256", "A128GCM"],
	 * "request_object_signing_alg_values_supported": ["none", "RS256", "ES256"],
	 * "display_values_supported": ["page", "popup"], "claim_types_supported":
	 * ["normal", "distributed"], "claims_supported": ["sub", "iss", "auth_time",
	 * "acr", "name", "given_name", "family_name", "nickname", "profile", "picture",
	 * "website", "email", "email_verified", "locale", "zoneinfo",
	 * "http://example.info/claims/groups"], "claims_parameter_supported": true,
	 * "service_documentation":
	 * "http://server.example.com/connect/service_documentation.html",
	 * "ui_locales_supported": ["en-US", "en-GB", "en-CA", "fr-FR", "fr-CA"] }
	 */
}
