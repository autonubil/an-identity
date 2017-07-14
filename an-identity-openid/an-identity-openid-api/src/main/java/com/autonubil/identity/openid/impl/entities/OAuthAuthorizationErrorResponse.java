package com.autonubil.identity.openid.impl.entities;

public class OAuthAuthorizationErrorResponse {

	/**
	 * Required. Must be one of a set of predefined error codes. See the specification for the codes and their meaning.
	 */
	private String error;

	/**
	 * Required, if present in authorization request. The same value as sent in the state parameter in the request.
	 */
	private String state;

	
	/**
	 * Optional. A human-readable UTF-8 encoded text describing the error. Intended for a developer, not an end user.
	 */
	private String error_description;

	
	/**
	 * Optional. A URI pointing to a human-readable web page with information about the error.
	 */
	private String error_uri;

	
	public OAuthAuthorizationErrorResponse(String error, String error_description, String state) {
		this.error = error;
		this.error_description = error_description;
		this.state = state;
	}
	
	public OAuthAuthorizationErrorResponse(String error, String error_description, String error_uri, String state) {
		this.error = error;
		this.error_description = error_description;
		this.error_uri = error_uri;
		this.state = state;
	}
	
	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getErrorDescription() {
		return error_description;
	}


	public void setErrorDescription(String error_description) {
		this.error_description = error_description;
	}


	public String getErrorUri() {
		return error_uri;
	}


	public void setErrorUri(String error_uri) {
		this.error_uri = error_uri;
	}


	
	
}
