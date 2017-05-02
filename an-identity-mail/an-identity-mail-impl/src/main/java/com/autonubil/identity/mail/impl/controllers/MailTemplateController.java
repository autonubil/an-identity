package com.autonubil.identity.mail.impl.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.auth.api.exceptions.AuthException;
import com.autonubil.identity.auth.api.util.AuthUtils;
import com.autonubil.identity.mail.api.MailException;
import com.autonubil.identity.mail.api.MailService;
import com.autonubil.identity.mail.api.MailServiceFactory;
import com.autonubil.identity.mail.impl.entities.Mail;
import com.autonubil.identity.mail.impl.entities.MailTemplate;
import com.autonubil.identity.mail.impl.services.MailTemplateService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/autonubil")
public class MailTemplateController {
	
	@Autowired
	private MailTemplateService mailTemplateService;
	
	@Autowired
	private MailServiceFactory mailServiceFactory;
	
	@RequestMapping(value="/api/mail/templates",method=RequestMethod.GET)
	public List<MailTemplate> list(
			@RequestParam(required=false) String module, 
			@RequestParam(required=false) String name, 
			@RequestParam(required=false) String locale,
			@RequestParam(required=false) String search,
			@RequestParam(required=false,defaultValue="0") int offset,
			@RequestParam(required=false,defaultValue="25") int max
		) throws AuthException {
		AuthUtils.checkAdmin();
		
		return mailTemplateService.list(null,module,name,locale,search,offset,max);
	}
	
	@RequestMapping(value="/api/mail/templates/{id}",method=RequestMethod.GET)
	public MailTemplate get(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return mailTemplateService.get(id);
	}
	
	@RequestMapping(value="/api/mail/templates/{id}/example",method=RequestMethod.GET)
	public Mail renderExample(@PathVariable String id, @RequestParam String mailConfigId) throws Exception {
		AuthUtils.checkAdmin();
		return mailTemplateService.renderExample(id,mailConfigId);
	}
	
	
	@RequestMapping(value="/api/mail/templates",method=RequestMethod.POST)
	public MailTemplate create(@PathVariable String id, @RequestBody MailTemplate mailTemplate) throws AuthException {
		AuthUtils.checkAdmin();
		mailTemplate.setId(null);
		return mailTemplateService.save(mailTemplate);
	}
	
	@RequestMapping(value="/api/mail/templates/{id}",method=RequestMethod.PUT)
	public MailTemplate save(@PathVariable String id, @RequestBody MailTemplate mailTemplate) throws AuthException {
		AuthUtils.checkAdmin();
		mailTemplate.setId(id);
		return mailTemplateService.save(mailTemplate);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/mail/templates/{id}",method=RequestMethod.POST)
	public void sendTest(@PathVariable String id, @RequestParam String configId, @RequestParam String recipient) throws AuthException, JsonParseException, JsonMappingException, MailException, IOException {
		AuthUtils.checkAdmin();
		MailTemplate mt = get(id);
		MailService ms = mailServiceFactory.getMailService(configId,null,null);
		Map<String,Object> params = new ObjectMapper().readValue(mt.getModel(),new TypeReference<Map<String,Object>>() {}); 
		ms.sendMail(
				recipient, 
				mt.getModule(), 
				mt.getName(), 
				mt.getLocale(), 
				params
				);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/mail/templates/{id}",method=RequestMethod.DELETE)
	public void delete(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		mailTemplateService.delete(id);
	}
	
	@RequestMapping(value="/api/mail/template_locales",method=RequestMethod.GET)
	public List<String> listModules() throws AuthException {
		AuthUtils.checkAdmin();
		return mailTemplateService.getLanguages();
	}
	

	@RequestMapping(value="/api/mail/template_modules",method=RequestMethod.GET)
	public List<String> listLanguages() throws AuthException {
		AuthUtils.checkAdmin();
		return mailTemplateService.getModules();
	}
	
	
}
