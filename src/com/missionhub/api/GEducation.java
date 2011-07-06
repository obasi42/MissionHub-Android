package com.missionhub.api;

public class GEducation {
	private String type;
	private GIdNameProvider school;
	private GIdNameProvider year;
	private String provider;
	private GIdNameProvider[] concentration;
	private GIdNameProvider degree;
	
	public String getType() { return type; }
	public GIdNameProvider getSchool() { return school; }
	public GIdNameProvider getYear() { return year; }
	public String getProvider() { return provider; }
	public GIdNameProvider[] getConcentration() { return concentration; }
	public GIdNameProvider getDegree() { return degree; }
	
	public void setType(String s) { type = s; }
	public void setSchool(GIdNameProvider g) { school = g; }
	public void setYear(GIdNameProvider g) { year = g; }
	public void setProvider(String s) { provider = s; }
	public void setConcentration(GIdNameProvider[] g) { concentration = g; }
	public void setDegree(GIdNameProvider g) { degree = g; }
}
