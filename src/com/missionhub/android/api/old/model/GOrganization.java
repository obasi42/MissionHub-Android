package com.missionhub.android.api.old.model;

public class GOrganization {

	private long id;
	private String name;
	private String ancestry;
	private GPerson[] leaders;
	private GKeyword[] keywords;

	public long getId() {
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

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setAncestry(final String ancestry) {
		this.ancestry = ancestry;
	}

	public void setLeaders(final GPerson[] leaders) {
		this.leaders = leaders;
	}

	public void setKeywords(final GKeyword[] keywords) {
		this.keywords = keywords;
	}
}