package com.autonubil.identity.mail.impl.controllers;

import java.util.List;

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
import com.autonubil.identity.mail.impl.entities.MailConfig;
import com.autonubil.identity.mail.impl.services.MailConfigService;

@RestController
@RequestMapping("/autonubil")
public class MailConfigController {
	
	@Autowired
	private MailConfigService mailConfigService;
	
	@RequestMapping(value="/api/mail/configs",method=RequestMethod.GET)
	public List<MailConfig> list() throws AuthException {
		AuthUtils.checkAdmin();
		return mailConfigService.list(null);
	}
	
	@RequestMapping(value="/api/mail/configs/{id}",method=RequestMethod.GET)
	public MailConfig get(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return mailConfigService.get(id);
	}
	
	@RequestMapping(value="/api/mail/configs/{id}",method=RequestMethod.PUT)
	public MailConfig save(@PathVariable String id, @RequestBody MailConfig mailConfig) throws AuthException {
		AuthUtils.checkAdmin();
		mailConfig.setId(id);
		return mailConfigService.save(mailConfig);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/mail/configs/{id}/password",method=RequestMethod.PUT)
	public void setPassword(@PathVariable String id, @RequestParam String password) throws AuthException {
		AuthUtils.checkAdmin();
		mailConfigService.setPassword(id, password);
	}
	
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/mail/configs/{id}",method=RequestMethod.DELETE)
	public void delete(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		mailConfigService.delete(id);
	}
	

}
