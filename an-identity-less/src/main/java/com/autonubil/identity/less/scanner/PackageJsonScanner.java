package com.autonubil.identity.less.scanner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.FileMatchContentsProcessor;

@Component
public class PackageJsonScanner {
	
	
	private static Log log = LogFactory.getLog(PackageJsonScanner.class);

	@PostConstruct
	public void scanForMails() {
		
		ExecutorService e = Executors.newScheduledThreadPool(1);

		e.submit(new Runnable() {
			
			public void run() {
				
				log.info("finding package.json ... ");
				
				FastClasspathScanner fcs = new FastClasspathScanner();
				try {
					
					fcs.matchFilenamePattern(".*.public.package.json", new FileMatchContentsProcessor() {
						@Override
						public void processMatch(String relativePath, byte[] fileContents) throws IOException {
							try {
								File f = new File(relativePath);
								System.err.println(f.getAbsolutePath());
								String module = "default";
								
							} catch (Exception e2) {
								log.error("error finding package json: "+relativePath,e2);
							}
						}
					});
					
					fcs.scan();

					
				} catch (Exception e) {
					log.error("error scanning for mails: ",e);
					e.printStackTrace();
				}
			}
		});
	}
}
