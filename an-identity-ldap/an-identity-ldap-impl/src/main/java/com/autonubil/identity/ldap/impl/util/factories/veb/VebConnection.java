package com.autonubil.identity.ldap.impl.util.factories.veb;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import com.autonubil.identity.ldap.api.entities.LdapConfig;
import com.autonubil.identity.ldap.api.entities.LdapCustomsFieldConfig;
import com.autonubil.identity.ldap.api.entities.LdapUser;
import com.autonubil.identity.ldap.impl.util.factories.apacheds.ApacheDsConnection;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.util.ldap.LdapEncoder;

public class VebConnection extends ApacheDsConnection {

	private static List<String> transformNames(String in) {
		List<String> out = new ArrayList<>();
		out.add(in.toLowerCase().replace(" ", "."));
		out.add("extern."+in.toLowerCase().replace(" ", "."));
		out.add("admin."+in.toLowerCase().replace(" ", "."));
		return out;
	}
	
	
	@Override
	public String getUserSearchFilter(String username, String cn, String search, LdapUser user) {
		List<String> conditions = new ArrayList<>();
		conditions.add("(&");
		conditions.add("(objectClass=inetOrgPerson)");
		if(!StringUtils.isEmpty(username)) {
			conditions.add(String.format("(uid=%1$s)",LdapEncoder.escapeLDAPSearchFilter(username)));
		}
		if(!StringUtils.isEmpty(cn)) {
			conditions.add(String.format("(cn=%1$s)",LdapEncoder.escapeLDAPSearchFilter(cn)));
		}
		if(!StringUtils.isEmpty(search)) {
			conditions.add(String.format("(|(uid=%1$s*)(mail=%1$s*)(entryUUID=%1$s*)(displayName=%1$s*))",search));
		}
		if(user!=null) {
			List<String> uc = new ArrayList<>();
			uc.add("(|");
			if(!StringUtils.isEmpty(user.getCn())) {
				for(String s : transformNames(user.getCn())) {
					uc.add(String.format("(vebLoginName=%1$s)",s));
				}
			}
			uc.add(")");
			conditions.add(StringUtils.join(uc,""));
		}
		conditions.add(")");
		return StringUtils.join(conditions,"");
	}
	
	public VebConnection(LdapConfig config, String password, List<LdapCustomsFieldConfig> fields, MailService mailService) {
		super(config, password, fields, mailService);
	}

	@Override
	public LdapUser getUser(LdapUser ou, SearchResult r) throws NamingException {
		super.getUser(ou, r);
    	ou.setUsername(getAttribute(r, "vebLoginName",""));
    	ou.setAccountName(getAttribute(r, "uid",""));
    	return ou;
	}
	
	
}
