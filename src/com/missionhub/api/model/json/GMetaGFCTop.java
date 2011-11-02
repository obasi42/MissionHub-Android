package com.missionhub.api.model.json;

public class GMetaGFCTop {
	private GMeta meta;
	private GFCTop[] followup_comments;

	public void setMeta(GMeta x) {
		meta = x;
	}

	public GMeta getMeta() {
		return meta;
	}

	public void setFollowup_comments(GFCTop[] x) {
		followup_comments = x;
	}

	public GFCTop[] getFollowup_comments() {
		return followup_comments;
	}
}
