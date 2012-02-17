package com.missionhub.api.model;

public class GOrgGeneric {
	private Long org_id;
	private String name;
	private String primary;
	private String role;

	public Long getOrg_id() {
		return org_id;
	}

	public String getName() {
		return name;
	}

	public String getPrimary() {
		return primary;
	}

	public String getRole() {
		return role;
	}

	public void setOrg_id(final Long i) {
		org_id = i;
	}

	public void setName(final String s) {
		name = s;
	}

	public void setPrimary(final String s) {
		primary = s;
	}

	public void setRole(final String s) {
		role = s;
	}

}
