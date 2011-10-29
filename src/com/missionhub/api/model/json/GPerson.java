package com.missionhub.api.model.json;

public class GPerson {	

	private String first_name;
	private String last_name;
	private String name;
	private Integer id;
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
	private GAssign assignment;
	private GOrgGeneric[] organizational_roles;
	private GEducation[] education;
	
	public String getFirst_name() { return first_name; }
	public String getLast_name() { return last_name; }
	public String getName() { return name; }
	public Integer getId() { return id; }
	public String getBirthday() { return birthday; }
	public String getLocale() { return locale; }
	public String getGender() { return gender; }
	public String getFb_id() { return fb_id; }
	public String getPicture() { return picture; }
	public String getStatus() { return status; }
	public String getRequest_org_id() { return request_org_id; }
	public String getPhone_number() { return phone_number; }
	public String getEmail_address() { return email_address; }
	public String getFirst_contact_date() { return first_contact_date; }
	public String getDate_surveyed() { return date_surveyed; }
	public GIdNameProvider getLocation() { return location; }
	public GIdNameProvider[] getInterests() { return interests; }
	public GAssign getAssignment() { return assignment; }
	public GOrgGeneric[] getOrganizational_roles() { return organizational_roles; }
	public GEducation[] getEducation() { return education; }
	
	public void setFirst_name(String s) { first_name = s; }
	public void setLast_name(String s) { last_name = s; }	
	public void setName(String s) {name = s; }
	public void setId(Integer i) { id = i; }
	public void setBirthday(String s) { birthday = s; }
	public void setLocale(String s) { locale = s; }
	public void setGender(String s) { gender = s; }
	public void setFb_id(String s) { fb_id = s; }
	public void setPicture(String s) { picture = s; }
	public void setStatus(String s) { status = s; }
	public void setRequest_org_id(String s) { request_org_id = s; }
	public void setPhone_number(String s) { phone_number = s; }
	public void setEmail_address(String s) { email_address = s; }
	public void setFirst_contact_date(String s) { first_contact_date = s; }
	public void setDate_survyed(String s) { date_surveyed = s; }
	public void setLocation(GIdNameProvider x) { location = x; }
	public void setInterests(GIdNameProvider[] i) { interests = i; }
	public void setAssignment(GAssign a) { assignment = a; }
	public void setOrganizational_roles(GOrgGeneric[] g) { organizational_roles = g; }
	public void setEducation(GEducation[] g) { education = g; }
}
