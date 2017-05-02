package com.autonubil.identity.auth.impl.util;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

@Component
public class Logger {

	private static Log log = LogFactory.getLog(Logger.class);
	
	@Autowired
	private List<AuthenticationManager> authenticationManagers;

	@PostConstruct
	public void init() {
		for(AuthenticationManager am : authenticationManagers) {
			log.info(" ---- "+am.getClass()+" - "+am.toString());
		}
		
	}
	
	
}
