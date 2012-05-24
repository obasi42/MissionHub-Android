package com.missionhub.android.api.old.model;

public class GMetaPeople {
	private GMeta meta;
	private GPerson[] people;

	public void setMeta(final GMeta x) {
		meta = x;
	}

	public GMeta getMeta() {
		return meta;
	}

	public void setPeople(final GPerson[] x) {
		people = x;
	}

	public GPerson[] getPeople() {
		return people;
	}
}
