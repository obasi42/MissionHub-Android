package com.missionhub.api.model;

public class GQuestion {
	private long id;
	private String kind;
	private String label;
	private String style;
	private String required;
	private String active;
	private String[] choices;

	public long getId() {
		return id;
	}

	public String getKind() {
		return kind;
	}

	public String getLabel() {
		return label;
	}

	public String getStyle() {
		return style;
	}

	public String getRequired() {
		return required;
	}

	public String[] getChoices() {
		return choices;
	}

	public String setActive() {
		return active;
	}

	public void setId(final long i) {
		id = i;
	}

	public void setKind(final String s) {
		kind = s;
	}

	public void setLabel(final String s) {
		label = s;
	}

	public void setStyle(final String s) {
		style = s;
	}

	public void setRequired(final String b) {
		required = b;
	}

	public void setChoices(final String[] s) {
		choices = s;
	}

	public void setActive(final String b) {
		active = b;
	}
}
