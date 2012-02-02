package com.missionhub.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	/**
	 * Get the stored access token
	 * @param context
	 * @return access token or null
	 */
	public static synchronized String getAccessToken(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("accessToken", null);
	}
	
	/**
	 * Sets the stored access token
	 * @param context
	 * @param accessToken
	 */
	public static synchronized void setAccessToken(Context context, String accessToken) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessToken", accessToken);
		editor.commit();
	}
	
	/**
	 * Removes the access token from the stored preferences
	 * @param context
	 */
	public static synchronized void removeAccessToken(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("accessToken");
		editor.commit();
	}
	
	/**
	 * Returns the stored organization ID
	 * @param context
	 * @return the org id or -1
	 */
	public static synchronized int getOrganizationID(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("organizationID", -1);
	}
	
	/**
	 * Sets the stored organizationID
	 * @param context
	 * @param organizationID
	 */
	public static synchronized void setOrganizationID(Context context, int organizationID) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("organizationID", organizationID);
		editor.commit();
	}
	
	/**
	 * Removes the stored organizationID
	 * @param context
	 */
	public static synchronized void removeOrganizationID(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("organizationID");
		editor.commit();
	}
	
	/**
	 * Returns the stored user's id
	 * @param context
	 * @return the org id or -1
	 */
	public static synchronized int getUserID(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("userID", -1);
	}
	
	/**
	 * Sets the stored user's id
	 * @param context
	 * @param organizationID
	 */
	public static synchronized void setUserID(Context context, int organizationID) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("userID", organizationID);
		editor.commit();
	}
	
	/**
	 * Removes the stored organizationID
	 * @param context
	 */
	public static synchronized void removeUserID(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("userID");
		editor.commit();
	}

	/**
	 * Returns the stored lastRunVersion
	 * @param context
	 * @return the lastRunVersion or null
	 */
	public static synchronized String getLastRunVersion(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("lastRunVersion", null);
	}	
	
	/**
	 * Sets the stored lastRunVersion
	 * @param context
	 * @param lastRunVersion
	 */
	public static synchronized void setLastRunVersion(Context context, String lastRunVersion) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("lastRunVersion", lastRunVersion);
		editor.commit();
	}
	
	/**
	 * Removes the stored lastRunVersion
	 * @param context
	 */
	public static synchronized void removeLastRunVersion(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("lastRunVersion");
		editor.commit();
	}
}