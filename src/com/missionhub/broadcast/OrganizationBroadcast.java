package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class OrganizationBroadcast extends MissionHubBroadcast {

	public static final String NOTIFY_ORGANIZATIONS_COMPLETE = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATIONS_COMPLETE";
	public static final String NOTIFY_ORGANIZATIONS_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATIONS_ERROR";
	public static final String NOTIFY_ORGANIZATION_UPDATE = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATION_UPDATE";
	public static final String NOTIFY_ORGANIZATION_DELETE = MissionHubBroadcast.PREFIX + "NOTIFY_ORGANIZATION_DELETE";

	public static void broadcastComplete(final Context context, final long[] organizations, final String... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATIONS_COMPLETE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationIds", organizations);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	public static void broadcastError(final Context context, final long[] organizations, final Throwable t, final String... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATIONS_ERROR);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationIds", organizations);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", t);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	public static void broadcastUpdate(final Context context, final long organizationId, final String... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATION_UPDATE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationId", organizationId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	public static void broadcastDelete(final Context context, final long organizationId, final String... categories) {
		final Intent intent = new Intent(NOTIFY_ORGANIZATION_DELETE);
		intent.putExtra(MissionHubBroadcast.PREFIX + "organizationId", organizationId);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}
}