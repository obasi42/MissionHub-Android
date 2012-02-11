package com.missionhub;

import android.content.Intent;
import android.os.Bundle;

import com.missionhub.api.ApiHelper;

public class DashboardActivity extends MissionHubBaseActivity {
	
	/** logging tag */
	public static final String TAG = DashboardActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_dashboard);
	}
	
}