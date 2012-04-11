package com.missionhub;

import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.widget.item.NavigationItem;

import android.os.Bundle;

public class PeopleAllActivity extends MissionHubMainActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.widget_refresh_button);
	}

	@Override
	public void onCreateNavigationMenu(NavigationMenu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onNavigationItemSelected(NavigationItem item) {
		// TODO Auto-generated method stub
		return false;
	}

}