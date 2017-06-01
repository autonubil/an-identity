package com.autonubil.identity.persistence.api.filter;

import java.util.List;

import com.autonubil.identity.persistence.api.filter.impl.AbstractFieldMatchingFilter;

public class BasicFilterBuilder<T> {
	
	private Class<T> clazz;
	
	public BasicFilterBuilder(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public Filter<T> equals(String fieldname, String value) {
		return new AbstractFieldMatchingFilter<T>(clazz,fieldname,value) {
			@Override
			protected boolean matchesImpl(List<Object> compare) {
				for(Object o : compare) {
					if((o+"").compareTo(compareTo+"")==0) {
						return true;
					}
				} 
				return false;
			}
		};
	}

	public Filter<T> startsWith(String fieldname, String value) {
		return new AbstractFieldMatchingFilter<T>(clazz,fieldname,value) {
			@Override
			protected boolean matchesImpl(List<Object> compare) {
				for(Object o : compare) {
					if((o+"").startsWith(compareTo+"")) return true;
				} 
				return false;
			}
		};
	}
	
	public static <T> BasicFilterBuilder<T> create(Class<T> clazz) {
		return new BasicFilterBuilder<T>(clazz);
	}
	
	
}
