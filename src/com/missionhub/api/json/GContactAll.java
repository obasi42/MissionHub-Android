package com.missionhub.api.json;

public class GContactAll {
	private GMeta meta;
	private GContact[] people;
	private GQuestion[] questions;
	private GKeyword[] keywords;
	
	public GMeta getMeta() { return meta; }
	public GContact[] getPeople() { return people; }
	public GQuestion[] getQuestions() { return questions; }
	public GKeyword[] getKeywords() { return keywords; }
	
	public void setMeta(GMeta m) { meta = m; }
	public void setPeople(GContact[] g) { people = g; }
	public void setQuestions(GQuestion[] q) { questions = q; }
	public void setKeywords(GKeyword[] k) { keywords = k; }
	
}
