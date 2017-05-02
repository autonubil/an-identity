package com.autonubil.identity.audit.api;

public interface AuditLogger {

	public void log(String component, String type, String sessionId, String remote, String identity, String action);
	
}
