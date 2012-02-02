package com.missionhub;

import roboguice.activity.RoboFragmentActivity;

public class MissionHubBaseActivity extends RoboFragmentActivity {

	public Session getSession() {
		return ((MissionHubApplication) getApplicationContext()).getSession();
	}
	
}