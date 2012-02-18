package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class OrganizationBroadcast extends MissionHubBroadcast {

	public static final String NOTIFY_ORGANIZATION_CREATE = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATION_CREATE";
	public static final String NOTIFY_ORGANIZATION_UPDATE = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATION_UPDATE";
	public static final String NOTIFY_ORGANIZATION_DELETE = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATION_DELETE";
	public static final String NOTIFY_ORGANIZATION_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATION_ERROR";
	
	public static void broadcastCreate(Context context, long organizationId, String ... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATION_CREATE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationId", organizationId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastUpdate(Context context, long organizationId, String ... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATION_UPDATE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationId", organizationId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastDelete(Context context, long organizationId, String ... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATION_DELETE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationId", organizationId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
	
	public static void broadcastError(Context context, long organizationId, Throwable t, String ... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATION_ERROR);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationId", organizationId);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", t);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
}