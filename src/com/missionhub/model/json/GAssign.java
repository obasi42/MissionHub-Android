package com.missionhub.model.json;

public class GAssign {
	private GIdNameProvider[] assigned_to_person;
	private GIdNameProvider[] person_assigned_to;
	
	public GIdNameProvider[] getAssigned_to_person() { return assigned_to_person; }
	public GIdNameProvider[] getPerson_assigned_to() { return person_assigned_to; }
	
	public void setAssigned_to_person(GIdNameProvider[] x) { assigned_to_person = x; }
	public void setPerson_assigned_to(GIdNameProvider[] x) { person_assigned_to = x; }

}
