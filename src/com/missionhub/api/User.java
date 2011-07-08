package com.missionhub.api;

import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import com.google.gson.Gson;

import android.os.Bundle;
import android.util.Log;

public class User {
	
	private static String token;
	private static boolean loggedIn = false;
	private static String orgID;
	private static GContact contact;
	private static HashMap<Integer,HashMap<String, String>> validRoles = new HashMap<Integer, HashMap<String, String>>();
	private static String primaryOrgID;
	private static String currentRole;
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	private static String TAG = User.class.getName();
	
	public static synchronized boolean isValid() {
		if (contact != null)
			return true;
					
		return false;
	}
	
	public static synchronized Bundle getAsBundle() {
		Bundle b = new Bundle();
		
		Log.i(TAG, "Saving User To Bundle");
		
		try {
			b.putString("_token", token);
		} catch (Exception e) {Log.e(TAG, "token save failed", e); };
		try {
			b.putBoolean("_loggedIn", loggedIn);
		} catch (Exception e) {Log.e(TAG, "loggedin save failed", e); };
		try {
			Gson gson = new Gson();
			b.putString("_contact", gson.toJson(contact));
		} catch (Exception e) {Log.e(TAG, "contact save failed", e); };
		try {
			b.putString("_currentRole", currentRole);
		} catch (Exception e) {Log.e(TAG, "currentRole save failed", e); };
		
		return b;
	}
	
	public static synchronized void setFromBundle(Bundle b, Activity a) {
		if (b == null || contact != null) return;
		
		Log.i(TAG, "Restoring User From Bundle");
		
		try {
			setToken(b.getString("_token"));
		} catch (Exception e) {Log.e(TAG, "token restore failed", e); };
		try {
			setLoggedIn(b.getBoolean("_loggedIn"));
		} catch (Exception e) {Log.e(TAG, "loggedIn restore failed", e); };
		try {
			Gson gson = new Gson();
			setContact(gson.fromJson(b.getString("_contact"), GContact.class));
		} catch (Exception e) {Log.e(TAG, "contact restore failed", e); };
		try {
			setCurrentRole(b.getString("_currentRole"));
		} catch (Exception e) {Log.e(TAG, "currentRole restore failed", e); };
		
		setOrgID(getOrgIDPreference(a));
	}
	
	public static synchronized String getOrgID() {
		if (orgID == null) {
			return getPrimaryOrgID();
		}
		return orgID;
	}
	
	public static synchronized void setOrgID(String org) {
		if (org == null || org.equals("")) return;
		if (validRoles.containsKey(Integer.parseInt(org))) {
			orgID = org;
		}
		calculateCurrentRole();
	}
	
	public static synchronized String getToken() {
		return token;
	}
	
	public static synchronized void setToken(String t) {
		token = t;
	}
	
	public static synchronized boolean isLoggedIn() {
		return loggedIn;
	}
	
	public static synchronized void setLoggedIn(boolean b) {
		loggedIn = b;
	}
	
	public static synchronized GContact getContact() {
		return contact;
	}
	
	public static synchronized void setContact(GContact g) {
		if (g == null || g.getPerson() == null) return;
		contact = g;
		calculateRoles(contact.getPerson());
	}
	
	public static synchronized String getPrimaryOrgID() {
		return primaryOrgID;
	}
	
	public static synchronized String getCurrentRole() {
		if (currentRole == null) {
			return "none";
		}
		return currentRole;
	}
	
	public static synchronized void setCurrentRole(String s) {
		currentRole = s;
	}
	
	public static synchronized HashMap<Integer,HashMap<String, String>> getValidRoles() {
		return validRoles;
	}
	
	
	public static synchronized void calculateRoles(GPerson person) {
		GOrgGeneric[] org_roles = person.getOrganizational_roles();
		
		for( int i=0; i < org_roles.length; i++ ) {
			HashMap<String, String> map = new HashMap<String,String>();
			if (org_roles[i].getRole().equalsIgnoreCase("leader") || org_roles[i].getRole().equalsIgnoreCase("admin")) {
				map.put("role", org_roles[i].getRole());
				map.put("org_id", String.valueOf(org_roles[i].getOrg_id()));
				map.put("primary", org_roles[i].getPrimary());
				map.put("name", org_roles[i].getName());
				if (org_roles[i].getPrimary().equalsIgnoreCase("true")) {
					primaryOrgID = String.valueOf(org_roles[i].getOrg_id());
				}
				validRoles.put(org_roles[i].getOrg_id(), map);
			}
		}
	}
	
	public static synchronized void calculateCurrentRole() {
		GOrgGeneric[] org_roles = contact.getPerson().getOrganizational_roles();
		for(GOrgGeneric o : org_roles) {
			if (o.getOrg_id() == Integer.parseInt(getOrgID())) {
				setCurrentRole(o.getRole());
			}
		}
		//TODO: make it such that the current role will be set off the orgID preference, if not set then set it off the primaryOrgID
	}
	
	public static synchronized void setOrgIDPreference(String s, Activity a) {
		SharedPreferences settings = a.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("orgID", s);
		editor.commit();
		setOrgID(s);
	}
	
	public static synchronized String getOrgIDPreference(Activity a) {
		SharedPreferences settings = a.getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("orgID", getOrgID());
	}
}
