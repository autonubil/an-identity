package com.autonubil.identity.auth.api.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

public class RestAuthClientFactory {

	private static final Log log = LogFactory.getLog(RestAuthClientFactory.class);
	
	public RestAuthClient restAuthClient(
				@Value("an.identity.server.url") String url
			) {
		if(url=="") {
			log.info("NOT instantiating rest client for url: "+url);
			return null;
		}
		log.info("instantiating rest client for url: "+url);
		return new RestAuthClient();
	}
	
}
