package com.autonubil.identity.ldap.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_IMPLEMENTED)
public class UnsupportedOperation extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedOperation() {
		super();
	}

	public UnsupportedOperation(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedOperation(String message) {
		super(message);
	}
	
}
