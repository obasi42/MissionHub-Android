package com.missionhub.activity;

import roboguice.inject.ContentView;

import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.fragment.PreferencesFragment;

import android.os.Bundle;

@ContentView(R.layout.content_frame)
public class PreferencesActivity extends BaseAuthenticatedActivity {
	
	private PreferencesFragment mFragment;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState != null) {
			mFragment = (PreferencesFragment) getSupportFragmentManager().findFragmentById(R.layout.content_frame);
		} else {
			mFragment = new PreferencesFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mFragment).commit();
		}
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}	
}