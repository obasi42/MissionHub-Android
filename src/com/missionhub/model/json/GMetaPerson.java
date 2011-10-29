package com.missionhub.model.json;

public class GMetaPerson {
	private GMeta meta;
	private GPerson[] people;
	
	public void setMeta(GMeta x) { meta = x; }
	public GMeta getMeta() { return meta; }
	public void setPeople(GPerson[] x) { people = x; }
	public GPerson[] getPeople() { return people; }
}
