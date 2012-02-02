package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionBroadcast extends MissionHubBroadcast {

	public static String ACTION_LOGIN = "ACTION_LOGIN";
	public static String ACTION_LOGOUT = "ACTION_LOGOUT";
	public static String ACTION_VERIFY_START = "ACTION_VERIFY_START";
	public static String ACTION_VERIFY_PASS = "ACTION_VERIFY_PASS";
	public static String ACTION_VERIFY_FAIL = "ACTION_VERIFY_FAIL";

	public static void broadcastLogin(final Context context, final String accessToken) {
		final Intent intent = new Intent(ACTION_LOGIN);
		intent.putExtra("accessToken", accessToken);
		sendBroadcast(context, intent);
	}

	public static void broadcastLogout(final Context context) {
		final Intent intent = new Intent(ACTION_LOGOUT);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastVerifyStart(final Context context) {
		final Intent intent = new Intent(ACTION_VERIFY_START);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastVerifyPass(final Context context) {
		final Intent intent = new Intent(ACTION_VERIFY_PASS);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastVerifyFail(final Context context, final Throwable throwable) {
		final Intent intent = new Intent(ACTION_VERIFY_FAIL);
		intent.putExtra("throwable", throwable);
		sendBroadcast(context, intent);
	}
}