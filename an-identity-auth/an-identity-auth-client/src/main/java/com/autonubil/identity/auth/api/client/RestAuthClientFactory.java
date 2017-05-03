package com.autonubil.identity.auth.api.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class RestAuthClientFactory {

	private static final Log log = LogFactory.getLog(RestAuthClientFactory.class);
	
	
	@ConditionalOnProperty(matchIfMissing=false,name="an.identity.server.host")
	public RestAuthClient restAuthClient(
				@Value("an.identity.server.host") String host,
				@Value("an.identity.server.port") int port
			) {
		
		log.info("instantiating rest client .... ");
		return new RestAuthClient();
	}
	
}
