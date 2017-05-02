package com.autonubil.identity.auth.api.util;

import java.util.List;

import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.exceptions.NotAuthenticatedException;
import com.autonubil.identity.auth.api.exceptions.NotAuthorizedException;

public class AuthUtils {

	public static boolean isLoggedIn() {
		return IdentityHolder.get()!=null;
	}
	
	public static void checkLoggedIn() throws AuthException {
		if(!isLoggedIn()) {
			throw new NotAuthenticatedException();
		}
	}
	
	public static boolean hasAnyRole(String source, String... roles) {
		if(!isLoggedIn()) {
			return false;
		}
		if(IdentityHolder.get().getUser().getSourceId()==null || IdentityHolder.get().getUser().getSourceId().compareTo(source)!=0) {
			return false;
		}
		List<Group> groups = IdentityHolder.get().getUser().getGroups();
		if(groups==null || groups.size()==0) {
			return false;
		}
		for(Group g : groups) {
			for(String r : roles) {
				if(g.getDisplayName().compareTo(r)==0) return true;
			}
		}
		return false;
	}
	
	public static boolean isAdmin() {
		if(IdentityHolder.get()!=null && IdentityHolder.get().getUser()!=null && IdentityHolder.get().getUser().isAdmin()) {
			return true;
		}
		return false;
	}

	public static void checkAdmin() throws AuthException {
		if(!isLoggedIn()) {
			throw new NotAuthenticatedException();
		}
		if(!isAdmin()) {
			throw new NotAuthorizedException();
		}
	}
	
	
	public static void checkAnyRole(String source, String... roles) throws AuthException {
		if(!isLoggedIn()) {
			throw new NotAuthenticatedException();
		}
		if(!hasAnyRole(source, roles)) {
			throw new NotAuthorizedException();
		}
	}
	
}
