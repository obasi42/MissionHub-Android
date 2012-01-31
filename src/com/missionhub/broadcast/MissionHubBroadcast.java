package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MissionHubBroadcast {

	public static final String TAG = MissionHubBroadcast.class.getSimpleName();

	protected static void sendBroadcast(final Context context, final Intent intent) {
		try {
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		} catch (final Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

}