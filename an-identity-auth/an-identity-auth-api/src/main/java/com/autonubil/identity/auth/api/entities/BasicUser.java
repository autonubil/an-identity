package com.autonubil.identity.auth.api.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicUser implements User {

	private String id;
	private String sourceId;
	private String sourceName;
	private String username;
	private String displayName;
	private boolean admin = false;
	private List<Group> groups = new ArrayList<>();
	private List<Notification> notifications = new ArrayList<>();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see com.autonubil.identity.auth.api.entities.User#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/* (non-Javadoc)
	 * @see com.autonubil.identity.auth.api.entities.User#getGroups()
	 */
	@Override
	public List<Group> getGroups() {
		return Collections.unmodifiableList(groups);
	}

	public void setGroups(List<Group> groups) {
		this.groups.clear();
		if (groups != null) {
			this.groups.addAll(groups);
		}
	}

	public void addGroup(Group group) {
		this.groups.add(group);

	}

	/* (non-Javadoc)
	 * @see com.autonubil.identity.auth.api.entities.User#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.autonubil.identity.auth.api.entities.User#getSourceId()
	 */
	@Override
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<Notification> getNotifications() {
		return new ArrayList<>(notifications);
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications.clear();
		if(notifications!=null) {
			this.notifications.addAll(notifications);
		}
	}
	
	public void addNotification(Notification notification) {
		this.notifications.add(notification);
	}

}
