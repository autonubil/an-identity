package com.autonubil.identity.auth.api.exceptions;

public class AuthException extends Exception {

	private static final long serialVersionUID = 8732326092643443533L;

	public AuthException() {
		super();
	}

	public AuthException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthException(String message) {
		super(message);
	}

	public AuthException(Throwable cause) {
		super(cause);
	}

}
