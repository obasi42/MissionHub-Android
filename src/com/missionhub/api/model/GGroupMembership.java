package com.missionhub.api.model;

public class GGroupMembership {

	private int group_id;
	private String name;
	private String role;

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(final int group_id) {
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