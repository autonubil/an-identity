package com.autonubil.identity.ldap.impl.services;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.X509TrustManager;

public class TrustManagerDelegate implements X509TrustManager {

	private List<X509TrustManager> trustManagers = new ArrayList<X509TrustManager>();

	public TrustManagerDelegate(X509TrustManager... trustManagers) {
		for(X509TrustManager tm : trustManagers) {
			this.trustManagers.add(tm);
		}
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
		for(X509TrustManager tm : trustManagers) {
			try {
				tm.checkClientTrusted(x509Certificates, authType);
				return;
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
		for(X509TrustManager tm : trustManagers) {
			try {
				tm.checkServerTrusted(x509Certificates, authType);
				return;
			} catch (Exception e) {
			}
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		ArrayList<X509Certificate> out = new ArrayList<X509Certificate>();
		for(X509TrustManager tm : trustManagers) {
			for(X509Certificate c : tm.getAcceptedIssuers()) {
				out.add(c);
			}
		}
		return out.toArray(new X509Certificate[out.size()]);
	}

}