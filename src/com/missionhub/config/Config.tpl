package com.missionhub.config;

public class Config {
	
	public static final boolean debug = @CONFIG.MHDEBUG@;
	public static final Market market = @CONFIG.MHMARKET@;
	
	public static final String baseUrl = "@CONFIG.MHBASEURL@";
	public static final String cookieHost = "@CONFIG.MHCOOKIEHOST@";
	public static final String oauthUrl = baseUrl + "/oauth";
	public static final String apiUrl = baseUrl + "/api";
	public static final String apiVersion = "2";
	public static final String oauthClientId = "@CONFIG.MHCLIENTID@";
	public static final String oauthClientSecret = "@CONFIG.MHCLIENTSECRET@";
	public static final String oauthScope = "userinfo,contacts,contact_assignment,followup_comments,roles,organization_info";
	public static final String gAnalyticsKey = "@CONFIG.MHGANALYTICSKEY@";
	
}