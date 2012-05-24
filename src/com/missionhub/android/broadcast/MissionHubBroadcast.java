package com.missionhub.android.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.missionhub.android.app.MissionHubApplication;

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

	protected static void addCategories(final Intent intent, final String... categories) {
		for (final String category : categories) {
			intent.addCategory(category);
		}
	}

}