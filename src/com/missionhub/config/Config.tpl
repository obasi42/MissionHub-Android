package com.missionhub.config;

public class Config {
	
	public static final String baseUrl = "@MHCONFIG.BASEURL@";
	public static final String cookieHost = "@MHCONFIG_COOKIEHOST@";
	public static final String oauthUrl = baseUrl + "/oauth";
	public static final String apiUrl = baseUrl + "/api/v1";
	public static final String oauthClientId = "@MHCONFIG.CLIENTID@";
	public static final String oauthClientSecret = "@MHCONFIG.CLIENTSECRET@";
	public static final String oauthScope = "@MHCONFIG.OAUTHSCOPE@";
	public static final String flurryKey = "@MHCONFIG.FLURRYKEY@";
	
}