package com.autonubil.identity.auth.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PasswordExpiredException extends AuthException {

	private static final long serialVersionUID = 3700760578448118033L;

	public PasswordExpiredException() {
		super();
	}

	public PasswordExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PasswordExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordExpiredException(String message) {
		super(message);
	}

	public PasswordExpiredException(Throwable cause) {
		super(cause);
	}

}
