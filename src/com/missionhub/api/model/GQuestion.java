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

	public void setId(long i) {
		id = i;
	}

	public void setKind(String s) {
		kind = s;
	}

	public void setLabel(String s) {
		label = s;
	}

	public void setStyle(String s) {
		style = s;
	}

	public void setRequired(String b) {
		required = b;
	}

	public void setChoices(String[] s) {
		choices = s;
	}

	public void setActive(String b) {
		active = b;
	}
}
