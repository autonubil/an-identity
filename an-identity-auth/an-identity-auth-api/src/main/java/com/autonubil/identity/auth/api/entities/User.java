package com.autonubil.identity.auth.api.entities;

import java.util.List;

public interface User {

	String getDisplayName();

	List<Group> getGroups();

	String getId();

	String getSourceId();

	String getSourceName();

	boolean isAdmin();

}