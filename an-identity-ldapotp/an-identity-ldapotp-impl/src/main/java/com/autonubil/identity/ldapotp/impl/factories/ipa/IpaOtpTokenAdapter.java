package com.autonubil.identity.ldapotp.impl.factories.ipa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autonubil.identity.ldap.api.LdapConnection;
import com.autonubil.identity.ldap.api.LdapSearchResultMapper;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldapotp.api.LdapOtpAdapter;
import com.autonubil.identity.ldapotp.api.OtpToken;
import com.autonubil.identity.util.ldap.LdapEncoder;
import com.autonubil.identity.util.totp.TotpUtil;

public class IpaOtpTokenAdapter implements LdapOtpAdapter {

	private static Log log = LogFactory.getLog(IpaOtpTokenAdapter.class);
	
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
					new String[] {"ipatokenUniqueID", "description", "createTimestamp", "ipatokenTOTPtimeStep", "ipatokenOTPkey", "ipatokenOTPalgorithm", "ipatokenOTPdigits", "ipatokenTOTPclockOffset" }, 
					new LdapSearchResultMapper<OtpToken>() {
						
						public OtpToken map(SearchResult r) {
							try {
								OtpToken out = new OtpToken();
								out.setDn(r.getNameInNamespace());
								out.setOwnerDn(user.getDn());
								out.setComment(r.getAttributes().get("description").get()+"");
								out.setHash(r.getAttributes().get("ipatokenOTPalgorithm").get()+"");
								out.setOffsetSeconds(Integer.parseInt(r.getAttributes().get("ipatokenTOTPclockOffset").get()+""));
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
			token.setId(UUID.randomUUID().toString());
			LdapUser user = ldapConnection.getUserById(userId);
			attributes.put("ipatokenTOTPtimeStep", new Integer(token.getStepSeconds())+"");
			attributes.put("ipatokenUniqueID", token.getId());
			attributes.put("ipatokenOTPkey", bytes);
			attributes.put("ipatokenOTPdigits", token.getLength()+"");
			attributes.put("ipatokenOwner", user.getDn());
			attributes.put("ipatokenOTPalgorithm", token.getHash());
			attributes.put("description", token.getComment());
			attributes.put("ipatokenTOTPclockOffset", "0");
			ldapConnection.createEntry("ipatokenuniqueid="+token.getId()+",cn=otp,"+ldapConnection.getBaseDn(), new String[] {"ipatoken", "ipatokentotp"} , attributes);
			try {
				List<OtpToken> tokens = listTokens(userId, null);
				if(tokens.size()==1) {
					updateOtpGroup(ldapConnection.getConfig().getOtpGroup());
				}
			} catch (Exception e) {
				log.warn("unable to update otp group",e);
			}
			return token;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteToken(String userId, String tokenId) {
		
		try {
			List<OtpToken> tokens = listTokens(userId, null);
			if(tokens.size()==0) {
				updateOtpGroup(ldapConnection.getConfig().getOtpGroup());
			}
		} catch (Exception e) {
			log.warn("unable to update otp group",e);
		}
	}

	@Override
	public void setDirContext(DirContext context) {
		this.context = context;
	}

	@Override
	public void updateOtpGroup(String otpGroup) {
		if(otpGroup==null || otpGroup.length()==0) {
			return;
		}
		try {
			List<String> tokenOwners = ldapConnection.getList(
				ldapConnection.getBaseDn(), 
				"(objectClass=ipatokentotp)", 
				new String[] { "ipatokenowner" }, 
				new LdapSearchResultMapper<String>() {
					
					public String map(SearchResult r) {
						try {
							return r.getAttributes().get("ipatokenowner").get()+"";
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			);
			List<List<String>> tokenGroupMembers = ldapConnection.getList(
					otpGroup, 
					"(objectClass=groupofnames)", 
					new String[] { "member" }, 
					new LdapSearchResultMapper<List<String>>() {
						
						public List<String> map(SearchResult r) {
							try {
								NamingEnumeration<?> ne = r.getAttributes().get("member").getAll();
								List<String> out = new ArrayList<>();
								while(ne.hasMore()) {
									out.add(ne.next()+"");
								}
								return out; 
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
					);

			List<String> toRemove = new ArrayList<>(); 

			for(List<String> ms : tokenGroupMembers) {
				for(String m : ms) {
					if(tokenOwners.contains(m)) {
						tokenOwners.remove(m);
					} else {
						toRemove.add(m);
					}
				}
			}
			
			ModificationItem[] items = new ModificationItem[tokenOwners.size()+toRemove.size()];
			int index=0;
			
			for(String s : tokenOwners) {
				log.info(s+" should be added");
				items[index] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("member", s));
				index++;
			}
			for(String s : toRemove) {
				log.info(s+" should be removed");
				items[index] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", s));
				index++;
			}
			
			ldapConnection.getContext().modifyAttributes(otpGroup, items);
			
 		} catch (Exception e) {
			log.error("error updating otp group: ",e);
		}
		
	}
	
}
