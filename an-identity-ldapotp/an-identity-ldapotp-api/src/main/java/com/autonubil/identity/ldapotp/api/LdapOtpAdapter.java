package com.autonubil.identity.ldapotp.api;

import java.util.List;

import javax.naming.directory.DirContext;

public interface LdapOtpAdapter {
	
	public List<OtpToken> listTokens(String userId, String tokenId);
	public OtpToken getToken(String userId, String tokenId);
	public OtpToken createToken(String userId, OtpToken token);
	public void deleteToken(String userId, String tokenId);

	public void setDirContext(DirContext context);
	public void updateOtpGroup(String otpGroup);
	
}
