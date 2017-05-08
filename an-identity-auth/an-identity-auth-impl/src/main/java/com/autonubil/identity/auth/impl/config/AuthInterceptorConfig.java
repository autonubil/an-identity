package com.autonubil.identity.auth.impl.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.autonubil.identity.auth.impl.controllers.CookieAuthInterceptor;
import com.autonubil.identity.auth.impl.controllers.HeaderAuthInterceptor;

@Configuration
@PropertySource(value = { "auth.properties" })
public class AuthInterceptorConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private CookieAuthInterceptor cookieAuthInterceptor;
	
	@Autowired
	private HeaderAuthInterceptor headerAuthInterceptor;
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(cookieAuthInterceptor);
		registry.addInterceptor(headerAuthInterceptor);
		registry.addInterceptor(new HandlerInterceptorAdapter() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				response.setHeader("Cache-Control", "no-cache, must-revalidate");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "now");
				return super.preHandle(request, response, handler);
			}
			
		});
	}

}
