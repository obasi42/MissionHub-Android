package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionBroadcast extends MissionHubBroadcast {

	public static String ACTION_LOGIN = "ACTION_LOGIN";
	public static String ACTION_LOGOUT = "ACTION_LOGOUT";

	public static void broadcastLogin(final Context context, final String accessToken) {
		final Intent intent = new Intent(ACTION_LOGIN);
		intent.putExtra("accessToken", accessToken);
		sendBroadcast(context, intent);
	}

	public static void broadcastLogout(final Context context) {
		final Intent intent = new Intent(ACTION_LOGOUT);
		sendBroadcast(context, intent);
	}
}