package com.missionhub;

import android.os.Bundle;

import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.widget.item.NavigationItem;

public class PeopleAllActivity extends MissionHubMainActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.widget_refresh_button);
	}

	@Override
	public void onCreateNavigationMenu(final NavigationMenu menu) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNavigationItemSelected(final NavigationItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreateSideNavigationMenu(NavigationMenu menu) {
		// TODO Auto-generated method stub
		
	}

}