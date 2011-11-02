package com.missionhub.api.model.json;

public class GKeyword {
	private String keyword;
	private int id;
	private GQuestion[] questions;
	private String state;

	public String getKeyword() {
		return keyword;
	}

	public int getId() {
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

	public void setId(int i) {
		id = i;
	}

	public void setQuestions(GQuestion[] i) {
		questions = i;
	}

	public void setState(String s) {
		state = s;
	}
}
