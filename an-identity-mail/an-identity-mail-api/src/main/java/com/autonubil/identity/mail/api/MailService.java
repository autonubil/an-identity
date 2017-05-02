package com.autonubil.identity.mail.api;

import java.util.Map;

public interface MailService {
	
	public void sendMail(String recipient, String module, String mail, String locale, Map<String,Object> params) throws MailException;

}
