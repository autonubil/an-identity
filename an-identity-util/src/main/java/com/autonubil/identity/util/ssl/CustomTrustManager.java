package com.autonubil.identity.util.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;

public class CustomTrustManager implements X509TrustManager {

	private List<X509Certificate> trusted = new ArrayList<>();
	
	public CustomTrustManager(X509Certificate[] certs) {
		for(X509Certificate c : certs) {
			trusted.add(c);
		}
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
		int a = 0;
		int b = 0;
		for(X509Certificate c1 : x509Certificates) {
			a++;
			String x1 = new Base64().encodeAsString(c1.getEncoded());
			for(X509Certificate c2:trusted) {
				String x2 = new Base64().encodeAsString(c2.getEncoded());
				b++;
				if(x1.compareTo(x2)==0) {
					return;
				}
				try {
					c1.verify(c2.getPublicKey());
					// yes
					return;
				} catch (Exception e) {
					// nope
				}
				try {
					c1.verify(c2.getPublicKey());
					// yes
					return;
				} catch (Exception e) {
					// nope
				}
			}
		}
		String[] pems = new String[x509Certificates.length];
		for(int i=0;i<pems.length;i++) {
			pems[i] = CertUtil.getPem(x509Certificates[i]);
		}
		throw new SslException("untrusted certificates", pems);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}