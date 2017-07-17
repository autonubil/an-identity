package com.autonubil.identity.openid;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.apache.commons.codec.digest.Md5Crypt;

import com.autonubil.identity.openid.impl.entities.Jwk;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RsaJwk extends Jwk {

	private String e;
	private String n;
	
	public RsaJwk(RSAPublicKey publicKey) {
		this.e = Base64.getEncoder().encodeToString( publicKey.getPublicExponent().toByteArray());
		this.n = Base64.getEncoder().encodeToString( publicKey.getModulus().toByteArray());
		
		
		super.setAlgorythm("RSA");
		super.setUse("sig");
		super.setId(Md5Crypt.apr1Crypt( publicKey.getEncoded() ));
	}
	
	@JsonProperty("e")
	public String getExponent() {
		  return this.e;
	}
	
	public String getN() {
		return this.n;
	}
	
	
}
