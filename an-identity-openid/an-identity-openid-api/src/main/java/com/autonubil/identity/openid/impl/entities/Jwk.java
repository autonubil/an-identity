package com.autonubil.identity.openid.impl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Jwk {
	@JsonProperty("kid")
	private String  id;

	@JsonProperty("kty")
	private String  algorythm ="RSA";
	
	@JsonProperty("use")
	private String  use = "RS256";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlgorythm() {
		return algorythm;
	}

	public void setAlgorythm(String algorythm) {
		this.algorythm = algorythm;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}
	
	

}
