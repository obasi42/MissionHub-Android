package com.missionhub.config;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	public static final String PREFS_NAME = "MissionHubPrivate";

	/**
	 * Removes all the shared preferences
	 * 
	 * @param context
	 */
	public static synchronized void reset(final Context context) {
		context.getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
	}

	/**
	 * Get the stored access token
	 * 
	 * @param context
	 * @return access token or null
	 */
	public static synchronized String getAccessToken(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("accessToken", null);
	}

	/**
	 * Sets the stored access token
	 * 
	 * @param context
	 * @param accessToken
	 */
	public static synchronized void setAccessToken(final Context context, final String accessToken) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessToken", accessToken);
		editor.commit();
	}

	/**
	 * Removes the access token from the stored preferences
	 * 
	 * @param context
	 */
	public static synchronized void removeAccessToken(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.remove("accessToken");
		editor.commit();
	}

	/**
	 * Returns the stored organization ID
	 * 
	 * @param context
	 * @return the org id or -1
	 */
	public static synchronized long getOrganizationID(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong("organizationID", -1);
	}

	/**
	 * Sets the stored organizationID
	 * 
	 * @param context
	 * @param organizationID
	 */
	public static synchronized void setOrganizationID(final Context context, final long organizationID) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putLong("organizationID", organizationID);
		editor.commit();
	}

	/**
	 * Removes the stored organizationID
	 * 
	 * @param context
	 */
	public static synchronized void removeOrganizationID(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.remove("organizationID");
		editor.commit();
	}

	/**
	 * Returns the stored user's id
	 * 
	 * @param context
	 * @return the org id or -1
	 */
	public static synchronized long getUserID(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getLong("userID", -1);
	}

	/**
	 * Sets the stored user's id
	 * 
	 * @param context
	 * @param organizationID
	 */
	public static synchronized void setUserID(final Context context, final long organizationID) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putLong("userID", organizationID);
		editor.commit();
	}

	/**
	 * Removes the stored organizationID
	 * 
	 * @param context
	 */
	public static synchronized void removeUserID(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.remove("userID");
		editor.commit();
	}

	/**
	 * Returns the stored lastRunMajorVersion
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized int getLastRunMajorVersion(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("lastRunMajorVersion", -1);
	}

	/**
	 * Returns the stored lastRunMinorVersion
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized int getLastRunMinorVersion(final Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		return settings.getInt("lastRunMinorVersion", -1);
	}

	/**
	 * Sets the stored lastRunMajorVersion
	 * 
	 * @param context
	 * @param lastRunMajorVersion
	 */
	public static synchronized void setLastRunMajorVersion(final Context context, final int lastRunMajorVersion) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putInt("lastRunMajorVersion", lastRunMajorVersion);
		editor.commit();
	}

	/**
	 * Sets the stored lastRunMinorVersion
	 * 
	 * @param context
	 * @param lastRunMinorVersion
	 */
	public static synchronized void setLastRunMinorVersion(final Context context, final int lastRunMinorVersion) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putInt("lastRunMinorVersion", lastRunMinorVersion);
		editor.commit();
	}
}