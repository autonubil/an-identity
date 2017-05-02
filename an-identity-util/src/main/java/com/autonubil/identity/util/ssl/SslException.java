package com.autonubil.identity.util.ssl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.BAD_REQUEST)
public class SslException extends RuntimeException {

	private static final long serialVersionUID = -1646557840819333612L;
	private String[] pems;
	
	public SslException(String message, String[] pems) {
		super(message);
		this.setPems(pems);
	}

	public String[] getPems() {
		return pems;
	}

	public void setPems(String[] pems) {
		this.pems = pems;
	}
	
}
