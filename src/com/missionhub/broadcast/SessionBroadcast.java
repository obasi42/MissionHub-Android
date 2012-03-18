package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionBroadcast extends MissionHubBroadcast {

	public static final String NOTIFY_LOGIN = MissionHubBroadcast.PREFIX + "NOTIFY_LOGIN";
	public static final String NOTIFY_LOGOUT = MissionHubBroadcast.PREFIX + "NOTIFY_LOGOUT";

	public static final String NOTIFY_SESSION_UPDATE_START = MissionHubBroadcast.PREFIX + "NOTIFY_SESSION_UPDATE_START";
	public static final String NOTIFY_SESSION_UPDATE_SUCCESS = MissionHubBroadcast.PREFIX + "NOTIFY_SESSION_UPDATE_SUCCESS";
	public static final String NOTIFY_SESSION_UPDATE_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_SESSION_UPDATE_ERROR";

	public static void broadcastLogin(final Context context, final String accessToken) {
		final Intent intent = new Intent(NOTIFY_LOGIN);
		intent.putExtra(MissionHubBroadcast.PREFIX + "accessToken", accessToken);
		sendBroadcast(context, intent);
	}

	public static void broadcastLogout(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_LOGOUT));
	}

	public static void broadcastUpdateStart(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_SESSION_UPDATE_START));
	}

	public static void broadcastUpdateSuccess(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_SESSION_UPDATE_SUCCESS));
	}

	public static void broadcastUpdateError(final Context context, final Throwable throwable) {
		final Intent intent = new Intent(NOTIFY_SESSION_UPDATE_ERROR);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", throwable);
		sendBroadcast(context, intent);
	}
}