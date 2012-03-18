package com.missionhub.api.model;

public class GGroup {

	public long id;
	public String name;
	public String location;
	public String meets;
	public int start_time;
	public int end_time;
	public long organization_id;
	public String created_at;
	public String updated_at;
	public boolean list_publicly;
	public boolean approve_join_requests;
	public GLabel[] labels;

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

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public String getMeets() {
		return meets;
	}

	public void setMeets(final String meets) {
		this.meets = meets;
	}

	public int getStart_time() {
		return start_time;
	}

	public void setStart_time(final int start_time) {
		this.start_time = start_time;
	}

	public int getEnd_time() {
		return end_time;
	}

	public void setEnd_time(final int end_time) {
		this.end_time = end_time;
	}

	public long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(final long organization_id) {
		this.organization_id = organization_id;
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

	public boolean isList_publicly() {
		return list_publicly;
	}

	public void setList_publicly(final boolean list_publicly) {
		this.list_publicly = list_publicly;
	}

	public boolean isApprove_join_requests() {
		return approve_join_requests;
	}

	public void setApprove_join_requests(final boolean approve_join_requests) {
		this.approve_join_requests = approve_join_requests;
	}

	public GLabel[] getLabels() {
		return labels;
	}

	public void setLabels(final GLabel[] labels) {
		this.labels = labels;
	}

}