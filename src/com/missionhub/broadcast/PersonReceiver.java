package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class PersonReceiver extends MissionHubReceiver {

	public PersonReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] NOTIFYs = { PersonBroadcast.NOTIFY_PERSON_CREATE,  PersonBroadcast.NOTIFY_PERSON_UPDATE, PersonBroadcast.NOTIFY_PERSON_DELETE, PersonBroadcast.NOTIFY_PERSON_ERROR};
		return NOTIFYs;
	}
	
	public void onCreate(long personId) {
		
	}
	
	public void onUpdate(long personId) {
		
	}
	
	public void onDelete(long personId) {
		
	}
	
	public void onError(long personId, Throwable t) {
		
	}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(PersonBroadcast.NOTIFY_PERSON_CREATE)) {
			onCreate(intent.getLongExtra(MissionHubBroadcast.PREFIX + "personId", -1));
		} else if (intent.getAction().equals(PersonBroadcast.NOTIFY_PERSON_UPDATE)) {
			onUpdate(intent.getLongExtra(MissionHubBroadcast.PREFIX + "personId", -1));
		} else if (intent.getAction().equals(PersonBroadcast.NOTIFY_PERSON_DELETE)) {
			onDelete(intent.getLongExtra(MissionHubBroadcast.PREFIX + "personId", -1));
		} else if (intent.getAction().equals(PersonBroadcast.NOTIFY_PERSON_ERROR)) {
			onError(intent.getLongExtra(MissionHubBroadcast.PREFIX + "personId", -1), (Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}