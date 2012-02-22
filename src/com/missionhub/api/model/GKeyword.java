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

	public void setKeyword(String s) {
		keyword = s;
	}

	public void setId(long i) {
		id = i;
	}

	public void setQuestions(GQuestion[] i) {
		questions = i;
	}

	public void setState(String s) {
		state = s;
	}
}
