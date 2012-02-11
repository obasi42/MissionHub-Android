package com.missionhub;

import roboguice.activity.RoboFragmentActivity;

public class MissionHubBaseActivity extends RoboFragmentActivity {

	public Session getSession() {
		return ((MissionHubApplication) getApplicationContext()).getSession();
	}
	
	public void showProgress() {
		setSupportProgressBarIndeterminateVisibility(true);
	}
	
	public void hideProgress() {
		setSupportProgressBarIndeterminateVisibility(false);
	}

}