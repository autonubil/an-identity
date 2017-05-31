package com.autonubil.identity.certs.api;

import java.util.List;

import com.autonubil.identity.certs.api.entities.CertificateInformation;

public interface CertificateService {
	
	public List<CertificateInformation> list(/** filters **/);

	public CertificateInformation get(String id);

	public CertificateInformation revoke(String id);
	
	public CertificateInformation create(String parentId, String profile, String commonName, List<String> san, List<String> alternativeNames, List<String> extendedAlternativeNames);
	
	public CertificateInformation sign(String parentId, byte[] csr);
	
}
