package com.missionhub.api.model;

public class GKeyword {
	private String keyword;
	private long id;
	private GQuestion[] questions;
	private String state;

	public String getKeyword() {
		return keyword;
	}

	public long getId() {
		return id;
	}

	public GQuestion[] getQuestions() {
		return questions;
	}

	public String getState() {
		return state;
	}

	public void setKeyword(final String s) {
		keyword = s;
	}

	public void setId(final long i) {
		id = i;
	}

	public void setQuestions(final GQuestion[] i) {
		questions = i;
	}

	public void setState(final String s) {
		state = s;
	}
}
