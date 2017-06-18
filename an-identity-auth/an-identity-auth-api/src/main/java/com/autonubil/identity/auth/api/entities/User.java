package com.autonubil.identity.auth.api.entities;

import java.util.ArrayList;
import java.util.List;

public interface User {

	String getDisplayName();

	String getUsername();
	
	List<Group> getGroups();

	String getId();

	String getSourceId();

	String getSourceName();

	boolean isAdmin();

	void addNotification(Notification notification);

	List<Notification> getNotifications();

}