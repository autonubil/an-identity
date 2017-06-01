package com.autonubil.identity.persistence.api;

import java.util.List;

public interface PersistenceService {

	public <T> List<T> list(String component, Class<T> clazz, String jsonPath);

	public <T> T get(String component, Class<T> clazz, String id);
	
	public <T> T save(String component, T object);
	
	public void delete(String component, String id);
	
}
