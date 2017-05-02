package com.autonubil.identity.auth.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class AccountExpiredException extends AuthException {

	private static final long serialVersionUID = 3700760578448118033L;

	public AccountExpiredException() {
		super();
	}

	public AccountExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AccountExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountExpiredException(String message) {
		super(message);
	}

	public AccountExpiredException(Throwable cause) {
		super(cause);
	}

}
