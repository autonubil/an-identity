package com.autonubil.identity.persistence.api.filter;

import java.util.List;

import com.autonubil.identity.persistence.api.filter.impl.AbstractFieldMatchingFilter;

public class BasicFilterBuilder {
	
	public <T> Filter<T> equals(Class<T> clazz, String fieldname, String value) {
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

	public <T> Filter<T> startsWith(Class<T> clazz, String fieldname, String value) {
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
	
	
}
