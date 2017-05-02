package com.autonubil.identity.auth.api.entities;

public class Notification {

	public enum LEVEL {
		DEBUG, INFO, WARN, ERROR
	};

	private LEVEL level;
	private String message;
	
	public Notification() {
	}
	
	public Notification(LEVEL level, String message) {
		super();
		this.level = level;
		this.message = message;
	}

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
