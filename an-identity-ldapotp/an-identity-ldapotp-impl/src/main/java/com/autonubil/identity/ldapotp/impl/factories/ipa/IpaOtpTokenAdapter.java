package com.autonubil.identity.ldapotp.impl.factories.ipa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.LdapSearchResultMapper;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapter;
import com.autonubil.identity.ldapotp.api.OtpToken;
import com.autonubil.identity.util.ldap.LdapEncoder;
import com.autonubil.identity.util.totp.TotpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IpaOtpTokenAdapter implements LdapOtpAdapter {

	private DirContext context;
	private LdapConnection ldapConnection;
	
	
	public IpaOtpTokenAdapter(LdapConnection connection) {
		this.ldapConnection = connection;
		this.context = connection.getContext();
	}

	@Override
	public List<OtpToken> listTokens(String userId, String tokenId) {
		
		try {
			final LdapUser user = ldapConnection.getUserById(userId);
			List<String> filters = new ArrayList<>();
			filters.add("(&");
			filters.add("(objectClass=ipatokentotp)");
			filters.add(String.format("(ipatokenOwner=%1$s)",LdapEncoder.escapeDn(user.getDn())));
			if(tokenId!=null) {
				filters.add(String.format("(ipatokenUniqueID=%1$s)",tokenId));
			}
			filters.add(")");
			String otpFilter = StringUtils.join(filters,"");
			List<OtpToken> tokens = ldapConnection.getList(
					ldapConnection.getBaseDn(), 
					otpFilter, 
					new String[] {"ipatokenUniqueID", "description", "createTimestamp", "ipatokenTOTPtimeStep", "ipatokenOTPkey", "ipatokenOTPalgorithm", "ipatokenOTPdigits" }, 
					new LdapSearchResultMapper<OtpToken>() {
						
						public OtpToken map(SearchResult r) {
							try {
								OtpToken out = new OtpToken();
								out.setDn(r.getNameInNamespace());
								out.setOwnerDn(user.getDn());
								out.setCreated(ldapConnection.parseDate(r.getAttributes().get("createTimestamp").get()+""));
								out.setStepSeconds(Integer.parseInt(r.getAttributes().get("ipatokenTOTPtimeStep").get()+""));
								byte[] bytes = (byte[])r.getAttributes().get("ipatokenOTPkey").get();
								out.setSecret(TotpUtil.toString(bytes));
								out.setId(r.getAttributes().get("ipatokenUniqueID").get()+"");
								return out;
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
					);
			
			return tokens;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OtpToken getToken(String userId, String tokenId) {
		List<OtpToken> ts = listTokens(userId, tokenId);
		if(ts.size()>0) {
			return ts.get(0);
		}
		return null;
	}

	@Override
	public OtpToken createToken(String userId, OtpToken token) {
		Map<String,Object> attributes = new HashMap<>();
		try {
			byte[] bytes = TotpUtil.toBytes(token.getSecret());
			
			System.err.println(token.getSecret()+" ---> "+bytes+"   ("+bytes.length+" bytes)");
			
			LdapUser user = ldapConnection.getUserById(userId);
			attributes.put("ipatokenTOTPtimeStep", new Integer(token.getStepSeconds())+"");
			attributes.put("ipatokenUniqueID", token.getId());
			attributes.put("ipatokenOTPkey", bytes);
			attributes.put("ipatokenOTPdigits", new Integer(6)+"");
			attributes.put("ipatokenOwner", user.getDn());
			attributes.put("ipatokenOTPalgorithm", "sha1");
			attributes.put("ipatokenTOTPclockOffset", "0");
			
			ldapConnection.createEntry("ipatokenuniqueid="+token.getId()+",cn=otp,"+ldapConnection.getBaseDn(), new String[] {"ipatoken", "ipatokentotp"} , attributes);
			return token;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteToken(String userId, String tokenId) {
		

	}

	@Override
	public void setDirContext(DirContext context) {
		this.context = context;
	}

}
