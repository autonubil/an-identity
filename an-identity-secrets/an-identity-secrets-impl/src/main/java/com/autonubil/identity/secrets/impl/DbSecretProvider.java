package com.autonubil.identity.secrets.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

	private static byte[] iv = {0xa, 0xd, 0x1, 0x4, 0x2, 0x2, 0x3, 0xc, 0xa, 0xd, 0x1, 0x4, 0x2, 0x2, 0x3, 0xc};
	private IvParameterSpec ivspec = new IvParameterSpec( iv);

	
	@Value("secrets.db.master_secrety")
	private String masterSecret;
	
	
	@Qualifier("secrets")
	@Autowired
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
	
	private byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		 MessageDigest sha = MessageDigest.getInstance("SHA-1");
	        
	        if (data.length() % 8 != 0) {
	        	data = data + "        ".substring(0,data.length() % 8);
	        }
	        
	        byte key[] = sha.digest(this.masterSecret.getBytes());
	        key = Arrays.copyOf(key, 16); // use only first 128 bit
	        SecretKeySpec secret = new SecretKeySpec(key, "AES");
	        
	        byte[] crypted = null;
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secret,ivspec);
	        byte[] ptext = data.getBytes("UTF-8");
	        crypted = cipher.doFinal(ptext);
	        return crypted;
	}
	
	private String decrypt(byte[] data) throws Exception {
		  byte[] output = null;
	        
	        MessageDigest sha = MessageDigest.getInstance("SHA-1");
	        byte key[] = sha.digest(this.masterSecret.getBytes());
	        key = Arrays.copyOf(key, 16); // use only first 128 bit
	        SecretKeySpec secret = new SecretKeySpec(key, "AES");
	        
	        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); //Change here
	        cipher.init(Cipher.DECRYPT_MODE, secret,ivspec);
	        output = cipher.doFinal(data);
	        if(output==null){
	            throw new Exception();
	        }

	        // return new String[]{new String(output),iv};
	        return new String(output,"UTF-8").trim();
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
			ps = c.prepareStatement("DELETE FROM secrets WHERE id = ?");
			ps.setString(1, key);
			ps.executeUpdate();
			ps.close();

			ps = c.prepareStatement("INSERT INTO secrets (id,secret) VALUES (?,?)");
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
