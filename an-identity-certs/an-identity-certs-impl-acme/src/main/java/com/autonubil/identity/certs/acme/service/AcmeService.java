package com.autonubil.identity.certs.acme.service;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.autonubil.identity.certs.api.CertificateService;
import com.autonubil.identity.certs.api.entities.CertificateInformation;
import com.autonubil.identity.secrets.impl.DbSecretProvider;

@Service
@PropertySource("certs.acme.properties")
public class AcmeService implements CertificateService {

	private static Log log = LogFactory.getLog(AcmeService.class);

	private Session acmeSession;

	@Value("${certs.acme.enabled}")
	private boolean enabled;

	@Value("${certs.acme.key_bits}")
	private int keyBits;

	@Value("${certs.acme.provider_url}")
	private String providerUrl;

	@Value("${certs.acme.registration_contact}")
	private String registrationContact;

	@Autowired
	DbSecretProvider secretsProvider;
	
	private Registration registration;

	private static final String SECRETSTORE_KEY = "certs/acme/privateKey";

	@PostConstruct
	public void init() throws InvalidKeySpecException, NoSuchAlgorithmException, AcmeException {
		
		if (!enabled) {
			return;
		}
		
		String privatkeyBase64 = secretsProvider.getSecret(SECRETSTORE_KEY);
		PublicKey publicKey = null;
		PrivateKey privateKey = null;

		if (privatkeyBase64 == null) {
			log.info("Creating new private key");
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(keyBits);
			KeyPair keyPair = kpg.genKeyPair();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
			RSAPrivateKey rsaPrivateKey  = ((RSAPrivateKey)privateKey);
			byte[] encoded = rsaPrivateKey.getEncoded();
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);
			secretsProvider.setSecret(SECRETSTORE_KEY, Base64.getEncoder().encodeToString(spec.getEncoded()));
		} else {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privatkeyBase64.getBytes()) );
			privateKey = keyFactory .generatePrivate(spec);
			RSAPrivateKey rasPrivKey = (RSAPrivateKey) privateKey;
			RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(rasPrivKey.getModulus(), BigInteger.valueOf(65537));
			publicKey = keyFactory.generatePublic(publicKeySpec);
		}
		KeyPair keyPair = new KeyPair(publicKey, privateKey);
		this.acmeSession = new Session(this.providerUrl, keyPair);
		
		
		RegistrationBuilder builder = new RegistrationBuilder();
		builder.addContact(registrationContact);

		this.registration = builder.create(this.acmeSession);

		URI accountLocationUri = this.registration.getLocation();
		
		log.info("Registered as: "+  accountLocationUri.toString());
		
	}

	public Http01Challenge getChallengeForToken(String token) {
		if (token == null)
			throw new NullPointerException("token must not be null");
		return null;
	}
 

	@Override
	public List<CertificateInformation> list() {
		throw new NotImplementedException("Operation not supported for ACME");
	}

	@Override
	public CertificateInformation get(String id) {
		throw new NotImplementedException("Operation not supported for ACME");
	}

	@Override
	public CertificateInformation revoke(String id) {
		throw new NotImplementedException("Operation not supported for ACME");
	}

	@Override
	public CertificateInformation create(String parentId, String profile, String commonName, List<String> san,
			List<String> alternativeNames, List<String> extendedAlternativeNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CertificateInformation sign(String parentId, byte[] csr) {
		throw new NotImplementedException("Operation not supported for ACME");
	}

}
