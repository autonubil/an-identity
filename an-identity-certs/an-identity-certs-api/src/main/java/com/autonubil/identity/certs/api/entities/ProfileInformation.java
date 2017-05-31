package com.autonubil.identity.certs.api.entities;

import java.util.List;

public class ProfileInformation {

	private String name;
	private String algorithm;
	private int keyLength;
	private String signatureAlgorithm;
	private List<String> allowedKeyUsages;
	private List<String> allowedExtendedKeyUsages;
	
}
