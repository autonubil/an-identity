package com.autonubil.identity.less.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.RequireJS;

@Controller
public class JsController {

	@ResponseBody
	@RequestMapping(value = "/all_modules", produces = "application/javascript")
	public String webjarjs() {
	    
	}
	
}
