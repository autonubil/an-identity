package com.autonubil.identity.automigrate.entities;

public class AutomigrateLog {
	
	private String id;
	private String migrateId;
	private String fromGroup;
	private String userId;
	private boolean success;
	private String message;

	public AutomigrateLog() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMigrateId() {
		return migrateId;
	}
	public void setMigrateId(String migrateId) {
		this.migrateId = migrateId;
	}
	public String getFromGroup() {
		return fromGroup;
	}
	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
