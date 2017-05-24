package com.autonubil.identity.less.entities;

import java.util.HashMap;
import java.util.Map;

public class PackageJson {

	private String name;
	private Map<String, String> dependencies = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Map<String, String> dependencies) {
		this.dependencies = dependencies;
	}

}
