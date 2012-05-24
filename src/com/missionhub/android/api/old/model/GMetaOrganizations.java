package com.missionhub.android.api.old.model;

public class GMetaOrganizations {
	private GMeta meta;
	private GOrganization[] organizations;

	public void setMeta(final GMeta x) {
		meta = x;
	}

	public GMeta getMeta() {
		return meta;
	}

	public void setOrganizations(final GOrganization[] x) {
		organizations = x;
	}

	public GOrganization[] getOrganizations() {
		return organizations;
	}
}
