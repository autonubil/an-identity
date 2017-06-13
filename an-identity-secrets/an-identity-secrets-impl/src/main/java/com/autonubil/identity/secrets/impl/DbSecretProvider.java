package com.autonubil.identity.secrets.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.autonubil.identity.secrets.api.SecretsProvider;

@PropertySource(value="secrets.db.properties")
@Service
public class DbSecretProvider implements SecretsProvider{

	private static Log log = LogFactory.getLog(DbSecretProvider.class);

	
	@Value("secrets.db.keyLength")
	private int keyLength;
		
	@Value("secrets.db.algorythm")
	private String algorythm;
		
	@Value("secrets.db.cipher")
	private String cipher;
		
	@Value("secrets.db.key")
	private String key;
	
	
	@Autowired
	@Qualifier("secrets")
	private DataSource dataSource;
	
	
	@Override
	public String getId() {
		return "07c4377a-f058-46f3-b90e-300a55a7ed57";
	}

	@Override
	public String getName() {
		return "internal";
	}

	@Override
	public String getDescription() {
		return "Store Secrets in local Database";
	}
	
	private byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] encodedKey     = Base64.getDecoder().decode(this.key);
		SecretKey secretKey = new SecretKeySpec(encodedKey, 0, this.keyLength, this.algorythm);
 
		byte[] iv = new byte[this.keyLength / 8];
		SecureRandom prng = new SecureRandom();
		prng.nextBytes(iv);
		
		 Cipher cipher = Cipher.getInstance(this.cipher);
	
		 cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
		 
		 byte[] byteDataToEncrypt = data.getBytes();
		byte[] byteCipherText = cipher.doFinal(byteDataToEncrypt);
			
			
		 return byteCipherText;
	}
	
	private String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] encodedKey     = Base64.getDecoder().decode(this.key);
		SecretKey secretKey = new SecretKeySpec(encodedKey, 0, this.keyLength, this.algorythm);
 
		byte[] iv = new byte[this.keyLength / 8];
		SecureRandom prng = new SecureRandom();
		prng.nextBytes(iv);
		
		 Cipher cipher = Cipher.getInstance(this.cipher);
	
		 cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		 
  		 byte[] byteCipherText = cipher.doFinal(data);
			
			
		 return new String(byteCipherText);
	}
	
	
	

	@Override
	public String getSecret(String key) {
		if (key == null) 
			throw new NullPointerException("key must not be null");
		Connection c = null;
		PreparedStatement ps = null;
		try {

			c = dataSource.getConnection();
			ps = c.prepareStatement("SELECT secret FROM secrets WHERE id = ?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				byte[] data = rs.getBytes("secret");
				log.debug("Read secret from "+key);
				return decrypt(data);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try { ps.close(); } catch (Exception e2) {};
			try { c.close(); } catch (Exception e2) {};
		}
	}

	@Override
	public void setSecret(String key, String secret) {
		if (key == null) 
			throw new NullPointerException("key must not be null");
		if (secret == null) 
			throw new NullPointerException("secret must not be null");
		
		Connection c = null;
		PreparedStatement ps = null;
		try {

			c = dataSource.getConnection();
			ps = c.prepareStatement("DELETE FROM secrets WHERE id = key");
			ps.setString(1, key);
			ps.executeUpdate();
			ps.close();

			ps = c.prepareStatement("INSERT INTO serets (id,secret) VALUES (?,?)");
			ps.setString(1, key);
			
			byte[] data;
			data = encrypt(secret);
			
			ps.setBytes(2, data);
			ps.executeUpdate();
			log.debug("Wrote secret to "+key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try { ps.close(); } catch (Exception e2) {};
			try { c.close(); } catch (Exception e2) {};
		}
	}

	@Override
	public void delteSecret(String key) {
		if (key == null) 
			throw new NullPointerException("key must not be null");
		Connection c = null;
		PreparedStatement ps = null;
		try {

			c = dataSource.getConnection();
			ps = c.prepareStatement("DELETE FROM secrets WHERE id = key");
			ps.setString(1, key);
			ps.executeUpdate();
			ps.close(); 
			log.debug("deleted secret from "+key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try { ps.close(); } catch (Exception e2) {};
			try { c.close(); } catch (Exception e2) {};
		}
		
	}

}
