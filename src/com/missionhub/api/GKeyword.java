package com.missionhub.api;

public class GKeyword {
	private String name;
	private int keyword_id;
	private int[] questions;
	
	public String getName() { return name; }
	public int getKeyword_id() { return keyword_id; }
	public int[] getQuestions() { return questions; }
	
	public void setName(String s) { name = s; }
	public void setKeyword_id(int i) { keyword_id = i; }
	public void setQuestions(int[] i) { questions = i; }
	
}
