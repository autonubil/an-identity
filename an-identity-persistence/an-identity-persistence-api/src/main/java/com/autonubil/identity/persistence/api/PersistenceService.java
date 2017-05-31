package com.autonubil.identity.persistence.api;

import java.util.List;

import com.autonubil.identity.persistence.api.filter.Filter;

public interface PersistenceService {

	public <T> List<T> getList(String component, Class<T> clazz, Filter<T> filter);

	public <T> T getOne(String component, Class<T> clazz, String id);
	
}
