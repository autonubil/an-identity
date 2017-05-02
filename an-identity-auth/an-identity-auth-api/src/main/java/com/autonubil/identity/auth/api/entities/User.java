package com.autonubil.identity.auth.api.entities;

import java.util.List;

public interface User {

	String getDisplayName();

	String getId();

	String getSourceId();

	void setGroups(List<Group> groups);

	List<Group> getGroups();

	boolean isAdmin();

	void setSourceName(String sourceName);

	String getSourceName();

	public List<Notification> getNotifications();
	
}