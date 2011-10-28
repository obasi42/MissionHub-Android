package com.missionhub.ui;

import java.util.HashMap;

public class ListItemSimple {
	public String header;
	public String info;
	
	public HashMap<String, String> data = new HashMap<String, String>();
	
	public ListItemSimple(String header, String info) {
		this.header = header;
		this.info = info;
	}
	
	public ListItemSimple(String header, String info, HashMap<String, String> data) {
		this.header = header;
		this.info = info;
		this.data = data;
	}
}
