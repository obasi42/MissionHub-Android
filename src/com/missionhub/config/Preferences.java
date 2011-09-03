package com.missionhub.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	/**
	 * Get the stored access token
	 * @param ctx
	 * @return access token or null
	 */
	public static synchronized String getAccessToken(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("accessToken", null);
	}
	
	/**
	 * Sets the stored access token
	 * @param ctx
	 * @param accessToken
	 */
	public static synchronized void setAccessToken(Context ctx, String accessToken) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessToken", accessToken);
		editor.commit();
	}
	
	/**
	 * Removes the access token from the stored preferences
	 * @param ctx
	 */
	public static synchronized void removeAccessToken(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("accessToken");
		editor.commit();
	}
	
	/**
	 * Returns the stored organization ID
	 * @param ctx
	 * @return the org id or -1
	 */
	public static synchronized int getOrganizationID(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("organizationID", -1);
	}
	
	/**
	 * Sets the stored organizationID
	 * @param ctx
	 * @param organizationID
	 */
	public static synchronized void setOrganizationID(Context ctx, int organizationID) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("organizationID", organizationID);
		editor.commit();
	}
	
	/**
	 * Removes the stored organizationID
	 * @param ctx
	 */
	public static synchronized void removeOrganizationID(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("organizationID");
		editor.commit();
	}

	/**
	 * Returns the stored lastRunVersion
	 * @param ctx
	 * @return the lastRunVersion or null
	 */
	public static synchronized String getLastRunVersion(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("lastRunVersion", null);
	}	
	
	/**
	 * Sets the stored lastRunVersion
	 * @param ctx
	 * @param lastRunVersion
	 */
	public static synchronized void setLastRunVersion(Context ctx, String lastRunVersion) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("lastRunVersion", lastRunVersion);
		editor.commit();
	}
	
	/**
	 * Removes the stored lastRunVersion
	 * @param ctx
	 */
	public static synchronized void removeLastRunVersion(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("lastRunVersion");
		editor.commit();
	}
}