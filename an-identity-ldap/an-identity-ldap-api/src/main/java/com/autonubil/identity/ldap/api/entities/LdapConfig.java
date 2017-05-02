package com.autonubil.identity.ldap.api.entities;

public class LdapConfig {
	
	public static enum ENCRYPTION { NONE, SSL, START_TLS };
	public static enum AUTH { SIMPLE, DIGEST_MD5, CRAM_MD5, GSSAPI };
	

	private String serverType;
	private boolean useOtp;
	private boolean useAsAuth;
	private boolean trustAll;
	
	private String id;
	private String name="";
	private String host="";
	private int port;
	private ENCRYPTION encryption = ENCRYPTION.NONE;
	private AUTH auth = AUTH.SIMPLE;
	private String adminBindDn = "";
	private String rootDse = "";
	
	private String cert; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ENCRYPTION getEncryption() {
		return encryption;
	}

	public void setEncryption(ENCRYPTION encryption) {
		this.encryption = encryption;
	}

	public AUTH getAuth() {
		return auth;
	}

	public void setAuth(AUTH auth) {
		this.auth = auth;
	}

	public String getAdminBindDn() {
		return adminBindDn;
	}

	public void setAdminBindDn(String adminBindDn) {
		this.adminBindDn = adminBindDn;
	}

	public String getRootDse() {
		return rootDse;
	}

	public void setRootDse(String rootDse) {
		this.rootDse = rootDse;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public boolean isUseAsAuth() {
		return useAsAuth;
	}

	public void setUseAsAuth(boolean useAsAuth) {
		this.useAsAuth = useAsAuth;
	}

	public boolean isTrustAll() {
		return trustAll;
	}

	public void setTrustAll(boolean trustAll) {
		this.trustAll = trustAll;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public boolean isUseOtp() {
		return useOtp;
	}

	public void setUseOtp(boolean useOtp) {
		this.useOtp = useOtp;
	}
	
}
