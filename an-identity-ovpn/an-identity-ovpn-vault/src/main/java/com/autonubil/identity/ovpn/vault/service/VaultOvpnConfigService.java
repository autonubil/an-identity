package com.autonubil.identity.ovpn.vault.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.ovpn.api.OvpnClientConfigService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.autonubil.identity.ovpn.api.entities.Ovpn;
import com.autonubil.identity.ovpn.common.Renderer;
import com.autonubil.identity.ovpn.vault.entities.VaultConfiguration;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import com.bettercloud.vault.response.LogicalResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

 
@Service
public class VaultOvpnConfigService implements OvpnClientConfigService {

	@Autowired
	private OvpnConfigService ovpnConfigService;

	private VaultConfiguration configuration = new VaultConfiguration();

	@Override
	public String getClassName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public String getDisplayName() {
		return "Hashicorp Vault Secret Backend";
	}

	@Override
	public String getDescription() {
		return "Uess vault to issue Client Certificates";
	}

	@Override
	public JsonNode getConfigruation() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(this.configuration);
	}

	@Override
	public void setConfigruation(JsonNode configuration) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configuration = mapper.treeToValue(configuration, VaultConfiguration.class);
	}
	
	
	 /**
     * Generate the desired keypair
     * 
     * @param alg
     * @param keySize
     * @return
     */
    KeyPair generateKeyPair(String alg, int keySize) {
        try{
            KeyPairGenerator keyPairGenerator = null;
            keyPairGenerator = KeyPairGenerator.getInstance(alg);
             
            keyPairGenerator.initialize(keySize);
             
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         
        return null;
    }
    
   
	private String getUserCsr(KeyPair pair, String subject, String displayName, String sourceId, String dnPrefix, Ovpn ovpn) throws IOException, CertificateParsingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		
		String subjectPath =  "dc="+ovpn.getName()+",CN=" + subject+ "@"+sourceId;
		if ( (dnPrefix != null) && (dnPrefix.length() > 0) ) {
			subjectPath = dnPrefix+","+ subjectPath;
		}
		X500Principal  subjectDN = new X500Principal (subjectPath);
		
		
		Vector oids = new Vector();
		Vector values = new Vector();
        
        
        KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyAgreement);
        oids.add(X509Extension.keyUsage);
        values.add(new X509Extension(true, new DEROctetString(keyUsage)));
        
        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth);
        oids.add(X509Extension.extendedKeyUsage);
        values.add(new X509Extension(true, new DEROctetString(extendedKeyUsage)));
        
        GeneralNames subjectAltName = new GeneralNames( new GeneralName(GeneralName.directoryName, "uid="+ subject));
        oids.add(X509Extension.subjectAlternativeName);
        values.add(new X509Extension(false, new DEROctetString(subjectAltName)));
        
        oids.add(new ASN1ObjectIdentifier("2.16.840.1.113730.1.1"));
        values.add(new X509Extension(false, new DEROctetString( "client".getBytes())));

        oids.add(new ASN1ObjectIdentifier("2.16.840.1.113730.1.13"));
        values.add(new X509Extension(false, new DEROctetString( ("OpenVPN Client Certificate for " + displayName  + " on " + ovpn.getDescription()).getBytes() )));
			
			
        X509Extensions extensions = new X509Extensions(oids, values);
        
        Attribute attribute = new Attribute(
                                   PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
                                   new DERSet(extensions));
        
        PKCS10CertificationRequest kpGen = new PKCS10CertificationRequest(
        		"SHA256withRSA",
        		subjectDN,
                pair.getPublic(),
                new DERSet(attribute),
                pair.getPrivate());
        
        StringBuilder request = new StringBuilder(); 
        
        request.append("-----BEGIN CERTIFICATE REQUEST-----\n");
        request.append(  Base64.getEncoder(). encodeToString(kpGen.getEncoded()).replaceAll(".{76}", "$0\n") );
        request.append("\n-----END CERTIFICATE REQUEST----- \n");
        
        return request.toString();
	}
 
    
    X509Certificate getCaCert(String token) throws MalformedURLException, IOException, CertificateException {
    	 URL url = new URL( this.configuration.getVaultAddress()+"/v1/" +  this.configuration.getPkiPath()+"/ca/pem" );
    	 HttpURLConnection conn =  (HttpURLConnection)url.openConnection();
    	 conn.setRequestProperty("X-Vault-Token", token);
		 InputStream in = conn.getInputStream();

		 try {
			 CertificateFactory fact = CertificateFactory.getInstance("X.509");
		 	 return (X509Certificate) fact.generateCertificate(in);
		   
		   } finally {
			  IOUtils.closeQuietly(in);
		 }

    }
    
    String getCaPath(String token) throws MalformedURLException, IOException, CertificateException {
   	 URL url = new URL( this.configuration.getVaultAddress()+"/v1/" +  this.configuration.getPkiPath()+"/ca_chain" );
   	 HttpURLConnection conn =  (HttpURLConnection)url.openConnection();
   	 conn.setRequestProperty("X-Vault-Token", token);
		 InputStream in = conn.getInputStream();

		 try {
			 return IOUtils.toString(in, "UTF-8");
		   
		   } finally {
			  IOUtils.closeQuietly(in);
		 }

   }
    
	@Override
	public String getClientConfiguration(Ovpn resultVpn, Identity identity) throws IOException {
		Map<String, Object> params = Renderer.ovpnConfigToParamMap(this.configuration.getOvpnOptions());
		params.put("identity", identity);
		try {
			VaultConfig config = new VaultConfig(this.configuration.getVaultAddress()).build();
			Vault vault = new Vault(config);

			AuthResponse auth = null;
			if (this.configuration.getAuthMethod().equals("approle")) {
				auth = vault.auth().loginByAppRole(this.configuration.getAuthPath(), this.configuration.getRoleId(),
						this.configuration.getSecretId());
				config.token(auth.getAuthClientToken());
			}

			if (auth == null) {
				throw new IllegalArgumentException("No supported vault authentication method found");
			}

			
			
			Date now = new Date();
			// try to get user config from vault
			Map<String,String> userData = new HashMap<>();
			try {
				LogicalResponse userDataResponse = vault.logical().read("secret/vpn/users/" + identity.getUser().getUsername());
				CertificateFactory fact = CertificateFactory.getInstance("X.509");
				InputStream is = new ByteArrayInputStream( userDataResponse.getData().get("cert").getBytes());
				X509Certificate userCert =  (X509Certificate) fact.generateCertificate(is);
				
				if (userCert.getNotAfter().getTime() < now.getTime() ) {
					Map<String,String> tidyParams = new HashMap<>();
					tidyParams.put("tidy_cert_store", "true");
					tidyParams.put("tidy_revocation_list", "true");
					tidyParams.put("safety_buffer", "600");
					vault.logical().write(this.configuration.getPkiPath()+"/tidy", tidyParams);
					throw new Exception("Certificate expired");
				}
				params.put("key", userDataResponse.getData().get("key"));
				params.put("cert", userDataResponse.getData().get("cert"));

			} catch (Exception e) {
			
				KeyPair pair = generateKeyPair("RSA", 2048);
				
				
				// /ca/pem
				String csr = getUserCsr(pair, identity.getUser().getUsername(), identity.getUser().getDisplayName(), identity.getUser().getSourceId(), this.configuration.getDnPrefix(), resultVpn);
				
				X509Certificate caCert = getCaCert(auth.getAuthClientToken());
				Date caExpiry = caCert.getNotAfter();
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, 6);
				
				Date maxvalidityEndDate = new Date(calendar.getTime().getTime());
				/*
				Date validityEndDate = identity.getExpires();
				
					if (validityEndDate.getTime() > caExpiry.getTime())
				 */
				Date validityEndDate = caExpiry;
				if (validityEndDate.getTime() > maxvalidityEndDate.getTime())
					validityEndDate = maxvalidityEndDate;
				
				Map<String,String> csrParams = new HashMap<>();
//				csrParams.put("common_name",    identity.getUser().getSourceId()+"\\:"+ identity.getUser().getUsername());
				csrParams.put("csr", csr);
				csrParams.put("format", "pem");
				csrParams.put("key_bits", "2048");
				csrParams.put("key_type", "rsa");
				
				csrParams.put("ttl", Long.toString( (validityEndDate.getTime() - now.getTime() ) / 1000 ) );
			 	LogicalResponse signResponse = vault.logical().write(this.configuration.getPkiPath()+"/sign-verbatim/vpnuser", csrParams);
			 	String cert = signResponse.getData().get("certificate");
			 	
			 	
			 	String keypem  = "-----BEGIN RSA PRIVATE KEY-----\n" +
			 			 Base64.getEncoder(). encodeToString(pair.getPrivate().getEncoded()).replaceAll(".{76}", "$0\n") +
				          "\n-----END RSA PRIVATE KEY-----\n";
				params.put("key", keypem);
				params.put("cert", cert);
				
				userData.clear();
				userData.put("key", keypem);
				userData.put("cert", cert);
				LogicalResponse writeUserDataToCubbyHoleResponse =  vault.logical().write("secret/vpn/users/" + identity.getUser().getUsername(), userData);
			 	
			}
			params.put("ca", getCaPath(auth.getAuthClientToken()));
			
			
			String result = Renderer.renderClientConfig(params);
			return result;
		} catch (VaultException |  InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | CertificateException e) {
			throw new RuntimeException("Vault or certificate access problems", e);
		}
	}

	@Override
	public String getId() {
		return "65425941-7eaa-445a-a602-4cb385e2db57";
	}
}
