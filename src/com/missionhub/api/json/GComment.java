package com.missionhub.api.json;

public class GComment {
	private int id;
	private int contact_id;
	private GCommenter commenter;
	private String comment;
	private String status;
	private int organization_id;
	private String created_at;
	private String created_at_words;
	
	public int getId() { return id; }
	public int getContact_id() { return contact_id; }
	public GCommenter getCommenter() { return commenter; }
	public String getComment() { return comment; }
	public String getStatus() { return status; }
	public int getOrganization_id() { return organization_id; }
	public String getCreated_at() { return created_at; }
	public String getCreated_at_words() { return created_at_words; }
	
	public void setId(int i) { id = i; }
	public void setContact_id(int i) { contact_id = i; }
	public void setCommenter(GCommenter g) { commenter = g; }
	public void setComment(String s) { comment = s; }
	public void setStatus(String s) { status = s; }
	public void setOrganization_id(int i) { organization_id = i; }
	public void setCreatedAt(String s) { created_at = s; }
	public void setCreated_at_words(String s) { created_at_words = s; }
	
}
