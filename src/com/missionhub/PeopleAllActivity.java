package com.missionhub;

import android.os.Bundle;

public class PeopleAllActivity extends MissionHubMainActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.widget_refresh_button);

		getNavigationMenu().attach(this.getClass());
	}

}