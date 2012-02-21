package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class SessionReceiver extends MissionHubReceiver {

	public SessionReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] NOTIFYs = { SessionBroadcast.NOTIFY_LOGIN, SessionBroadcast.NOTIFY_LOGOUT, SessionBroadcast.NOTIFY_UPDATE_PERSON_START,
				SessionBroadcast.NOTIFY_UPDATE_PERSON_SUCCESS, SessionBroadcast.NOTIFY_UPDATE_PERSON_ERROR, SessionBroadcast.NOTIFY_UPDATE_ORGANIZATIONS_START,
				SessionBroadcast.NOTIFY_UPDATE_ORGANIZATIONS_SUCCESS, SessionBroadcast.NOTIFY_UPDATE_ORGANIZATIONS_ERROR };
		return NOTIFYs;
	}

	public void onLogin(final String accessToken) {}

	public void onLogout() {}
	
	public void onUpdatePersonStart() {}
	
	public void onUpdatePersonSuccess() {}
	
	public void onUpdatePersonError(final Throwable throwable) {}
	
	public void onUpdateOrganizationsStart() {}
	
	public void onUpdateOrganizationsSuccess() {}
	
	public void onUpdateOrganizationsError(final Throwable throwable) {}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(SessionBroadcast.NOTIFY_LOGIN)) {
			onLogin(intent.getStringExtra(MissionHubBroadcast.PREFIX + "accessToken"));
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_LOGOUT)) {
			onLogout();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_UPDATE_PERSON_START)) {
			onUpdatePersonStart();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_UPDATE_PERSON_SUCCESS)) {
			onUpdatePersonSuccess();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_UPDATE_PERSON_ERROR)) {
			onUpdatePersonError((Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_UPDATE_ORGANIZATIONS_START)) {
			onUpdateOrganizationsStart();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_UPDATE_ORGANIZATIONS_SUCCESS)) {
			onUpdateOrganizationsSuccess();
		} else if (intent.getAction().equals(SessionBroadcast.NOTIFY_UPDATE_ORGANIZATIONS_ERROR)) {
			onUpdateOrganizationsError((Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}