package com.autonubil.identity.persistence.api.filter.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.autonubil.identity.persistence.api.filter.Filter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractFieldMatchingFilter<T> implements Filter<T> {

	protected Class<T> clazz;
	protected String field;
	protected Object compareTo;
	protected List<Field> fields;
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public AbstractFieldMatchingFilter(Class<T> clazz, String field, Object compareTo) {
		this.clazz = clazz;
		this.field = field;
		this.compareTo = compareTo;
	}
	
	public List<Object> getFieldValues(T t) {
		try {
			return resolve(t,field);
		} catch (Exception e) {
		}
		return null;
		
	}
	
	public List<Object> resolve(Object ob, String f) throws IOException {
		List<Object> out = new ArrayList<>();
		
		if(ob instanceof Collection) {
			for(Object o : (Collection<?>)ob) {
				out.addAll(resolve(o, f));
			}
		} else {
			String x = objectMapper.writeValueAsString(ob);
			Map<String,Object> map = objectMapper.readValue(x, new TypeReference<Map<String,Object>>() {});
			
			String[] parts = f.split("\\.",2);

			if(map.get(parts[0])==null) {
				// nothing
			} else {
				if(parts.length>1) {
					out.addAll(resolve(map.get(parts[0]), f));
				} else {
					out.add(map.get(parts[0]));
				}
			}
		}
		return out;
	}

	protected abstract boolean matchesImpl(List<Object> compare);
	
	public boolean matches(T t) {
		return matchesImpl(getFieldValues(t));
	}
	
}
