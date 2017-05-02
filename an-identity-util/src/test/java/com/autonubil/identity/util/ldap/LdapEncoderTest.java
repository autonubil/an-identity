package com.autonubil.identity.util.ldap;

import javax.naming.InvalidNameException;

import org.junit.Assert;
import org.junit.Test;


public class LdapEncoderTest {

	@Test
	public void testExtractDomain() throws InvalidNameException {
		String x = "cn=hase,dc=hund,dc=tiere,dc=alle";
		String y = LdapEncoder.getDomainComponents(x);
		Assert.assertEquals("hund.tiere.alle", y);
	}
	
	@Test
	public void testEscapeDn() {
		String x = "cn=Hund/Katze,dc=hasenkatze";
		String y = LdapEncoder.escapeDn(x);
		Assert.assertEquals("cn=Hund\\/Katze,dc=hasenkatze", y);
	}
	
	
}
