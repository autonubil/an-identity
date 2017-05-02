package com.autonubil.identity.util.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.lang3.StringUtils;

public class LdapEncoder {

	
	public static List<String> getRdnComponents(String name, String comp) throws InvalidNameException {
		
		List<String> cs = new ArrayList<>();
		
		LdapName dn = new LdapName(name);
		for(Rdn r : dn.getRdns()) {
			if(r.getType().compareToIgnoreCase(comp)==0) {
				cs.add(0,r.getValue().toString());
			}
		}
		return cs;
	}
	
	
	public static String getDomainComponents(String in) throws InvalidNameException {
		
		List<String> cs = new ArrayList<>();
		
		LdapName dn = new LdapName(in);
		for(Rdn r : dn.getRdns()) {
			if(r.getType().compareToIgnoreCase("dc")==0) {
				cs.add(0,r.getValue().toString());
			}
		}
		return StringUtils.join(cs,".");
	}
	
	
	public static Date parseLdapDate(Object ldapDate){
		
		if(ldapDate==null) {
			return null;
		}
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    try {
	    	String s = ldapDate.toString().replaceAll("Z", "");
	        Date d = sdf.parse(s);
	        return d;
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    return null;
	}	
	
	public static final String escapeLDAPSearchFilter(String filter) {
		StringBuilder sb = new StringBuilder();
	       for (int i = 0; i < filter.length(); i++) {
	           char curChar = filter.charAt(i);
	           switch (curChar) {
	               case '\\':
	                   sb.append("\\5c");
	                   break;
	               case '*':
	                   sb.append("\\2a");
	                   break;
	               case '(':
	                   sb.append("\\28");
	                   break;
	               case ')':
	                   sb.append("\\29");
	                   break;
	               case '\u0000': 
	                   sb.append("\\00"); 
	                   break;
	               default:
	                   sb.append(curChar);
	           }
	       }
	       return sb.toString();
	   }


	public static String escapeDn(String dn) {
		StringBuilder sb = new StringBuilder();
	       for (int i = 0; i < dn.length(); i++) {
	           char curChar = dn.charAt(i);
	           switch (curChar) {
	               case '/':
	                   sb.append("\\");
	               default:
	                   sb.append(curChar);
	           }
	       }
	       return sb.toString();
	}	
	

	
	
}
