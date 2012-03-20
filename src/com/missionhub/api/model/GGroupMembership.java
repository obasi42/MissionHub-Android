package com.missionhub.api.model;

public class GGroupMembership {

	private long group_id;
	private String name;
	private String role;

	public long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(final long group_id) {
		this.group_id = group_id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

}