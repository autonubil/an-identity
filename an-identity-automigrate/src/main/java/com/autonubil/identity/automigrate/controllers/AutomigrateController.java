package com.autonubil.identity.automigrate.controllers;

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
import com.autonubil.identity.automigrate.entities.AutomigrateConfig;
import com.autonubil.identity.automigrate.services.AutomigrateConfigService;

@RestController
public class AutomigrateController {

	@Autowired
	private AutomigrateConfigService automigrateConfigService; 

	@RequestMapping(value="/api/automigrate/configs",method=RequestMethod.GET)
	public List<AutomigrateConfig> list(
			@RequestParam(required=false) String fromLdap,  
			@RequestParam(required=false) String toLdap,  
			@RequestParam(required=false,defaultValue="0") int offset,  
			@RequestParam(required=false,defaultValue="25") int max
		) throws AuthException {
		AuthUtils.checkAdmin();
		return automigrateConfigService.list(null, fromLdap, toLdap, offset, max);
	}
	
	@RequestMapping(value="/api/automigrate/configs",method=RequestMethod.POST)
	public AutomigrateConfig create(@RequestBody AutomigrateConfig config) throws AuthException {
		AuthUtils.checkAdmin();
		config.setId(null);
		return automigrateConfigService.save(config);
	}
	
	@RequestMapping(value="/api/automigrate/configs/{id}",method=RequestMethod.GET)
	public AutomigrateConfig list(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		return automigrateConfigService.get(id);
	}
	
	@RequestMapping(value="/api/automigrate/configs/{id}",method=RequestMethod.PUT)
	public AutomigrateConfig save(@PathVariable String id, @RequestBody AutomigrateConfig config) throws AuthException {
		AuthUtils.checkAdmin();
		config.setId(id);
		return automigrateConfigService.save(config);
	}

	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/automigrate/configs/{id}",method=RequestMethod.DELETE)
	public void delete(@PathVariable String id) throws AuthException {
		AuthUtils.checkAdmin();
		automigrateConfigService.delete(id);
	}
	
	
}
