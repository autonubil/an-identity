package com.autonubil.identity.auth.api.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Identity;

public class RestAuthentication implements Authentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8920408288569890183L;
	private Identity identity;
	
	public RestAuthentication(Identity identity) {
		this.identity = identity;
	}
	
	@Override
	public String getName() {
		return identity.getUser().getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> out = new ArrayList<>(); 
		for(Group g : identity.getUser().getGroups()) {
			out.add(new SimpleGrantedAuthority(g.getDisplayName()));
		}
		if(identity.getUser().isAdmin()) {
			out.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		out.add(new SimpleGrantedAuthority("ROLE_USER"));
		return out;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return identity.getUser();
	}

	@Override
	public Object getPrincipal() {
		return identity.getUser().getUsername();
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
	}

}
