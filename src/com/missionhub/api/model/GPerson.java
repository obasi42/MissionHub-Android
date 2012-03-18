package com.missionhub.api.model;

public class GPerson {

	private String first_name;
	private String last_name;
	private String name;
	private Long id;
	private String birthday;
	private String locale;
	private String gender;
	private String fb_id;
	private String picture;
	private String status;
	private String request_org_id;
	private String phone_number;
	private String email_address;
	private String first_contact_date;
	private String date_surveyed;
	private GIdNameProvider location;
	private GIdNameProvider[] interests;
	private GAssignment assignment;
	private GOrgGeneric[] organizational_roles;
	private GGroupMembership[] group_memberships;
	private GEducation[] education;
	private String num_contacts;

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getLocale() {
		return locale;
	}

	public String getGender() {
		return gender;
	}

	public String getFb_id() {
		return fb_id;
	}

	public String getPicture() {
		return picture;
	}

	public String getStatus() {
		return status;
	}

	public String getRequest_org_id() {
		return request_org_id;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public String getEmail_address() {
		return email_address;
	}

	// public String getFirst_contact_date() {
	// return first_contact_date;
	// }
	//
	// public String getDate_surveyed() {
	// return date_surveyed;
	// }

	public GIdNameProvider getLocation() {
		return location;
	}

	public GIdNameProvider[] getInterests() {
		return interests;
	}

	public GAssignment getAssignment() {
		return assignment;
	}

	public GOrgGeneric[] getOrganizational_roles() {
		return organizational_roles;
	}

	public GEducation[] getEducation() {
		return education;
	}

	public String getNum_contacts() {
		return num_contacts;
	}

	public void setFirst_name(final String s) {
		first_name = s;
	}

	public void setLast_name(final String s) {
		last_name = s;
	}

	public void setName(final String s) {
		name = s;
	}

	public void setId(final Long i) {
		id = i;
	}

	public void setBirthday(final String s) {
		birthday = s;
	}

	public void setLocale(final String s) {
		locale = s;
	}

	public void setGender(final String s) {
		gender = s;
	}

	public void setFb_id(final String s) {
		fb_id = s;
	}

	public void setPicture(final String s) {
		picture = s;
	}

	public void setStatus(final String s) {
		status = s;
	}

	public void setRequest_org_id(final String s) {
		request_org_id = s;
	}

	public void setPhone_number(final String s) {
		phone_number = s;
	}

	public void setEmail_address(final String s) {
		email_address = s;
	}

	// public void setFirst_contact_date(String s) {
	// first_contact_date = s;
	// }
	//
	// public void setDate_survyed(String s) {
	// date_surveyed = s;
	// }

	public void setFirst_contact_date(final String first_contact_date) {
		this.first_contact_date = first_contact_date;
	}

	public String getFirst_contact_date() {
		return first_contact_date;
	}

	public void setLocation(final GIdNameProvider x) {
		location = x;
	}

	public void setInterests(final GIdNameProvider[] i) {
		interests = i;
	}

	public void setAssignment(final GAssignment a) {
		assignment = a;
	}

	public void setOrganizational_roles(final GOrgGeneric[] g) {
		organizational_roles = g;
	}

	public void setGroup_memberships(final GGroupMembership[] group_memberships) {
		this.group_memberships = group_memberships;
	}

	public GGroupMembership[] getGroup_memberships() {
		return group_memberships;
	}

	public void setEducation(final GEducation[] g) {
		education = g;
	}

	public void setNum_contacts(final String s) {
		num_contacts = s;
	}

	public void setDate_surveyed(final String date_surveyed) {
		this.date_surveyed = date_surveyed;
	}

	public String getDate_surveyed() {
		return date_surveyed;
	}
}
