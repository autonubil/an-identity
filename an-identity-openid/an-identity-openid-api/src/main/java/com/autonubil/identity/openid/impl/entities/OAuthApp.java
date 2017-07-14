package com.autonubil.identity.openid.impl.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

public class OAuthApp {
	@JsonView(OAuthAppViews.Public.class)
	private String id;
	@JsonView(OAuthAppViews.Public.class)
	private String name;
	@JsonView(OAuthAppViews.Public.class)
	private String linkedAppId;
	@JsonView(OAuthAppViews.Public.class)
	private boolean userApprovalRequired = false;
	@JsonView(OAuthAppViews.Public.class) 
	private List<String> scopes;
	
	@JsonView(OAuthAppViews.Internal.class) 
	private String secret;

	public OAuthApp() {
		this.scopes = new ArrayList<>();
	}

	public OAuthApp(String id) {
		this();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

 
	public boolean isUserApprovalRequired() {
		return userApprovalRequired;
	}

	public void setUserApprovalRequired(boolean userApprovalRequired) {
		this.userApprovalRequired = userApprovalRequired;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		if (scopes == null) {
			this.scopes.clear();
		} else {
			this.scopes =  new ArrayList<>(scopes);
			if (this.scopes.contains("") ) {
				this.scopes.remove("");
			}
			
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLinkedAppId() {
		return linkedAppId;
	}

	public void setLinkedAppId(String linkedAppId) {
		this.linkedAppId = linkedAppId;
	}
	
	 

}
