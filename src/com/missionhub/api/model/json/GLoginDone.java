package com.missionhub.api.model.json;

public class GLoginDone {
	private String access_token;
	private String scope;
	private GPerson person;
	
	public String getAccess_token() { return access_token; }
	public String getScope() { return scope; }
	public GPerson getPerson() { return person; }
	
	public void setAccess_token(String s) { access_token = s; }
	public void setScope(String s) { scope = s; }
	public void setPerson(GPerson p) { person = p; }
}
