package com.missionhub.android.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionReceiver extends MissionHubReceiver {

	public SessionReceiver(final Context context) {
		super(context);
	}

	@Override
	public String[] getAllActions() {
		final String[] NOTIFYs = { SessionBroadcast.NOTIFY_LOGIN, SessionBroadcast.NOTIFY_LOGOUT, SessionBroadcast.NOTIFY_SESSION_UPDATE_START,
				SessionBroadcast.NOTIFY_SESSION_UPDATE_SUCCESS, SessionBroadcast.NOTIFY_SESSION_UPDATE_ERROR };
		return NOTIFYs;
	}

	public void onLogin(final String accessToken) {}

	public void onLogout() {}

	public void onUpdateStart() {}

	public void onUpdateSuccess() {}

	public void onUpdateError(final Throwable throwable) {}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(SessionBroadcast.NOTIFY_LOGIN)) {
			onLogin(intent.getStringExtra(MissionHubBroadcast.PREFIX + "accessToken"));
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_LOGOUT)) {
			onLogout();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_SESSION_UPDATE_START)) {
			onUpdateStart();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_SESSION_UPDATE_SUCCESS)) {
			onUpdateSuccess();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_SESSION_UPDATE_ERROR)) {
			onUpdateError((Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}