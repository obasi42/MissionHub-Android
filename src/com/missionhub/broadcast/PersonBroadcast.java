package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class PersonBroadcast extends MissionHubBroadcast {
	
	public static final String NOTIFY_PERSON_CREATE = MissionHubBroadcast.PREFIX + "NOTIFY_PERSON_CREATE";
	public static final String NOTIFY_PERSON_UPDATE = MissionHubBroadcast.PREFIX + "NOTIFY_PERSON_UPDATE";
	public static final String NOTIFY_PERSON_DELETE = MissionHubBroadcast.PREFIX + "NOTIFY_PERSON_DELETE";
	public static final String NOTIFY_PERSON_ERROR  = MissionHubBroadcast.PREFIX + "NOTIFY_PERSON_ERROR";

	public static void broadcastCreate(Context context, long personId, String ... categories) {
		final Intent intent = new Intent(NOTIFY_PERSON_CREATE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "personId", personId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastUpdate(Context context, long personId, String ... categories) {
		final Intent intent = new Intent(NOTIFY_PERSON_UPDATE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "personId", personId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastDelete(Context context, long personId, String ... categories) {
		final Intent intent = new Intent(NOTIFY_PERSON_DELETE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "personId", personId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastError(Context context, long personId, Throwable t, String ... categories) {
		final Intent intent = new Intent(NOTIFY_PERSON_ERROR);
		intent.putExtra(MissionHubBroadcast.PREFIX + "personId", personId);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", t);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
}