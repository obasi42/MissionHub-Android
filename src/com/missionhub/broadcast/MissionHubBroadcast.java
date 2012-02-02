package com.missionhub.broadcast;

import com.missionhub.MissionHubApplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MissionHubBroadcast {

	public static final String TAG = MissionHubBroadcast.class.getSimpleName();
	
	public static final String PREFIX = MissionHubApplication.class.getPackage().getName() + ".";

	protected static void sendBroadcast(final Context context, final Intent intent) {
		try {
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		} catch (final Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	protected static void addCategories(final Intent intent, String ... categories) {
		for (String category : categories) {
			intent.addCategory(category);
		}
	}

}