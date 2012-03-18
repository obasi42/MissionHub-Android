package com.missionhub.broadcast;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;

public class GenericSEBroadcast extends MissionHubBroadcast {

	public static final String NOTIFY_GENERIC_SUCCESS = MissionHubBroadcast.PREFIX + "NOTIFY_GENERIC_SUCCESS";
	public static final String NOTIFY_GENERIC_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_GENERIC_ERROR";

	public static enum Type {
		MetaJsonSql, OrganizationJsonSql
	}

	public static void broadcastSuccess(final Context context, final Type type, final String... categories) {
		broadcastSuccess(context, type, null, categories);
	}

	public static void broadcastSuccess(final Context context, final Type type, final Serializable data, final String... categories) {
		final Intent intent = new Intent(NOTIFY_GENERIC_SUCCESS + "_" + type.toString());
		addCategories(intent, categories);
		if (data != null) {
			intent.putExtra(MissionHubBroadcast.PREFIX + "data", data);
		}
		sendBroadcast(context, intent);
	}

	public static void broadcastError(final Context context, final Type type, final Throwable throwable, final String... categories) {
		broadcastError(context, type, null, throwable, categories);
	}

	public static void broadcastError(final Context context, final Type type, final Serializable data, final Throwable throwable, final String... categories) {
		final Intent intent = new Intent(NOTIFY_GENERIC_ERROR + "_" + type.toString());
		addCategories(intent, categories);
		if (data != null) {
			intent.putExtra(MissionHubBroadcast.PREFIX + "data", data);
		}
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", throwable);
		sendBroadcast(context, intent);
	}
}