package com.missionhub.api.model.json;

public class GOrg {
	private GOrgGeneric[] organizational_roles;
	private GOrgGeneric[] organization_membership;
	
	public GOrgGeneric[] getOrganizational_roles() { return organizational_roles; }
	public GOrgGeneric[] getOrganization_membership() { return organization_membership; }
	
	public void setOrganizational_roles(GOrgGeneric[] x) { organizational_roles = x; }
	public void setOrganization_membership(GOrgGeneric[] x) { organization_membership = x; }
}
