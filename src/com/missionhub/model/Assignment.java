package com.missionhub.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ASSIGNMENT.
 */
public class Assignment {

	private Long id;
	private Long assigned_to_id;
	private Long person_id;
	private Long organization_id;

	public Assignment() {}

	public Assignment(final Long id) {
		this.id = id;
	}

	public Assignment(final Long id, final Long assigned_to_id, final Long person_id, final Long organization_id) {
		this.id = id;
		this.assigned_to_id = assigned_to_id;
		this.person_id = person_id;
		this.organization_id = organization_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Long getAssigned_to_id() {
		return assigned_to_id;
	}

	public void setAssigned_to_id(final Long assigned_to_id) {
		this.assigned_to_id = assigned_to_id;
	}

	public Long getPerson_id() {
		return person_id;
	}

	public void setPerson_id(final Long person_id) {
		this.person_id = person_id;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(final Long organization_id) {
		this.organization_id = organization_id;
	}

}
