package com.missionhub.android.broadcast;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.content.Intent;

public class GenericCUDEBroadcast extends MissionHubBroadcast {

	public static final String NOTIFY_GENERIC_CREATE = MissionHubBroadcast.PREFIX + "NOTIFY_GENERIC_CREATE";
	public static final String NOTIFY_GENERIC_UPDATE = MissionHubBroadcast.PREFIX + "NOTIFY_GENERIC_UPDATE";
	public static final String NOTIFY_GENERIC_DELETE = MissionHubBroadcast.PREFIX + "NOTIFY_GENERIC_DELETE";
	public static final String NOTIFY_GENERIC_ERROR = MissionHubBroadcast.PREFIX + "NOTIFY_GENERIC_ERROR";

	public static void broadcastCreate(final Context context, final Type table, final long rowId, final String... categories) {
		broadcastCreate(context, table, new long[] { rowId }, categories);
	}

	public static void broadcastCreate(final Context context, final Type table, final List<Long> rowIds, final String... categories) {
		broadcastCreate(context, table, toLongArray(rowIds), categories);
	}

	public static void broadcastCreate(final Context context, final Type table, final long[] rowIds, final String... categories) {
		final Intent intent = new Intent(NOTIFY_GENERIC_CREATE + "_" + table.getClass().getSimpleName());
		intent.putExtra(MissionHubBroadcast.PREFIX + "rowIds", rowIds);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	public static void broadcastUpdate(final Context context, final Type table, final long rowId, final String... categories) {
		broadcastUpdate(context, table, new long[] { rowId }, categories);
	}

	public static void broadcastUpdate(final Context context, final Type table, final List<Long> rowIds, final String... categories) {
		broadcastUpdate(context, table, toLongArray(rowIds), categories);
	}

	public static void broadcastUpdate(final Context context, final Type table, final long[] rowIds, final String... categories) {
		final Intent intent = new Intent(NOTIFY_GENERIC_UPDATE + "_" + table.getClass().getSimpleName());
		intent.putExtra(MissionHubBroadcast.PREFIX + "rowIds", rowIds);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	public static void broadcastDelete(final Context context, final Type table, final long rowId, final String... categories) {
		broadcastDelete(context, table, new long[] { rowId }, categories);
	}

	public static void broadcastDelete(final Context context, final Type table, final List<Long> rowIds, final String... categories) {
		broadcastDelete(context, table, toLongArray(rowIds), categories);
	}

	public static void broadcastDelete(final Context context, final Type table, final long[] rowIds, final String... categories) {
		final Intent intent = new Intent(NOTIFY_GENERIC_DELETE + "_" + table.getClass().getSimpleName());
		intent.putExtra(MissionHubBroadcast.PREFIX + "rowIds", rowIds);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	public static void broadcastError(final Context context, final Type table, final long rowId, final Throwable t, final String... categories) {
		broadcastError(context, table, new long[] { rowId }, t, categories);
	}

	public static void broadcastError(final Context context, final Type table, final List<Long> rowIds, final Throwable t, final String... categories) {
		broadcastError(context, table, toLongArray(rowIds), t, categories);
	}

	public static void broadcastError(final Context context, final Type table, final long[] rowIds, final Throwable t, final String... categories) {
		final Intent intent = new Intent(NOTIFY_GENERIC_ERROR + "_" + table.getClass().getSimpleName());
		intent.putExtra(MissionHubBroadcast.PREFIX + "rowIds", rowIds);
		intent.putExtra(MissionHubBroadcast.PREFIX + "throwable", t);
		addCategories(intent, categories);
		sendBroadcast(context, intent);
	}

	private static long[] toLongArray(final List<Long> rowIds) {
		final long[] ids = new long[rowIds.size()];
		for (int i = 0; i < rowIds.size(); i++) {
			ids[i] = rowIds.get(i);
		}
		return ids;
	}
}