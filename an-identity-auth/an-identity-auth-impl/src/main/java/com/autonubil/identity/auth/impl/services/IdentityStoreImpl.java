package com.autonubil.identity.auth.impl.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.autonubil.identity.audit.api.AuditLogger;
import com.autonubil.identity.auth.api.IdentityStore;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.util.TokenGenerator;

@Service
public class IdentityStoreImpl implements IdentityStore {

	private Map<String,Identity> identities = new HashMap<>();
	
	public static final int LIFETIME = 60; 
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Override
	public Identity getIdentity(String token) {
		Identity i = identities.get(token);
		if(i==null) return null;
		if(i.getExpires()==null) return null;
		if(i.getExpires().before(new Date())) return null;
		return i;
	}

	@Override
	public void removeIdentity(String token) {
		identities.remove(token);
	}

	@Override
	public String putIdentity(Identity identity) {
		String x = TokenGenerator.getToken(128);
		identity.setExpires(new Date(System.currentTimeMillis()+(LIFETIME*1000)));
		identities.put(x, identity);
		return x;
	}

	@Scheduled(fixedDelay=10000)
	public void cleanup() {
		Date d = new Date();
		for(String key : new ArrayList<>(identities.keySet())) {
			Identity i = identities.get(key); 
			if(i!=null && i.getExpires().before(d)) {
				auditLogger.log("AUTH", "SESSION_CLEANUP", key, "[null]", i.getUser().getSourceName()+":"+i.getUser(), "Session cleaned up");
				identities.remove(key);
			}
		}
	}
	
	@Override
	public void update(String value, Identity identity) {
		if(identities.get(value)==null) return;
		identity.setExpires(new Date(System.currentTimeMillis()+(LIFETIME*1000)));
		identities.put(value, identity);
	}
	
}
