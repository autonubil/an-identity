package com.autonubil.identity.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	
	@RequestMapping(value="/modules",method=RequestMethod.GET)
	@ResponseBody
	public String getModules() {
		return modules;
	}

	@RequestMapping(value="/templates",method=RequestMethod.GET)
	@ResponseBody
	public String getTemplates() {
		return templates;
	}
	
	@PostConstruct
	public void init() throws UnsupportedEncodingException {
		
		ExecutorService e = Executors.newScheduledThreadPool(1);

		ConcatenateProcessor modules = new ConcatenateProcessor();
		ConcatenateProcessor templates = new ConcatenateProcessor();
		
		FastClasspathScanner fcs = new FastClasspathScanner();
		fcs.matchFilenamePattern(".*[\\/]module.js", modules); 
		fcs.matchFilenamePattern(".*[\\/]templates.js", templates); 
		fcs.scan();
		
		this.modules = new String(modules.getBytes(),"utf-8");
		this.templates = new String(templates.getBytes(),"utf-8");
		
	}
	
	private class ConcatenateProcessor implements FileMatchContentsProcessorWithContext {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		@Override
		public void processMatch(File classpathElt, String relativePath, byte[] fileContents) throws IOException {
			log.info(" -----> "+classpathElt.getAbsolutePath()+":"+relativePath);
			baos.write(fileContents);
		}
		
		public byte[] getBytes() {
			return baos.toByteArray();
		}
		
	}
	
	
}
