package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionBroadcast extends MissionHubBroadcast {

	public static final String NOTIFY_LOGIN = MissionHubBroadcast.PREFIX + "NOTIFY_LOGIN";
	public static final String NOTIFY_LOGOUT = MissionHubBroadcast.PREFIX + "NOTIFY_LOGOUT";
	
	public static final String NOTIFY_UPDATE_PERSON_START = MissionHubBroadcast.PREFIX + "NOTIFY_UPDATE_PERSON_START";
	public static final String NOTIFY_UPDATE_PERSON_SUCCESS = MissionHubBroadcast.PREFIX + "NOTIFY_UPDATE_PERSON_SUCCESS";
	public static final String NOTIFY_UPDATE_PERSON_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_UPDATE_PERSON_ERROR";
	
	public static final String NOTIFY_UPDATE_ORGANIZATIONS_START = MissionHubBroadcast.PREFIX + "NOTIFY_UPDATE_ORGANIZATIONS_START";
	public static final String NOTIFY_UPDATE_ORGANIZATIONS_SUCCESS = MissionHubBroadcast.PREFIX + "NOTIFY_UPDATE_ORGANIZATIONS_SUCCESS";
	public static final String NOTIFY_UPDATE_ORGANIZATIONS_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_UPDATE_ORGANIZATIONS_ERROR";

	public static void broadcastLogin(final Context context, final String accessToken) {
		final Intent intent = new Intent(NOTIFY_LOGIN);
		intent.putExtra(MissionHubBroadcast.PREFIX + "accessToken", accessToken);
		sendBroadcast(context, intent);
	}

	public static void broadcastLogout(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_LOGOUT));
	}
	
	public static void broadcastUpdatePersonStart(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_UPDATE_PERSON_START));
	}
	
	public static void broadcastUpdatePersonSuccess(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_UPDATE_PERSON_SUCCESS));
	}
	
	public static void broadcastUpdatePersonError(final Context context, final Throwable throwable) {
		final Intent intent = new Intent(NOTIFY_UPDATE_PERSON_ERROR);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", throwable);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastUpdateOrganizationsStart(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_UPDATE_ORGANIZATIONS_START));
	}
	
	public static void broadcastUpdateOrganizationsSuccess(final Context context) {
		sendBroadcast(context, new Intent(NOTIFY_UPDATE_ORGANIZATIONS_SUCCESS));
	}
	
	public static void broadcastUpdateOrganizationsError(final Context context, final Throwable throwable) {
		final Intent intent = new Intent(NOTIFY_UPDATE_ORGANIZATIONS_ERROR);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", throwable);
		sendBroadcast(context, intent);
	}
}