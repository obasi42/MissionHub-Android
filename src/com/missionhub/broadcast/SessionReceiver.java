package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionReceiver extends MissionHubReceiver {

	public SessionReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] actions = { SessionBroadcast.ACTION_LOGIN, SessionBroadcast.ACTION_LOGOUT, 
				SessionBroadcast.ACTION_VERIFY_START, SessionBroadcast.ACTION_VERIFY_PASS, SessionBroadcast.ACTION_VERIFY_FAIL
		};
		return actions;
	}

	public void onLogin(final String accessToken) {}

	public void onLogout() {}
	
	public void onVerifyStart() {}
	
	public void onVerifyPass() {}
	
	public void onVerifyFail(final Throwable throwable) {}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(SessionBroadcast.ACTION_LOGIN)) {
			onLogin(intent.getStringExtra("accessToken"));
		} else if (intent.getAction().equals(SessionBroadcast.ACTION_LOGOUT)) {
			onLogout();
		} else if (intent.getAction().equals(SessionBroadcast.ACTION_VERIFY_START)) {
			onVerifyStart();
		} else if (intent.getAction().equals(SessionBroadcast.ACTION_VERIFY_PASS)) {
			onVerifyPass();
		} else if (intent.getAction().equals(SessionBroadcast.ACTION_VERIFY_FAIL)) {
			onVerifyFail((Throwable) intent.getSerializableExtra("throwable"));
		}
	}
}