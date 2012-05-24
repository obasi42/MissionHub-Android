package com.missionhub.android.api.old.model;

public class GGroupLabel {

	private long id;
	private String name;
	private long organization_id;
	private String ancestry;
	private String created_at;
	private String updated_at;
	private int group_labelings_count;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(final long organization_id) {
		this.organization_id = organization_id;
	}

	public String getAncestry() {
		return ancestry;
	}

	public void setAncestry(final String ancestry) {
		this.ancestry = ancestry;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(final String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(final String updated_at) {
		this.updated_at = updated_at;
	}

	public int getGroup_labelings_count() {
		return group_labelings_count;
	}

	public void setGroup_labelings_count(final int group_labelings_count) {
		this.group_labelings_count = group_labelings_count;
	}
}