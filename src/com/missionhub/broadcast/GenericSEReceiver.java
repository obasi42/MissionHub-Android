package com.missionhub.broadcast;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;

public class GenericSEReceiver extends MissionHubReceiver {

	private final GenericSEBroadcast.Type type;

	public GenericSEReceiver(final Context context, final GenericSEBroadcast.Type type) {
		super(context);
		this.type = type;
	}

	@Override
	public String[] getAllActions() {
		final String[] NOTIFYs = { GenericSEBroadcast.NOTIFY_GENERIC_SUCCESS + "_" + type.toString(), GenericSEBroadcast.NOTIFY_GENERIC_ERROR + "_" + type.toString() };
		return NOTIFYs;
	}

	public void onUpdateSuccess() {}

	public void onUpdateError(final Throwable throwable) {}

	public void onUpdateSuccess(final Serializable data) {}

	public void onUpdateError(final Serializable data, final Throwable throwable) {}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(GenericSEBroadcast.NOTIFY_GENERIC_SUCCESS + "_" + type.toString())) {
			onUpdateSuccess();
			onUpdateSuccess(intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "data"));
		} else if (intent.getAction().equals(GenericSEBroadcast.NOTIFY_GENERIC_ERROR + "_" + type.toString())) {
			onUpdateError((Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
			onUpdateError(intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "data"), (Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}

	@Override
	public void register(final String... actions) {
		super.register(appendType(actions));
	}

	@Override
	public void register(final String action, final List<String> categories) {
		super.register(action + "_" + type.toString(), categories);
	}

	@Override
	public void register(final List<String> categories, final String... actions) {
		super.register(categories, appendType(actions));
	}

	private String[] appendType(final String... actions) {
		final String[] a = new String[actions.length];
		for (int i = 0; i < actions.length; i++) {
			a[i] = actions[i] + "_" + type.toString();
		}
		return a;
	}
}