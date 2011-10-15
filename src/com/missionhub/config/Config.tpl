package com.missionhub.config;

public class Config {
	
	public static final String baseUrl = "@CONFIG.MHBASEURL@";
	public static final String cookieHost = "@CONFIG.MHCOOKIEHOST@";
	public static final String oauthUrl = baseUrl + "/oauth";
	public static final String apiUrl = baseUrl + "/api/v2";
	public static final String oauthClientId = "@CONFIG.MHCLIENTID@";
	public static final String oauthClientSecret = "@CONFIG.MHCLIENTSECRET@";
	public static final String oauthScope = "userinfo,contacts,contact_assignment,followup_comments,roles,organizations";
	public static final String flurryKey = "@CONFIG.MHFLURRYKEY@";
	
}