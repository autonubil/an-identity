package com.autonubil.identity.util.ssl;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;

public class CertUtil {

	public static X509Certificate getCertificate(String in) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");   
		X509Certificate certificate = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(in.getBytes()));
		return certificate;
	}

	public static String getPem(X509Certificate cert) throws CertificateEncodingException {
		 Base64 encoder = new Base64(64);
		 String cert_begin = "-----BEGIN CERTIFICATE-----\n";
		 String end_cert = "-----END CERTIFICATE-----";

		 byte[] derCert = cert.getEncoded();
		 String pemCertPre = new String(encoder.encode(derCert));
		 String pemCert = cert_begin + pemCertPre + end_cert;
		 return pemCert;
	}
}
