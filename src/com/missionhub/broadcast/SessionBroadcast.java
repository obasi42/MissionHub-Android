package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionBroadcast extends MissionHubBroadcast {

	public static String NOTIFY_LOGIN = MissionHubBroadcast.PREFIX + "NOTIFY_LOGIN";
	public static String NOTIFY_LOGOUT = MissionHubBroadcast.PREFIX + "NOTIFY_LOGOUT";
	public static String NOTIFY_VERIFY_START = MissionHubBroadcast.PREFIX + "NOTIFY_VERIFY_START";
	public static String NOTIFY_VERIFY_PASS = MissionHubBroadcast.PREFIX + "NOTIFY_VERIFY_PASS";
	public static String NOTIFY_VERIFY_FAIL = MissionHubBroadcast.PREFIX + "NOTIFY_VERIFY_FAIL";

	public static void broadcastLogin(final Context context, final String accessToken) {
		final Intent intent = new Intent(NOTIFY_LOGIN);
		intent.putExtra(MissionHubBroadcast.PREFIX + "accessToken", accessToken);
		sendBroadcast(context, intent);
	}

	public static void broadcastLogout(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_LOGOUT));
	}

	public static void broadcastVerifyStart(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_VERIFY_START));
	}

	public static void broadcastVerifyPass(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_VERIFY_PASS));
	}

	public static void broadcastVerifyFail(final Context context, final Throwable throwable) {
		final Intent intent = new Intent(NOTIFY_VERIFY_FAIL);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", throwable);
		sendBroadcast(context, intent);
	}
}