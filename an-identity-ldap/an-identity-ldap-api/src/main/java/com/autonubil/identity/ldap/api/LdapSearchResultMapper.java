package com.autonubil.identity.ldap.api;

import javax.naming.directory.SearchResult;

public interface LdapSearchResultMapper<T> {

	public T map(SearchResult r);
	
}
