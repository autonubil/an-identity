package com.autonubil.identity.openid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

import com.autonubil.identity.openid.impl.entities.Jwk;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RsaJwk extends Jwk {

	private String e;
	private String n;
	
	public RsaJwk(RSAPublicKey publicKey) {
		this.e = Base64.getEncoder().encodeToString( publicKey.getPublicExponent().toByteArray());
		this.n = Base64.getEncoder().encodeToString( publicKey.getModulus().toByteArray());
		
		super.setKeyType("RSA");
		super.setAlgorythm("RS256");
		super.setUse("sig");
		MessageDigest md;
		String id;
		try {
			md = MessageDigest.getInstance("MD5");
			id = String.format("%s-%d", Base64.getEncoder().encodeToString(md.digest(publicKey.getEncoded())).replaceAll("=", "") , new Date().getTime());
		} catch (NoSuchAlgorithmException e1) {
			id = String.format("%d", new Date().getTime());
		}
		super.setId( id );
	}
	
	@JsonProperty("e")
	public String getExponent() {
		  return this.e;
	}
	
	public String getN() {
		return this.n;
	}
	
	
}
