package com.missionhub.broadcast;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.content.Intent;

public class GenericCUDEReceiver extends MissionHubReceiver {

	private final Type type;

	public GenericCUDEReceiver(final Context context, final Type type) {
		super(context);
		this.type = type;
	}

	@Override
	public String[] getAllActions() {
		final String[] NOTIFYs = { GenericCUDEBroadcast.NOTIFY_GENERIC_CREATE + "_" + type.getClass().getSimpleName(),
				GenericCUDEBroadcast.NOTIFY_GENERIC_UPDATE + "_" + type.getClass().getSimpleName(),
				GenericCUDEBroadcast.NOTIFY_GENERIC_DELETE + "_" + type.getClass().getSimpleName(),
				GenericCUDEBroadcast.NOTIFY_GENERIC_ERROR + "_" + type.getClass().getSimpleName() };
		return NOTIFYs;
	}

	public void onCreate(final long[] rowIds) {

	}

	public void onUpdate(final long[] rowIds) {

	}

	public void onDelete(final long[] rowIds) {

	}

	public void onError(final long[] rowIds, final Throwable t) {

	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(GenericCUDEBroadcast.NOTIFY_GENERIC_CREATE + "_" + type.getClass().getSimpleName())) {
			onCreate(intent.getLongArrayExtra(MissionHubBroadcast.PREFIX + "rowIds"));
		} else if (intent.getAction().equals(GenericCUDEBroadcast.NOTIFY_GENERIC_UPDATE + "_" + type.getClass().getSimpleName())) {
			onUpdate(intent.getLongArrayExtra(MissionHubBroadcast.PREFIX + "rowIds"));
		} else if (intent.getAction().equals(GenericCUDEBroadcast.NOTIFY_GENERIC_DELETE + "_" + type.getClass().getSimpleName())) {
			onDelete(intent.getLongArrayExtra(MissionHubBroadcast.PREFIX + "rowIds"));
		} else if (intent.getAction().equals(GenericCUDEBroadcast.NOTIFY_GENERIC_ERROR + "_" + type.getClass().getSimpleName())) {
			onError(intent.getLongArrayExtra(MissionHubBroadcast.PREFIX + "rowIds"), (Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}

	@Override
	public void register(final String... actions) {
		super.register(appendType(actions));
	}

	@Override
	public void register(final String action, final List<String> categories) {
		super.register(action + "_" + type.getClass().getSimpleName());
	}

	@Override
	public void register(final List<String> categories, final String... actions) {
		super.register(categories, appendType(actions));
	}

	private String[] appendType(final String... actions) {
		final String[] a = new String[actions.length];
		for (int i = 0; i < actions.length; i++) {
			a[i] = actions[i] + "_" + type.getClass().getSimpleName();
		}
		return a;
	}
}