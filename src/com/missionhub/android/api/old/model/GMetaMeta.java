package com.missionhub.android.api.old.model;

public class GMetaMeta {
	private GMeta meta;
	private GPerson person;
	private GOrganization[] organizations;
	private GGroup[] groups;

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
