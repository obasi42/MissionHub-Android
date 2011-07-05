package com.missionhub.api;

import org.json.JSONObject;

public class Contact {
	
	private Person person;
	
	public Contact (JSONObject contact) {
		
	}
	
	public Contact (Person person) {
		setPerson(person);
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Person getPerson() {
		return person;
	}
	
}