package com.missionhub.api;

import java.util.HashMap;

import android.util.Log;

public class User {
	
	private static String token;
	private static boolean isLoggedIn = false;
	private static String orgID;
	private static GContact contact;
	private static HashMap<Integer,HashMap<String, String>> validRoles = new HashMap<Integer, HashMap<String, String>>();
	private static int primaryOrgID;
	private static String currentRole;
	
	public static synchronized String getOrgID() {
		return orgID;
	}
	
	public static synchronized void setOrgID(String org) {
		orgID = org;
	}
	
	public static synchronized String getToken() {
		return token;
	}
	
	public static synchronized void setToken(String t) {
		token = t;
	}
	
	public static synchronized boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	public static synchronized void setLoggedIn(boolean b) {
		isLoggedIn = b;
	}
	
	public static synchronized GContact getContact() {
		return contact;
	}
	
	public static synchronized void setContact(GContact g) {
		if (g == null || g.getPerson() == null) return;
		contact = g;
		calculateRoles(contact.getPerson());
		orgID = String.valueOf(primaryOrgID); 
	}
	
	public static synchronized int getPrimaryOrgID() {
		return primaryOrgID;
	}
	
	public static synchronized String getCurrentRole() {
		return currentRole;
	}
	
	public static synchronized void setCurrentRole(String s) {
		currentRole = s;
	}
	
	public static synchronized HashMap<Integer,HashMap<String, String>> getValidRoles() {
		return validRoles;
	}
	
	
	public static void calculateRoles(GPerson person) {
		GOrgGeneric[] org_roles = person.getOrganizational_roles();
		
		for( int i=0; i < org_roles.length-1; i++ ) {
			HashMap<String, String> map = new HashMap<String,String>();
			if (org_roles[i].getRole().equalsIgnoreCase("leader") || org_roles[i].getRole().equalsIgnoreCase("admin")) {
				map.put("role", org_roles[i].getRole());
				map.put("org_id", String.valueOf(org_roles[i].getOrg_id()));
				map.put("primary", org_roles[i].getPrimary());
				map.put("name", org_roles[i].getName());
				
				if (org_roles[i].getPrimary().equalsIgnoreCase("true")) {
					primaryOrgID = org_roles[i].getOrg_id();
				}
				Log.i("CR", String.valueOf(org_roles[i].getOrg_id()));
				validRoles.put(org_roles[i].getOrg_id(), map);
			}
		}
	}
	
	public static void calculateCurrentRole() {
		GOrgGeneric[] org_roles = contact.getPerson().getOrganizational_roles();
		//TODO: make it such that the current role will be set off the orgID preference, if not set then set it off the primaryOrgID
		
		
	}
}