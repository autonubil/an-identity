package com.autonubil.identity.ldapotp.impl.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonubil.identity.ldap.api.LdapConfigService;
import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.UnsupportedOperation;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapter;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapterFactory;
import com.autonubil.identity.ldapotp.api.OtpToken;

@Service
public class OtpTokenService {

	private static Log log = LogFactory.getLog(OtpTokenService.class);
	
	@Autowired
	private LdapConfigService ldapConfigService;
	
	@Autowired(required=false)
	private List<LdapOtpAdapterFactory> factories = new ArrayList<LdapOtpAdapterFactory>();
	
	public LdapOtpAdapter getAdapter(String connectionId) throws UnsupportedOperation {
		try {
			LdapConnection conn = ldapConfigService.connect(connectionId);
			log.info("number of factories: "+factories.size());
			for(LdapOtpAdapterFactory f : factories) {
				log.info("number of factories: --- "+f.getClass());
				LdapOtpAdapter a = f.create(conn);
				if(a!=null) {
					return a;
				}
			}
			throw new UnsupportedOperation("no adapter found");
		} catch (Exception e) {
			throw new UnsupportedOperation("unable to find adapter",e);
		}
	}
	
	public List<OtpToken> list(String connectionId, String userId) throws UnsupportedOperation {
		LdapOtpAdapter a = getAdapter(connectionId);
		return a.listTokens(userId, null);
	}
	
	public OtpToken create(String connectionId, String userId, OtpToken token) throws UnsupportedOperation {
		LdapOtpAdapter a = getAdapter(connectionId);
		return a.createToken(userId, token);
	}
	
	public void delete(String connectionId, String userId, String tokenId) throws UnsupportedOperation {
		LdapOtpAdapter a = getAdapter(connectionId);
		a.deleteToken(userId, tokenId);
	}
	
	
}
