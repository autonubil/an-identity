package com.autonubil.identity.persistence.impl;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.autonubil.identity.persistence.api.PersistenceService;

@Service
@PropertySource(value="persistence.properties")
public class BasicPersistenceService implements PersistenceService {

	@Value("${persistence.basepath}")
	private String basePath;
	
	@Override
	public <T> List<T> list(String component, Class<T> clazz, String jsonPath) {
		return null;
	}

	@Override
	public <T> T get(String component, Class<T> clazz, String id) {
		return null;
	}

	@Override
	public <T> T save(String component, T object) {
		return null;
	}

	@Override
	public void delete(String component, String id) {
	}

	@PostConstruct
	public void init() {
		File f = new File(getBasePath());
		if(!f.exists()) {
			f.mkdirs();
		}
		
		
		
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	
}
