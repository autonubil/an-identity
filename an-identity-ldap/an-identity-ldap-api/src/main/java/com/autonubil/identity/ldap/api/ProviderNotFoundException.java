package com.autonubil.identity.ldap.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class ProviderNotFoundException extends UnsupportedOperation {

	private static final long serialVersionUID = 1L;

	public ProviderNotFoundException() {
		super();
	}

	public ProviderNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProviderNotFoundException(String message) {
		super(message);
	}
	
}
