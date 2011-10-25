package com.missionhub.helpers;

import java.util.HashMap;

import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;
import com.missionhub.auth.User;
import com.missionhub.config.Config;
import com.missionhub.error.MHException;

import android.content.Context;

public class Flurry {
	
	public static void startSession(Context ctx) {
		initFlurryUser();
		FlurryAgent.setUseHttps(true);
		FlurryAgent.onStartSession(ctx, Config.flurryKey);
	}
	
	public static void endSession(Context ctx) {
		FlurryAgent.onEndSession(ctx);
	}
	
	public static void pageView(String page) {
		try {
			initFlurryUser();
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", page);
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
	}
	
	public static void event(String event) {
		try {
			initFlurryUser();
			FlurryAgent.onEvent(event);
		} catch (Exception e) {}
	}
	
	public static void event(String event, HashMap<String, String> params) {
		try {
			initFlurryUser();
			FlurryAgent.onEvent(event, params);
		} catch (Exception e) {}
	}
	
	public static void error(Throwable e, String title) {
		try {
			if (e == null) return;
			
			String userId = "";
			try {
				userId = String.valueOf(User.getContact().getPerson().getId());
			} catch (Exception e2) {}
			
			
			if (e instanceof MHException) {
				if (userId != null && !userId.equals("")) {
					FlurryAgent.onError(((MHException) e).getCode(), e.getMessage() + " || UserID: " + userId, e.getClass().getName());
				} else {
					FlurryAgent.onError(((MHException) e).getCode(), e.getMessage(), e.getClass().getName());
				}
			} else {
				if (e.getMessage() != null) {
					if (userId != null && !userId.equals("")) {
						FlurryAgent.onError(title, e.getMessage() + " || UserID: " + userId, e.getClass().getName());
					} else {
						FlurryAgent.onError(title, e.getMessage(), e.getClass().getName());
					}
				}
			}
		} catch (Exception e2) {}
	}
	
	/**
	 * Set FlurryAgent data based on contact object
	 */
	private static void initFlurryUser() {
		try {
			FlurryAgent.setUserId(String.valueOf(User.getContact().getPerson().getId()));
		} catch (Exception e) {
			try {
				FlurryAgent.setUserId(null);
			} catch (Exception e2) {}
		}
		try {
			if (User.getContact().getPerson().getGender().equals("male")) {
				FlurryAgent.setGender(Constants.MALE);
			} else if (User.getContact().getPerson().getGender().equals("female")) {
				FlurryAgent.setGender(Constants.FEMALE);
			}
		} catch (Exception e) {}
	}
}