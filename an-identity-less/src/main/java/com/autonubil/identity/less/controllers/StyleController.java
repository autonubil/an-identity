package com.autonubil.identity.less.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.autonubil.identity.less.services.LessConfigService;
import com.github.sommeri.less4j.Less4jException;

@RestController
@RequestMapping("/autonubil")
public class StyleController {

	@Autowired
	private LessConfigService lessConfigService; 
	
	@RequestMapping(value="/api/less/stylesheet",method=RequestMethod.GET,produces="text/css")
	public void getStyles(HttpServletResponse response) throws IOException, Less4jException {
		OutputStream os = response.getOutputStream();
		os.write(lessConfigService.getCss().getBytes("utf-8"));
		os.flush();
	}
	
	@RequestMapping(value="/api/less/bootstrap",method=RequestMethod.GET)
	public Map<String,String> getStyleConfig() throws IOException, Less4jException {
		return lessConfigService.getConfig();
	}
	
	@RequestMapping(value="/api/less/bootstrap",method=RequestMethod.PUT)
	public void setStyleConfig(@RequestBody Map<String,String> config) throws IOException, Less4jException {
		lessConfigService.setConfig(config);
	}
	
	@RequestMapping(value="/api/fonts/{font:.+}",method=RequestMethod.GET)
	public void getFont(@PathVariable("font") String font, HttpServletResponse response ) throws IOException, Less4jException {
		
		InputStream is = this.getClass().getResourceAsStream("/com/autonubil/intranet/fonts/" + font);
		if (is == null) {
			response.sendError(404);
			return;
		}
		
		OutputStream os = response.getOutputStream();
		IOUtils.copy(is, os);
		os.flush();
		is.close();
	}

	
}
