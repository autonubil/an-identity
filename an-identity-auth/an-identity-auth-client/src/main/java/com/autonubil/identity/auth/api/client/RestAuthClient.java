package com.autonubil.identity.auth.api.client;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
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
	private ObjectMapper objectMapper;
	
	private String sessionId;
	
	public RestAuthClient(String url, String sourceId, String adminSourceId, String adminUser, String adminPassword) {
		this.url = url;
		this.sourceId = sourceId;
		this.adminSourceId = adminSourceId;
		this.adminUser = adminUser;
		this.adminPassword = adminPassword;
	}
	
	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
		objectMapper.registerModule(sm);

		
		this.restTemplate = new RestTemplate(Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper)));
		this.restTemplate.setInterceptors(
			Collections.singletonList(
				new ClientHttpRequestInterceptor() {
					@Override
					public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
						log.info("intercepting request. adding headers ... ");
						log.info(" ### user: "+adminUser);
						request.getHeaders().add("x-auth-user", adminUser);
						log.info(" ### password: "+adminPassword);
						request.getHeaders().add("x-auth-password", adminPassword);
						log.info(" ### source: "+adminSourceId);
						request.getHeaders().add("x-auth-source", adminSourceId);
						return execution.execute(request, body);
					}
				}
			)
		);
	}
	
	public Authentication authenticate(String username, String password) {
		RestTemplate restTemplate = new RestTemplate(Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper)));
		UsernamePasswordCredentials upc = new UsernamePasswordCredentials(sourceId, username, password);
		String v;
		String u = url+"/autonubil/api/authentication/authenticate";
		try {
			v = objectMapper.writeValueAsString(upc);
			log.info(u+" --- "+v);
		} catch (JsonProcessingException e) {
		}
		ResponseEntity<Identity> ri = restTemplate.postForEntity(u, upc, Identity.class);
		Identity i = ri.getBody();
		if(i!=null && i.getUser()!=null) {
			return new RestAuthentication(i);
		}
		return null;
	}
	
	
	public User getUser(String username) {
		log.info("get user by name: "+username);
		Map<String,Object> reqParams = new HashMap<>();
		reqParams.put("sourceId", sourceId);
		reqParams.put("username", username);
		ResponseEntity<User> s = restTemplate.getForEntity(url+"/autonubil/api/authentication/user?sourceId="+sourceId+"&username="+username, User.class, reqParams);
		return s.getBody();
	}
	
	
	public static void main(String[] args) {
		RestAuthClient rac = new RestAuthClient("http://127.0.0.1:9099", "5283290f-fe0e-4e1b-a607-b0bd4519babf", "LOCAL", "admin", "hundemund");
		rac.init();
		rac.getUser("rmalchow");
	}
	
	
}
