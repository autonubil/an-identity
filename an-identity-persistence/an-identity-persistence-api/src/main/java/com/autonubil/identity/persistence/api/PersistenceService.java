package com.autonubil.identity.persistence.api;

import java.util.List;

public interface PersistenceService {

	public <T> List<T> getList(String component, String id, Class<T> clazz);

	public <T> T getOne(String component, String id, Class<T> clazz);
	
}
