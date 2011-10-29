package com.missionhub.api.model.json;

public class GFollowupComment {
	private GComment comment;
	private GRejoicable[] rejoicables;

	public GComment getComment() { return comment; }
	public GRejoicable[] getRejoicables() { return rejoicables; }
	
	public void setComment(GComment g) { comment = g; }
	public void setRejoicables(GRejoicable[] g) { rejoicables = g; }
}
