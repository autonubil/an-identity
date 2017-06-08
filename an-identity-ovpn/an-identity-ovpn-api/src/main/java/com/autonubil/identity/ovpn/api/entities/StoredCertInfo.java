package com.autonubil.identity.ovpn.api.entities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class StoredCertInfo {
	X509Certificate certificate;
	String certificatePem;
	String keyPem;
	boolean valid;
	
	public StoredCertInfo(String certificatePem, String keyPem) throws CertificateException{
		this.keyPem = keyPem;
		this.certificatePem = certificatePem;
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		InputStream is = new ByteArrayInputStream(certificatePem.getBytes());
		this.certificate =  (X509Certificate) fact.generateCertificate(is);
		
		long t = this.certificate.getNotAfter().getTime();
		long n = System.currentTimeMillis();
		this.valid = (t - n > 0);
	}
	
	public String getCertificatePem() {
		return certificatePem;
	}
	public void setCertificatePem(String certificatePem) {
		this.certificatePem = certificatePem;
	}
	public String getKeyPem() {
		return keyPem;
	}
	public void setKeyPem(String keyPem) {
		this.keyPem = keyPem;
	}
	public X509Certificate getCertificate() {
		return certificate;
	}
	
	public String getSerial() {
		String result = this.getCertificate().getSerialNumber().toString();
		return result ;
	}

	public String getSerialOctal() {
		String result = this.getCertificate().getSerialNumber().toString(8);
		if (result.length() % 2 != 0)
				result = "0"+ result;
		result = result.replaceAll("..(?!$)", "$0:");
		
		return result ;
	}

	public String getSerialHex() {
		String  result = this.getCertificate().getSerialNumber().toString(16);
		if (result.length() % 2 != 0)
				result = "0"+ result;
		result = result.replaceAll("..(?!$)", "$0-");
		
		return result ;
	}

	public boolean getValid() {
  		return this.valid;
	}

	
	
 
}
