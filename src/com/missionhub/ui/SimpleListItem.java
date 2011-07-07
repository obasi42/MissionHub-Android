package com.missionhub.ui;

public class SimpleListItem {
	public String header;
	public String info;
	public boolean selected = false;
	
	public SimpleListItem(int drawable, String header, String info) {
		this.header = header;
		this.info = info;
	}
}
