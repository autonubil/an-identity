package com.autonubil.identity.certs.api.entities;

import java.util.Date;
import java.util.List;

public class CertificateInformation {

	private String id;
	private String algorithm;
	private int keyLength;
	private String signatureAlgorithm;
	private String publicKey;
	private String issuer;
	private String subject;
	private String serial;
	private Date notValidBefore;
	private Date notValidAfter;
	private List<String> keyUsage;
	private List<String> extendedKeyUsage;
	private List<String> alternativeNames;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public List<String> getAlternativeNames() {
		return alternativeNames;
	}

	public void setAlternativeNames(List<String> alternativeNames) {
		this.alternativeNames = alternativeNames;
	}

	public List<String> getKeyUsage() {
		return keyUsage;
	}

	public void setKeyUsage(List<String> keyUsage) {
		this.keyUsage = keyUsage;
	}

	public List<String> getExtendedKeyUsage() {
		return extendedKeyUsage;
	}

	public void setExtendedKeyUsage(List<String> extendedKeyUsage) {
		this.extendedKeyUsage = extendedKeyUsage;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Date getNotValidAfter() {
		return notValidAfter;
	}

	public void setNotValidAfter(Date notValidAfter) {
		this.notValidAfter = notValidAfter;
	}

	public Date getNotValidBefore() {
		return notValidBefore;
	}

	public void setNotValidBefore(Date notValidBefore) {
		this.notValidBefore = notValidBefore;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
