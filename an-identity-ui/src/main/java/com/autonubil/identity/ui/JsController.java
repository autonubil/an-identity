package com.autonubil.identity.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessorWithContext;

@Controller
@RequestMapping("/js")
public class JsController {

	private static Log log = LogFactory.getLog(ConcatenateProcessor.class);

	private String modules, templates;

	@Value("${ui.cacheJs}")
	private boolean cacheJs;
	
	
	@RequestMapping(value="/modules",method=RequestMethod.GET)
	@ResponseBody
	public String getModules() throws IOException {
		if(modules==null || !cacheJs) {
			log.info("reloading modules ... ");
			modules = concatenate(".*[\\/]module.js");
		}
		return modules;
	}
	
	@RequestMapping(value="/templates",method=RequestMethod.GET)
	@ResponseBody
	public String getTemplates() throws IOException {
		if(templates==null || !cacheJs) {
			log.info("reloading templates ... ");
			templates = concatenate(".*[\\/]templates.js");
		}
		return templates;
	}
	
	@PostConstruct
	public void init() throws IOException {

		getModules();
		getTemplates();
		
	}
	
	
	public String concatenate(String pattern) throws UnsupportedEncodingException {
		ConcatenateProcessor p = new ConcatenateProcessor();
		
		FastClasspathScanner fcs = new FastClasspathScanner();
		fcs.matchFilenamePattern(pattern, p); 
		fcs.scan();
		
		return new String(p.getBytes(),"utf-8");
	}
	
	private class ConcatenateProcessor implements FileMatchContentsProcessorWithContext {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		@Override
		public void processMatch(File classpathElt, String relativePath, byte[] fileContents) throws IOException {
			log.info(" -----> "+classpathElt.getAbsolutePath()+":"+relativePath);
			baos.write(("//  "+ relativePath+"\n").getBytes());
			baos.write(fileContents);
		}
		
		public byte[] getBytes() {
			try {
				baos.flush();
			} catch (IOException e) {
				log.warn("could not flush buffer", e);
			}
			return baos.toByteArray();
		}
		
	}
	
	
}
