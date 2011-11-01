package com.missionhub.api.model.json;

public class GOrganization {
	
	private int id;
	private String name;
	private String ancestry;
	private GPerson[] leaders;
	private GKeyword[] keywords;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getAncestry() {
		return ancestry;
	}
	public GPerson[] getLeaders() {
		return leaders;
	}
	public GKeyword[] getKeywords() {
		return keywords;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAncestry(String ancestry) {
		this.ancestry = ancestry;
	}
	public void setLeaders(GPerson[] leaders) {
		this.leaders = leaders;
	}
	public void setKeywords(GKeyword[] keywords) {
		this.keywords = keywords;
	}
}