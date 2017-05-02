package com.autonubil.identity.less.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.less.entities.CssLink;
import com.autonubil.identity.less.services.CssConfigService;
import com.github.sommeri.less4j.Less4jException;

@RestController
@RequestMapping("/autonubil")
public class CssController {

	@Autowired
	private CssConfigService cssConfigService; 
	
	@RequestMapping(value="/api/less/css",method=RequestMethod.GET)
	public List<CssLink> getCss() throws IOException, Less4jException {
		return cssConfigService.list(null);
	}
	
	@RequestMapping(value="/api/less/css",method=RequestMethod.POST)
	public CssLink create(@RequestBody CssLink link) throws IOException, Less4jException {
		link.setId(null);
		return cssConfigService.save(link);
	}
	
	@RequestMapping(value="/api/less/css/{id}",method=RequestMethod.GET)
	public CssLink getCss(@PathVariable String id) throws IOException, Less4jException {
		return cssConfigService.get(id);
	}
	
	@RequestMapping(value="/api/less/css/{id}",method=RequestMethod.PUT)
	public CssLink updateCss(@PathVariable String id, @RequestBody CssLink link) throws IOException, Less4jException {
		link.setId(id);
		return cssConfigService.save(link);
	}

	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	@RequestMapping(value="/api/less/css/{id}",method=RequestMethod.DELETE)
	public void delete(@PathVariable String id) throws IOException, Less4jException {
		cssConfigService.delete(id);
	}
	
}
