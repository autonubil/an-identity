package com.autonubil.identity.ldapotp.impl.factories.ipa;

import org.springframework.stereotype.Component;

import com.autonubil.identity.ldapotp.api.LdapOtpAdapter;
import com.autonubil.identity.ldapotp.impl.factories.AbstractLdapOtpAdapterFactory;

@Component
public class IpaOtpTokenAdapterFactory extends AbstractLdapOtpAdapterFactory {

	@Override
	public String[] getSupported() {
		return new String [] { "IPA" };
	}

	@Override
	public Class<? extends LdapOtpAdapter> getAdapterClass() {
		return IpaOtpTokenAdapter.class;
	}

	
	
}
