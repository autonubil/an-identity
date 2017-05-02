package com.autonubil.identity.auth.api.entities;

public class BasicGroup implements Group {
	
	private String id;
	private String sourceId;
	private String sourceName;
	private String name;
	private String displayName;

	public BasicGroup() {
	}
	
	public BasicGroup(String sourceId, String sourceName, String name) {
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.name = name;
		this.displayName = name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public String getSourceName() {
		return sourceName;
	}

	@Override
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}


}
