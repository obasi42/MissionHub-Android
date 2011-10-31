package com.missionhub.helper;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.missionhub.Application;
import com.missionhub.config.Config;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class AnalyticsTracker {

	public static final String TAG = AnalyticsTracker.class.getSimpleName();

	public static final int SCOPE_VISITOR_LEVEL = 1;
	public static final int SCOPE_SESSION_LEVEL = 2;
	public static final int SCOPE_PAGE_LEVEL = 3;

	private GoogleAnalyticsTracker tracker;

	public AnalyticsTracker(Context context) {
		try {
			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.startNewSession(Config.gAnalyticsKey, 20, context);
			tracker.setProductVersion("MissionHubAndroid", ((Application) context.getApplicationContext()).getVersion());
			if (Config.debug) {
				tracker.setDebug(true);
				tracker.setDryRun(true);
			}
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}
	
	public GoogleAnalyticsTracker getTracker() {
		return tracker;
	}

	private void setCustomVars() {
		tracker.setCustomVar(1, "market", Config.market.name(), SCOPE_SESSION_LEVEL);
	}
	
	public void setCustomVar(String name, String value, int scope) {
		try {
			tracker.setCustomVar(2, name, value, scope);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}

	public void trackEvent(String category, String action, String label, int value) {
		try {
			setCustomVars();
			tracker.trackEvent(category, action, label, value);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}

	public void trackPageView(String page) {
		try {
			setCustomVars();
			tracker.trackPageView("/android/" + page);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}
	
	public void trackActivityView(Activity a) {
		try {
			trackPageView(a.getClass().getCanonicalName());
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}

	public void stopSession() {
		try {
			tracker.stopSession();
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}
}