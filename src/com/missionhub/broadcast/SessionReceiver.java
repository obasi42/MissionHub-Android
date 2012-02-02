package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionReceiver extends MissionHubReceiver {

	public SessionReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] NOTIFYs = { SessionBroadcast.NOTIFY_LOGIN, SessionBroadcast.NOTIFY_LOGOUT, SessionBroadcast.NOTIFY_VERIFY_START, SessionBroadcast.NOTIFY_VERIFY_PASS,
				SessionBroadcast.NOTIFY_VERIFY_FAIL };
		return NOTIFYs;
	}

	public void onLogin(final String accessToken) {}

	public void onLogout() {}

	public void onVerifyStart() {}

	public void onVerifyPass() {}

	public void onVerifyFail(final Throwable throwable) {}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(SessionBroadcast.NOTIFY_LOGIN)) {
			onLogin(intent.getStringExtra(MissionHubBroadcast.PREFIX + "accessToken"));
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_LOGOUT)) {
			onLogout();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_VERIFY_START)) {
			onVerifyStart();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_VERIFY_PASS)) {
			onVerifyPass();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_VERIFY_FAIL)) {
			onVerifyFail((Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}