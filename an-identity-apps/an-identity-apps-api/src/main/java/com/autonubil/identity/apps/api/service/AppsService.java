package com.autonubil.identity.apps.api.service;

import java.util.List;

import com.autonubil.identity.apps.api.entities.App;
import com.autonubil.identity.auth.api.entities.Group;

public interface AppsService {

	App get(String id);
	List<App> listAppsForGroups(List<Group> groups, String search);

}