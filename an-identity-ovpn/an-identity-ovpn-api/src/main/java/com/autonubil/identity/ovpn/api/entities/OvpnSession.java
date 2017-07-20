package com.autonubil.identity.ovpn.api.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.services.AuthService;
import com.autonubil.identity.ovpn.api.OvpnConfigService;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OvpnSession {

	private long vpnPid;
	private String code;
	private String local;
	private String ovpnId; 
	private String localNetmask;
	private String remote; 
	private String remoteNetmask;
	private String userName;
	private String userSourceId;
	private String pwdHash;
	private Date issued;
	private Date expires;
	private Date connected;
	 

	public OvpnSession() {
		
	}
	
	public OvpnSession(String code, String ovpnId, OvpnSessionConfigRequest configRequest ) {
		this.code = code;
		this.ovpnId = ovpnId;
		this.local = configRequest.getLocal();
		this.localNetmask = configRequest.getLocalNetmask();
		this.remote = configRequest.getRemote();
		this.remoteNetmask = configRequest.getRemoteNetmask();
		this.userSourceId = configRequest.getSourceId();
		this.userName = configRequest.getUsername();
		this.issued = new Date();
		this.expires = new Date(new Date().getTime() + (OvpnConfigService.SESSION_EXPIRY * 1000));
		this.vpnPid = Long.parseLong(configRequest.getVpnPid());
		this.connected = new Date(configRequest.getConnected()*1000);
		try {
			MessageDigest md5;
			md5 = MessageDigest.getInstance("MD5");
			this.pwdHash = Base64.getEncoder().encodeToString(md5.digest( String.format("%d:%d:%s", this.issued.getTime(),this.vpnPid,  configRequest.getPassword()  ).getBytes() )).replaceAll("=", "");
		} catch (NoSuchAlgorithmException e) {
			this.pwdHash = e.getMessage();
		}
	}
	
	public String getPwdHash() {
		return pwdHash;
	}

	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}

	public boolean validatePassword(OvpnSessionConfigRequest configRequest) {
		try {
			MessageDigest md5;
			md5 = MessageDigest.getInstance("MD5");
			String testHash = Base64.getEncoder().encodeToString(md5.digest( String.format("%d:%d:%s", this.issued.getTime(),this.vpnPid,  configRequest.getPassword()  ).getBytes() )).replaceAll("=", "");
			return this.pwdHash.equals(testHash);
		} catch (NoSuchAlgorithmException e) {
			return false;
		}
		
	}
  

	// from approval to token
	public String upgrade() {
		this.expires = new Date(new Date().getTime() + (OvpnConfigService.SESSION_EXPIRY * 1000));
		return this.code;
	}
 

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

  
	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
	}

	
	
	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getLocal() {
		return local;
	}


	public void setLocal(String local) {
		this.local = local;
	}


	public String getOvpnId() {
		return ovpnId;
	}


	public void setOvpnId(String ovpnId) {
		this.ovpnId = ovpnId;
	}


	public String getLocalNetmask() {
		return localNetmask;
	}


	public void setLocalNetmask(String localNetmask) {
		this.localNetmask = localNetmask;
	}


	public String getRemote() {
		return remote;
	}


	public void setRemote(String remote) {
		this.remote = remote;
	}


	public String getRemoteNetmask() {
		return remoteNetmask;
	}


	public void setRemoteNetmask(String remoteNetmask) {
		this.remoteNetmask = remoteNetmask;
	}


	@JsonIgnore
	public void setUser(User user) {
		if (user == null) {
			this.userName = null;
			this.userSourceId = null;
		} else {
			this.userName = user.getUsername();
			this.userSourceId = user.getSourceId();

		}
	}

	@JsonIgnore
	public User getUser(AuthService authService) {
		if ( (this.userName == null) || (this.userSourceId == null) ) {
			return null;
		}

		if (authService == null) {
			return null; 
		} else {
			User user = authService.getUser(this.userSourceId, this.userName );
			return user;
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserSourceId() {
		return userSourceId;
	}

	public void setUserSourceId(String userSourceId) {
		this.userSourceId = userSourceId;
	}

	public long getVpnPid() {
		return vpnPid;
	}

	public void setVpnPid(long vpnPid) {
		this.vpnPid = vpnPid;
	}

	public Date getConnected() {
		return connected;
	}

	public void setConnected(Date connected) {
		this.connected = connected;
	}


}
