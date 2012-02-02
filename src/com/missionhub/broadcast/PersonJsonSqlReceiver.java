package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class PersonJsonSqlReceiver extends MissionHubReceiver {

	public PersonJsonSqlReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] NOTIFYs = { PersonJsonSqlBroadcast.NOTIFY_PERSON_CREATE,  PersonJsonSqlBroadcast.NOTIFY_PERSON_UPDATE, PersonJsonSqlBroadcast.NOTIFY_PERSON_DELETE, PersonJsonSqlBroadcast.NOTIFY_PERSON_ERROR};
		return NOTIFYs;
	}
	
	public void onCreate(int personId) {
		
	}
	
	public void onUpdate(int personId) {
		
	}
	
	public void onDelete(int personId) {
		
	}
	
	public void onError(int personId, Throwable t) {
		
	}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(PersonJsonSqlBroadcast.NOTIFY_PERSON_CREATE)) {
			onCreate(intent.getIntExtra(MissionHubBroadcast.PREFIX + "personId", -1));
		} else if (intent.getAction().equals(PersonJsonSqlBroadcast.NOTIFY_PERSON_UPDATE)) {
			onUpdate(intent.getIntExtra(MissionHubBroadcast.PREFIX + "personId", -1));
		} else if (intent.getAction().equals(PersonJsonSqlBroadcast.NOTIFY_PERSON_DELETE)) {
			onDelete(intent.getIntExtra(MissionHubBroadcast.PREFIX + "personId", -1));
		} else if (intent.getAction().equals(PersonJsonSqlBroadcast.NOTIFY_PERSON_ERROR)) {
			onError(intent.getIntExtra(MissionHubBroadcast.PREFIX + "personId", -1), (Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}