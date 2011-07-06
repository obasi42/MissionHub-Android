package com.missionhub.api;

import java.util.HashMap;

import android.util.Log;

public class User {
	
	public static String token;
	public static boolean isLoggedIn = false;
	public static String orgID;
	public static GContact contact;
	public static HashMap<Integer,HashMap<String, String>> validRoles = new HashMap<Integer, HashMap<String, String>>();
	public static int primaryOrgId;
	
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
					primaryOrgId = org_roles[i].getOrg_id();
				}
				Log.i("CR", String.valueOf(org_roles[i].getOrg_id()));
				validRoles.put(org_roles[i].getOrg_id(), map);
			}
		}
	}
}