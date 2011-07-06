package com.missionhub.api;

public class GQuestion {
	private int id;
	private String kind;
	private String label;
	private String style;
	private boolean required;
	
	public int getId() { return id; }
	public String getKind() { return kind; }
	public String getLabel() { return label; }
	public String getStyle() { return style; }
	public boolean getRequired() { return required; }
	
	public void setId(int i) { id = i; }
	public void setKind(String s) { kind = s; }
	public void setLabel(String s) { label = s; }
	public void setStyle(String s) { style = s; }
	public void setRequired(boolean b) { required = b; }
	
}
