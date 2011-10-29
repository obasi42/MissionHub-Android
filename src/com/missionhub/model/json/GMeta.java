package com.missionhub.model.json;

public class GMeta {
	private String request_time;
	private String request_organization;
	private String last_request;
	private String last_identical_request;
	
	public String getRequest_time() { return request_time; }
	public void setRequest_time(String s) { this.request_time = s; }
	public String getRequest_organization() { return request_organization; }
	public void setRequest_organization(String s) { this.request_organization = s; }
	public String getLast_request() { return last_request; }
	public void setLast_request(String s) { this.last_request = s; }
	public String getLast_identical_request() {	return last_identical_request; }
	public void setLast_identical_request(String s) { this.last_identical_request = s; }
}
