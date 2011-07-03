package com.missionhub.api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.Config;

public class OAuth {
	
	public static final String TAG = "OAuth";
	
	public static boolean isLoggedIn = false;
	public static String token;
	
	public static void checkToken(String token, AsyncHttpResponseHandler handler) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Config.apiUrl + "/people/me.json?access_token=" + OAuth.token, handler);
		Log.d(TAG, "Open: " + Config.apiUrl + "/people/me.json?access_token=" + token);
	}
	
}