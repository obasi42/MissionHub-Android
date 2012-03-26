package com.missionhub;

import com.missionhub.ui.MainMenu;

import android.os.Bundle;

public class PeopleDirectoryActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = PeopleDirectoryActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainMenu.initialize(this, MainMenu.PEOPLE_DIRECTORY);
	}
}