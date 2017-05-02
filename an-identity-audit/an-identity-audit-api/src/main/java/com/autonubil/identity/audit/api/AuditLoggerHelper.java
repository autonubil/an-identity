package com.autonubil.identity.audit.api;

public class AuditLoggerHelper {
	
	private static AuditLogger auditLogger = new Log4jAuditLogger();

	public static void log(String component, String type, String sessionId, String remote, String identity, String action) {
		if(auditLogger == null) {
			auditLogger = new Log4jAuditLogger();
		}
		auditLogger.log(component, type, sessionId, remote, identity, action);
	}
	
	public static void setInstance(AuditLogger auditLogger) {
		AuditLoggerHelper.auditLogger = auditLogger; 
	}
	
	
}
