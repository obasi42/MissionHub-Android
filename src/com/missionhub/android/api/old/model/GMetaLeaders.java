package com.missionhub.android.api.old.model;

public class GMetaLeaders {
	private GMeta meta;
	private GPerson[] leaders;

	public void setMeta(final GMeta x) {
		meta = x;
	}

	public GMeta getMeta() {
		return meta;
	}

	public void setLeaders(final GPerson[] x) {
		leaders = x;
	}

	public GPerson[] getLeaders() {
		return leaders;
	}
}
