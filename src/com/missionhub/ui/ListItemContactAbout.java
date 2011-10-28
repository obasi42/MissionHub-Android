package com.missionhub.ui;

import java.util.HashMap;

import android.graphics.drawable.Drawable;

public class ListItemContactAbout {
	public String header;
	public String info;
	public Drawable icon;
	public Action action;
	
	public HashMap<String, String> data = new HashMap<String, String>();
	
	public ListItemContactAbout(String header, String info) {
		this(header, info, null, null);
	}
	
	public ListItemContactAbout(String header, String info, Drawable icon) {
		this(header, info, icon, null);
	}
	
	public ListItemContactAbout(String header, String info, Drawable icon, Action action) {
		this.header = header;
		this.info = info;
		this.icon = icon;
		this.action = action;
	}
	
	public static class Action {
		public void perform() {}
	}
}
