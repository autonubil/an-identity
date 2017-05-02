package com.autonubil.identity.less.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.autonubil.identity.less.services.CssConfigService;

@Controller
public class IndexController {

	private static final Log log = LogFactory.getLog(IndexController.class);
	
	@Autowired
	private CssConfigService cssConfigService; 
	
	@RequestMapping("/")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("css", cssConfigService.list(null));
		return mav;
	}
	
	
}
