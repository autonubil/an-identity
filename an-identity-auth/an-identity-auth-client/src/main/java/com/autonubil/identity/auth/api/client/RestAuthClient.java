package com.autonubil.identity.auth.api.client;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
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
import com.fasterxml.jackson.databind.module.SimpleModule;

public class RestAuthClient {

	private static final Log log = LogFactory.getLog(RestAuthClient.class);

	private String url,sourceId,adminSourceId, adminUser, adminPassword;
	
	private RestTemplate restTemplate;
	private RestTemplate restTemplateAuthenticated;
	
	private String sessionId;
	
	public RestAuthClient(String url, String sourceId, String adminSourceId, String adminUser, String adminPassword) {
		this.url = url;
	}
	
	@PostConstruct
	public void init() {
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
		
		this.restTemplate = new RestTemplate(converters);
		this.restTemplateAuthenticated = new RestTemplate(converters);
		
		this.restTemplateAuthenticated.setInterceptors(
			Collections.singletonList(
				new ClientHttpRequestInterceptor() {
					@Override
					public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
						return execution.execute(request, body);
					}
				}
			)
		);

	}
	
	public Authentication authenticate(String username, String password) {
		UsernamePasswordCredentials upc = new UsernamePasswordCredentials(sourceId, username, password); 
		ResponseEntity<Identity> ri = restTemplate.postForEntity(url+"/autonubil/api/authentication/authenticate", upc, Identity.class);
		Identity i = ri.getBody();
		if(i!=null && i.getUser()!=null) {
			return new RestAuthentication(i);
		}
		return null;
	}
	
	
	public User getUser(String username) {
		Map<String,Object> reqParams = new HashMap<>();
		reqParams.put("sourceId", sourceId);
		reqParams.put("username", username);
		return restTemplate.getForObject(url+"/autonubil/api/authentication/user?sourceId="+sourceId+"&username="+username, User.class, reqParams);
	}
	
	
	
}
