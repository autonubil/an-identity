package com.autonubil.identity.audit.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Log4jAuditLogger implements AuditLogger {

	private Map<String,Log> loggers = new HashMap<>();
	
	
	@Override
	public void log(String component, String type, String sessionId, String remote, String identity, String action) {
		
		String key = component+"."+type;
		Log l = loggers.get(key);
		if(l == null) {
			l = LogFactory.getLog(key);
			loggers.put(key, l);
		}
		l.info(sessionId+", "+remote+", "+identity+", "+action);
	}

}
