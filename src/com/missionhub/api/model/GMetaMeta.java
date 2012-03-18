package com.missionhub.api.model;

public class GMetaMeta {
	public GMeta meta;
	public GPerson person;
	public GOrganization[] organizations;
	public GGroup[] groups;

	public GMeta getMeta() {
		return meta;
	}

	public void setMeta(final GMeta meta) {
		this.meta = meta;
	}

	public GPerson getPerson() {
		return person;
	}

	public void setPerson(final GPerson person) {
		this.person = person;
	}

	public GOrganization[] getOrganizations() {
		return organizations;
	}

	public void setOrganizations(final GOrganization[] organizations) {
		this.organizations = organizations;
	}

	public GGroup[] getGroups() {
		return groups;
	}

	public void setGroups(final GGroup[] groups) {
		this.groups = groups;
	}
}
