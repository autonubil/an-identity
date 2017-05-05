package com.autonubil.identity.auth.api.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value="an.identity.auth.client.properties")
public class RestAuthClientFactory {

	private static final Log log = LogFactory.getLog(RestAuthClientFactory.class);
	
	@Bean
	@ConditionalOnProperty(havingValue="true",name="an.identity.client.enabled",matchIfMissing=false)
	public RestAuthClient restAuthClient(
			@Value("${an.identity.client.url}") String url,
			@Value("${an.identity.client.sourceId}") String sourceId,
			@Value("${an.identity.client.admin.sourceId}") String adminSourceId,
			@Value("${an.identity.client.admin.user}") String adminUser,
			@Value("${an.identity.client.admin.password}") String adminPassword
		) {
		log.info(" ######################################## ");
		log.info(" ## ");
		log.info(" ## Instantiating REST Authentication client with URL: "+url);
		log.info(" ## ");
		log.info(" ######################################## ");
		return new RestAuthClient(url,sourceId,adminSourceId, adminUser, adminPassword);
	}
	
}
