package com.missionhub.api.json;
import com.missionhub.api.json.GContact;

public class GContact {
	public GPerson person;
	public GQA[] form;
	
	public void setPerson(GPerson p) { person = p; }
	public void setForm(GQA[] g) { form = g; }
	
	public GPerson getPerson() { return person; }
	public GQA[] getForm() { return form; }
	
}
