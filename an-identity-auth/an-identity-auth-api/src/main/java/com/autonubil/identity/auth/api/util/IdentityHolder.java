package com.autonubil.identity.auth.api.util;

import com.autonubil.identity.auth.api.entities.Identity;

public class IdentityHolder {
	
	private static ThreadLocal<Identity> holder = new ThreadLocal<>();
	
	public static Identity get() {
		return holder.get();
	}
	
	public static void set(Identity u) {
		holder.set(u);
	}

	public static void clear() {
		holder.remove();
	}
	
	
}
