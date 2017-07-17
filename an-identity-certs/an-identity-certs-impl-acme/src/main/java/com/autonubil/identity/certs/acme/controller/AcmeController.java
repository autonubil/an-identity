package com.autonubil.identity.certs.acme.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/")
public class AcmeController {

	private static Log log = LogFactory.getLog(AcmeController.class);
	
	@RequestMapping(value = "/.well-known/acme-challenge/{token}", method = { RequestMethod.GET })
	public String webfinger(HttpServletRequest request,
			@PathVariable("token") String token)   {
	
		log.info("ACME Chellenge for token " + token);
//		String token = challenge.getToken();
	//	String content = challenge.getAuthorization();
		return null;
	}
}
