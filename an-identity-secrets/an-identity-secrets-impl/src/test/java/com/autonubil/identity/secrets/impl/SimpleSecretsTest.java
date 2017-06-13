package com.autonubil.identity.secrets.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

public class SimpleSecretsTest {
/*
	@Test
	public void testSecrets() {
		DbSecretProvider service = new DbSecretProvider();
		service.setSecret("test", "someValue");
		
		String readBack = service.getSecret("test");
		Assert.assertEquals(readBack, "someValue");
		
		service.delteSecret("test");
		readBack = service.getSecret("test");
		Assert.assertEquals(readBack, null);
	}
	*/
	@Test
	public void getKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		SecretKey secretKey = keyGen.generateKey();
		System.err.println( Base64.getEncoder().encodeToString(secretKey.getEncoded()));
	}
}
