package com.autonubil.identity.mail.api;

public interface MailServiceFactory {
	
	public MailService getDefaultMailService();
	public MailService getMailService(String id, String name, String description);

}
