package com.autonubil.identity.certs.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.certs.api.CertificateService;
import com.autonubil.identity.certs.api.entities.CertificateInformation;

@RestController
@RequestMapping(value="/api/certs/certs")
public class CertificateController {

	@Autowired
	private CertificateService certificateService;
	
	@RequestMapping(value="/api/certs/certs",method=RequestMethod.GET)
	public List<CertificateInformation> list() {
		return certificateService.list();
	}
	
	@RequestMapping(value="/api/certs/certs/{id}",method=RequestMethod.GET)
	public CertificateInformation get(@PathVariable String id) {
		return certificateService.get(id);
	}
	
	
	
}
