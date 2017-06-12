package com.autonubil.identity.ovpn.api.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OvpnOptions {

	private String dev = "tun";
	private int fragment;
	private int mssfix = 0;
	private int sndbuf = 10000;
	private int rcvbuf = 10000;
	private int verb = 3;
	private String cipher = "AES-256-CBC";
	private String auth = "SHA256";
	
	private String resolvRetry = "infinite";
	private String setenv = "PUSH_PEER_INFO";
	private boolean nobind = true;
	private boolean persistKey = true;
	private boolean persistTun = true;
	private String nsCertType = null; // "server";
	
	private String ca;
	private String cert;
	private String tlsAuth;
	
	// 6 month
	private long maxUserCertTtl = 60*60*24*182;
	
	private boolean authUserPass = false;
	
	private List<OvpnRemote> remotes;

	
	public OvpnOptions () {
		this.remotes = new ArrayList<>();
	}

	
	public String getDev() {
		return dev;
	}
	public void setDev(String dev) {
		this.dev = dev;
	}
 
	public int getFragment() {
		return fragment;
	}
	public void setFragment(int fragment) {
		this.fragment = fragment;
	}
	public int getMssfix() {
		return mssfix;
	}
	public void setMssfix(int mssfix) {
		this.mssfix = mssfix;
	}
	public int getSndbuf() {
		return sndbuf;
	}
	public void setSndbuf(int sndbuf) {
		this.sndbuf = sndbuf;
	}
	public int getRcvbuf() {
		return rcvbuf;
	}
	public void setRcvbuf(int rcvbuf) {
		this.rcvbuf = rcvbuf;
	}
	public int getVerb() {
		return verb;
	}
	public void setVerb(int verb) {
		this.verb = verb;
	}
	public String getCipher() {
		return cipher;
	}
	public void setCipher(String cipher) {
		this.cipher = cipher;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getResolvRetry() {
		return resolvRetry;
	}
	public void setResolvRetry(String resolvRetry) {
		this.resolvRetry = resolvRetry;
	}
	public String getSetenv() {
		return setenv;
	}
	public void setSetenv(String setenv) {
		this.setenv = setenv;
	}
	public boolean isNobind() {
		return nobind;
	}
	public void setNobind(boolean nobind) {
		this.nobind = nobind;
	}
	public boolean isPersistKey() {
		return persistKey;
	}
	public void setPersistKey(boolean persistKey) {
		this.persistKey = persistKey;
	}
	public boolean isPersistTun() {
		return persistTun;
	}
	public void setPersistTun(boolean persistTun) {
		this.persistTun = persistTun;
	}
	public String getNsCertType() {
		return nsCertType;
	}
	public void setNsCertType(String nsCertType) {
		this.nsCertType = nsCertType;
	}
	public List<OvpnRemote> getRemotes() {
		return remotes;
	}
	public void setRemotes(List<OvpnRemote> remotes) {
		this.remotes = remotes;
	}
	public String getCa() {
		return ca;
	}
	public void setCa(String ca) {
		this.ca = ca;
	}
	public String getCert() {
		return cert;
	}
	public void setCert(String cert) {
		this.cert = cert;
	}
	public String getTlsAuth() {
		return tlsAuth;
	}
	public void setTlsAuth(String tlsAuth) {
		this.tlsAuth = tlsAuth;
	}


	public boolean isAuthUserPass() {
		return authUserPass;
	}


	public void setAuthUserPass(boolean authUserPass) {
		this.authUserPass = authUserPass;
	}


	public long getMaxUserCertTtl() {
		return maxUserCertTtl;
	}


	public void setMaxUserCertTtl(long maxUserCertTtl) {
		this.maxUserCertTtl = maxUserCertTtl;
	}


		 
	
}
