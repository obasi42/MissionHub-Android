package com.missionhub.android.api.old.model;

public class GEducation {
	private String type;
	private GIdNameProvider school;
	private GIdNameProvider year;
	private String provider;
	private GIdNameProvider[] concentration;
	private GIdNameProvider degree;

	public String getType() {
		return type;
	}

	public GIdNameProvider getSchool() {
		return school;
	}

	public GIdNameProvider getYear() {
		return year;
	}

	public String getProvider() {
		return provider;
	}

	public GIdNameProvider[] getConcentration() {
		return concentration;
	}

	public GIdNameProvider getDegree() {
		return degree;
	}

	public void setType(final String s) {
		type = s;
	}

	public void setSchool(final GIdNameProvider g) {
		school = g;
	}

	public void setYear(final GIdNameProvider g) {
		year = g;
	}

	public void setProvider(final String s) {
		provider = s;
	}

	public void setConcentration(final GIdNameProvider[] g) {
		concentration = g;
	}

	public void setDegree(final GIdNameProvider g) {
		degree = g;
	}
}
