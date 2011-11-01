package com.missionhub.api.model.json;

public class GMetaOrganization {
	private GMeta meta;
	private GOrganization[] organizations;
	
	public void setMeta(GMeta x) { meta = x; }
	public GMeta getMeta() { return meta; }
	public void setOrganizations(GOrganization[] x) { organizations = x; }
	public GOrganization[] getOrganizations() { return organizations; }
}
