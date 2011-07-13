package com.missionhub.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	public static synchronized String getAccessToken(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("accessToken", null);
	}
	
	public static synchronized void setAccessToken(Context ctx, String accessToken) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessToken", accessToken);
		editor.commit();
	}
	
	public static synchronized void removeAccessToken(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("accessToken");
		editor.commit();
	}
	
	public static synchronized int getOrganizationID(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("organizationID", -1);
	}
	
	public static synchronized void setOrganizationID(Context ctx, int organizationID) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("organizationID", organizationID);
		editor.commit();
	}
	
	public static synchronized void removeOrganizationID(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("organizationID");
		editor.commit();
	}
	
}