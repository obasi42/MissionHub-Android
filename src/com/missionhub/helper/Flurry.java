package com.missionhub.helper;

import java.util.HashMap;

import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;
import com.missionhub.Application;
import com.missionhub.MHException;
import com.missionhub.config.Config;

import android.content.Context;

public class Flurry {
	
	public static void startSession(Context ctx) {
		initFlurryUser(ctx);
		FlurryAgent.setUseHttps(true);
		FlurryAgent.onStartSession(ctx, Config.flurryKey);
	}
	
	public static void endSession(Context ctx) {
		FlurryAgent.onEndSession(ctx);
	}
	
	public static void pageView(Context ctx, String page) {
		try {
			initFlurryUser(ctx);
			FlurryAgent.onPageView();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", page);
			FlurryAgent.onEvent("PageView", params);
		} catch (Exception e) {}
	}
	
	public static void event(Context ctx, String event) {
		try {
			initFlurryUser(ctx);
			FlurryAgent.onEvent(event);
		} catch (Exception e) {}
	}
	
	public static void event(Context ctx, String event, HashMap<String, String> params) {
		try {
			initFlurryUser(ctx);
			FlurryAgent.onEvent(event, params);
		} catch (Exception e) {}
	}
	
	public static void error(Context ctx, Throwable e, String title) {
		try {
			if (e == null) return;
			
			String userId = "";
			try {
				userId = String.valueOf(((Application) ctx.getApplicationContext()).getUser().getId());
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
	private static void initFlurryUser(Context ctx) {
		try {
			FlurryAgent.setUserId(String.valueOf(((Application) ctx.getApplicationContext()).getUser().getId()));
		} catch (Exception e) {
			try {
				FlurryAgent.setUserId(null);
			} catch (Exception e2) {}
		}
		try {
			if (((Application) ctx.getApplicationContext()).getUser().getPerson().getGender().equals("male")) {
				FlurryAgent.setGender(Constants.MALE);
			} else if (((Application) ctx.getApplicationContext()).getUser().getPerson().getGender().equals("female")) {
				FlurryAgent.setGender(Constants.FEMALE);
			}
		} catch (Exception e) {}
	}
}