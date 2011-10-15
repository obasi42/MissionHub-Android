package com.missionhub.api.json;

public class GContactAll {
	private GContact[] people;
	private GQuestion[] questions;
	private GKeyword[] keywords;
	
	public GContact[] getPeople() { return people; }
	public GQuestion[] getQuestions() { return questions; }
	public GKeyword[] getKeywords() { return keywords; }
	
	public void setPeople(GContact[] g) { people = g; }
	public void setQuestions(GQuestion[] q) { questions = q; }
	public void setKeywords(GKeyword[] k) { keywords = k; }
	
}
