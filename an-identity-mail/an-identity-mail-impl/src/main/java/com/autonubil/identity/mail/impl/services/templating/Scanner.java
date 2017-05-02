package com.autonubil.identity.mail.impl.services.templating;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autonubil.identity.mail.impl.entities.MailTemplate;
import com.autonubil.identity.mail.impl.services.MailTemplateService;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessor;

@Component
public class Scanner {
	
	@Autowired
	private MailTemplateService mailTemplateService;
	
	
	private static Log log = LogFactory.getLog(Scanner.class);

	@PostConstruct
	public void scanForMails() {
		
		ExecutorService e = Executors.newScheduledThreadPool(1);

		e.submit(new Runnable() {
			
			public void run() {
				log.info("SCANNING FOR MAILS >>> START");
				FastClasspathScanner fcs = new FastClasspathScanner();
				try {
					
					final Map<String,String> models = new HashMap<>();
					final Map<String,MailTemplate> mails = new HashMap<>();
					
					log.info("SCANNING FOR MAILS >>> VERBOSE");
					
					log.info("SCANNING FOR MAILS >>> MATCH model.json");
					fcs.matchFilenamePattern("mails/.*/model.json", new FileMatchContentsProcessor() {
						@Override
						public void processMatch(String relativePath, byte[] fileContents) throws IOException {
							try {
								File f = new File(relativePath);
								
								String module = "default";
								
								String model = new String(fileContents,"utf-8");
								String name = f.getParentFile().getName();
								if(name.indexOf(".") > -1 || name.indexOf("_")>-1) {
									String[] x = name.split("[\\.]");
									module = x[0];
									name = x[1];
								}
								models.put(module+":"+name, model);
							} catch (Exception e2) {
								log.error("error processing mail descriptor: "+relativePath,e2);
							}
						}
					});

					log.info("SCANNING FOR MAILS >>> MATCH Velocity templates");
					fcs.matchFilenamePattern("mails/.*/[A-Z]{2,3}_[A-Z]{2,3}/(subject|text|html).vm", new FileMatchContentsProcessor() {
						@Override
						public void processMatch(String relativePath, byte[] fileContents) throws IOException {
							try {
								
								File f = new File(relativePath);
								String locale = f.getParentFile().getName();
								String module = "default";
								String name = f.getParentFile().getParentFile().getName();
								if(name.indexOf(".") > -1 || name.indexOf("_")>-1) {
									String[] x = name.split("[\\.]");
									module = x[0];
									name = x[1];
								}
								String keyModel = module+":"+name+":"+locale;
								String key = module+":"+name+":"+locale;
								MailTemplate m = mails.get(key);
								m = m==null?new MailTemplate():m;
								m.setName(name);
								m.setModule(module);
								mails.put(key, m);
								m.setLocale(locale);
								if(relativePath.endsWith("subject.vm")) m.setSubject(new String(fileContents,"utf-8"));
								if(relativePath.endsWith("text.vm")) m.setText(new String(fileContents,"utf-8"));
								if(relativePath.endsWith("html.vm")) m.setHtml(new String(fileContents,"utf-8"));
								
							} catch (Exception e2) {
								log.error("error processing mail template: "+relativePath,e2);
							}
						}
					});
		
					log.info("SCANNING FOR MAILS >>> PERFORM SCAN ... ");
					fcs.scan();
					log.info("SCANNING FOR MAILS >>> PERFORM SCAN ... DONE!");
		
					int countNew = 0;
					
					for(Map.Entry<String,MailTemplate> mt : mails.entrySet()) {
						MailTemplate m = mt.getValue();
						m.setModel(models.get(m.getModule()+":"+m.getName()));
						List<MailTemplate> ms = mailTemplateService.list(null, m.getModule(), m.getName(), m.getLocale(), null, 0, 1);
						if(ms.isEmpty()) {
							countNew++;
							log.info("SCANNING FOR MAILS >>> new mail: "+m.getModule()+" | "+m.getName()+" | "+m.getLocale());
							mailTemplateService.save(mt.getValue());
						}
					}
					
					log.info("SCANNING FOR MAILS >>> found "+mails.size()+" mails, "+countNew+" new!");
					
				} catch (Exception e) {
					log.error("error scanning for mails: ",e);
					e.printStackTrace();
				}
			}
		});
	}
}
