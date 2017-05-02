package com.autonubil.identity.ldapotp.impl.factories.ipa;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapter;
import com.autonubil.identity.ldapotp.api.OtpToken;
import com.autonubil.identity.util.ldap.LdapEncoder;

public class IpaOtpTokenAdapter implements LdapOtpAdapter {

	private DirContext context;
	
	
	@Override
	public List<OtpToken> listTokens(String userId, String tokenId) {
		String uf = String.format("(&(objectClass=inetuser)(ipaUniqueId=%1$s))",LdapEncoder.escapeLDAPSearchFilter(userId));
		/**
		context.get
		
		SearchControls searchControls = new SearchControls();
	    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    NamingEnumeration<SearchResult> results = context.search("",uf,searchControls);
	    try {
	    	if(results.hasMore()) {
	    		return results.next();
	    	}
		} catch (Exception e) {
			log.warn("following referrals might yield more results, filter was: "+filter);
		}
    	return null;
		**/
		return new ArrayList<OtpToken>();
	}

	@Override
	public OtpToken getToken(String userId, String tokenId) {
		return new OtpToken();
	}

	@Override
	public OtpToken createToken(String userId, OtpToken token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteToken(String userId, String tokenId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDirContext(DirContext context) {
		this.context = context;
	}

}
