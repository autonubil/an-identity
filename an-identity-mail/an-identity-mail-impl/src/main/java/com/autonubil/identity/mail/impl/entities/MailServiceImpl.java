package com.autonubil.identity.mail.impl.entities;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autonubil.identity.mail.api.MailException;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.mail.impl.entities.MailConfig.ENCRYPTION;
import com.autonubil.identity.mail.impl.services.MailTemplateService;
import com.autonubil.identity.mail.impl.services.templating.Renderer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MailServiceImpl implements MailService {

	private static Log log = LogFactory.getLog(MailServiceImpl.class); 
	
	private MailConfig mc;
	private String password;
	
	private MailTemplateService mailTemplateService;
	
	public MailServiceImpl(MailConfig mc, String password, MailTemplateService mailTemplateService) {
		this.mc = mc;
		this.password = password;
		this.mailTemplateService = mailTemplateService;
	}
	
	public Session createSession() {
		Properties p = new Properties();
		p.put("mail.smtp.port", mc.getPort());		
		p.put("mail.smtp.port", mc.getPort());		
		p.put("mail.smtp.host", mc.getHost());		
		p.put("mail.transport.protocol", "smtp");
		if(mc.getEncryption() == ENCRYPTION.START_TLS) { 
			p.put("mail.smtp.starttls.enable", "true");
		} else if (mc.getEncryption() == ENCRYPTION.TLS) {
			p.put("mail.transport.protocol", "smtps");
			p.put("mail.smtp.ssl.enable",true);
			p.put("mail.transport.protocol", "smtps");
			p.put("mail.smtps.port", mc.getPort());		
			p.put("mail.smtps.port", mc.getPort());		
			p.put("mail.smtps.host", mc.getHost());		
		}
		
		if(mc.isAuth()) {
			p.put("mail.smtp.auth", mc.isAuth());
			p.put("mail.smtps.auth", mc.isAuth());
			return Session.getInstance(p,new SMTPAuthenticator(mc.getUsername(), password));
		} else {
			return Session.getInstance(p);
		}
	}
	
	
	@Override
	public void sendMail(String recipient, String module, String mail, String locale, Map<String, Object> params) throws MailException {
		
		try {
			log.info(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(params));
		} catch (Exception e) {
		}

		try {
			
			locale = locale==null?"XX_XX":locale;
			
			MailTemplate templ = null;
			
			List<MailTemplate> candidates = mailTemplateService.list(null, module, mail, null, null, 0, 1000); 
			
			for(MailTemplate x: candidates) {
				if(x.getLocale().compareToIgnoreCase(locale)==0) {
					templ = x;
				} else if (x.getLocale().equalsIgnoreCase("XX_XX") && templ==null) {
					templ = x;
				}
			}
			
			if(templ == null) {
				log.info("unable to find a matching template: "+module+" : "+mail+" : "+locale);
				return;
			}
			
			params.putAll(mc.getParams());
			
			Mail m = Renderer.render(templ, params);
			
			
			Session s = createSession();
			MimeMessage msg = new MimeMessage(s);
			msg.setFrom(InternetAddress.parse(mc.getSender())[0]);
			msg.setSender(InternetAddress.parse(mc.getSender())[0]);
			msg.setSubject(m.getSubject());
			msg.setContent(build(m.getText(),m.getHtml()));
			msg.addRecipient(RecipientType.TO, InternetAddress.parse(recipient)[0]);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			msg.writeTo(baos);
			
			log.debug(new String(baos.toByteArray(),"utf-8"));
			
			Transport t = s.getTransport();
			
			t.connect();
			t.sendMessage(msg, msg.getAllRecipients());
			
			
		} catch (Exception e) {
			log.error("error sending mail" ,e);
			try {
				log.error("mailconfig: "+(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(mc)));
				log.error("mailconfig: [PASSWORD="+(password==null?"NO":"YES")+"]");
			} catch (JsonProcessingException e1) {
			}
			throw new MailException("error sending mail" ,e);
		}
	}
	
	
	public Multipart build(String messageText, String messageHtml) throws MessagingException {
        Multipart mpMixed = new MimeMultipart("mixed");
        {
            // alternative
            Multipart mpMixedAlternative = newChild(mpMixed, "alternative");
            {
                // Note: MUST RENDER HTML LAST otherwise iPad mail client only renders the last image and no email
            	if(messageText!=null) {
            		addTextVersion(mpMixedAlternative,messageText);
            	}
            	if(messageHtml!=null) {
            		addHtmlVersion(mpMixedAlternative,messageHtml);
            	}
            }
        }

        return mpMixed;
    }

    private Multipart newChild(Multipart parent, String alternative) throws MessagingException {
        MimeMultipart child =  new MimeMultipart(alternative);
        final MimeBodyPart mbp = new MimeBodyPart();
        parent.addBodyPart(mbp);
        mbp.setContent(child);
        return child;
    }

    private void addTextVersion(Multipart mpRelatedAlternative, String messageText) throws MessagingException {
        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(messageText, "text/plain");
        mpRelatedAlternative.addBodyPart(textPart);
    }

    private void addHtmlVersion(Multipart parent, String messageHtml) throws MessagingException {
        final Multipart mpRelated = newChild(parent,"related");
        final MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(messageHtml, "text/html");
        mpRelated.addBodyPart(htmlPart);
    }
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		
		private String username;
		private String password;
		
		public SMTPAuthenticator(String username, String password) {
			this.username = username;
			this.password = password;
		}
		
        public PasswordAuthentication getPasswordAuthentication() {
           return new PasswordAuthentication(username, password);
        }
        
    }
	

}
