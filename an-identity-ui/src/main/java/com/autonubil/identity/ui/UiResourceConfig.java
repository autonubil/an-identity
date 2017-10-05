package com.autonubil.identity.ui;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class UiResourceConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/dist/**").addResourceLocations("classpath:/public/dist/");
	    registry.addResourceHandler("/js/**").addResourceLocations("classpath:/public/dist/js/");
	    registry.addResourceHandler("/node_modules/**").addResourceLocations("classpath:/public/dist/");
	    registry.addResourceHandler("/js/node_modules/**").addResourceLocations("classpath:/public/dist/");
	    registry.addResourceHandler("/dist/node_modules/**").addResourceLocations("classpath:/public/dist/");
	}
	
	
}
