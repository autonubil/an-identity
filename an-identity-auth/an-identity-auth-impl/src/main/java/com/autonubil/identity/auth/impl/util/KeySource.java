package com.autonubil.identity.auth.impl.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.autonubil.identity.auth.api.PrivateKeySource;
import com.autonubil.identity.auth.api.PublicKeySource;

@Component
public class KeySource implements PublicKeySource, PrivateKeySource {

	private KeyPair keyPair;

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
	}
	
	@Override
	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}

	@Override
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}

}
