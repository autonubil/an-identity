package com.autonubil.identity.auth.api.client;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import com.autonubil.identity.auth.api.entities.BasicGroup;
import com.autonubil.identity.auth.api.entities.BasicUser;
import com.autonubil.identity.auth.api.entities.Group;
import com.autonubil.identity.auth.api.entities.Identity;
import com.autonubil.identity.auth.api.entities.User;
import com.autonubil.identity.auth.api.entities.UsernamePasswordCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class RestAuthClient {

	private static final Log log = LogFactory.getLog(RestAuthClient.class);

	private String baseUrl;
	private String sourceId = "LOCAL";
	
	public RestAuthClient(String url) {
		this.baseUrl = url;
	}
	
	
	public Authentication authenticate(String username, String password) {
		ObjectMapper om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		SimpleModule sm = new SimpleModule();
		sm.addDeserializer(
			User.class, 
			new JsonDeserializer<User>() {
				public User deserialize(
						com.fasterxml.jackson.core.JsonParser parser, 
						com.fasterxml.jackson.databind.DeserializationContext ctx
				) throws java.io.IOException, JsonProcessingException {
					return parser.getCodec().readValue(parser, BasicUser.class);
				};
			}
		);
		sm.addDeserializer(
				Group.class, 
				new JsonDeserializer<Group>() {
					public Group deserialize(
							com.fasterxml.jackson.core.JsonParser parser, 
							com.fasterxml.jackson.databind.DeserializationContext ctx
							) throws java.io.IOException, JsonProcessingException {
						return parser.getCodec().readValue(parser, BasicGroup.class);
					};
				}
				);
		om.registerModule(sm);

		List<HttpMessageConverter<?>> converters = Collections.singletonList(new MappingJackson2HttpMessageConverter(om)); 
		
		RestTemplate temp = new RestTemplate(converters);
		
		UsernamePasswordCredentials upc = new UsernamePasswordCredentials(sourceId, username, password); 
		
		Identity i = temp.postForObject(baseUrl+"/autonubil/api/authentication/authenticate", upc, Identity.class);
		
		String s;
		try {
			s = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(i);
			log.info(s);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		if(i!=null && i.getUser()!=null) {
			return new RestAuthentication(i);
		}
		return null;

		
	}
	
	
	
}
