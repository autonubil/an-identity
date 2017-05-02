package com.autonubil.identity.mail.impl.services.templating;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.autonubil.identity.mail.impl.entities.Mail;
import com.autonubil.identity.mail.impl.entities.MailTemplate;

public class Renderer {
	
	private static VelocityEngine ve = new VelocityEngine();


	private static String runTemplate(Context ctx, String template) {
		if(template == null) {
			return null;
		}
		StringWriter w = new StringWriter();
		ve.evaluate(ctx, w, "", new StringReader(template));
		return w.toString();
	}
	
	
	public static Mail render(MailTemplate mailTemplate, Map<String, Object> params) {
		
		VelocityContext ctx = new VelocityContext();
		for(Map.Entry<String, Object> e : params.entrySet()) {
			ctx.put(e.getKey(), e.getValue());
		}
		
		Mail out = new Mail();
		
		out.setSubject(runTemplate(ctx, mailTemplate.getSubject()));
		out.setHtml(runTemplate(ctx, mailTemplate.getHtml()));
		out.setText(runTemplate(ctx, mailTemplate.getText()));

		return out;

	}
	
	

}
