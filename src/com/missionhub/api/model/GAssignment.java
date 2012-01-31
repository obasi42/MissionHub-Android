package com.missionhub.api.model;

public class GAssignment {
	private GIdNameProvider[] assigned_to_person;
	private GIdNameProvider[] person_assigned_to;

	public GIdNameProvider[] getAssigned_to_person() {
		return assigned_to_person;
	}

	public GIdNameProvider[] getPerson_assigned_to() {
		return person_assigned_to;
	}

	public void setAssigned_to_person(final GIdNameProvider[] x) {
		assigned_to_person = x;
	}

	public void setPerson_assigned_to(final GIdNameProvider[] x) {
		person_assigned_to = x;
	}

}
