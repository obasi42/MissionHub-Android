package com.missionhub;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.NavigationMenu.NavigationMenuActivity;

/**
 * The main activity for MissionHub. Manages the Action Bar and fragment
 * loading.
 */
public abstract class MissionHubMainActivity extends MissionHubBaseActivity implements NavigationMenuActivity {

	/** logging tag */
	public static final String TAG = MissionHubMainActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NavigationMenu.instantiate(this);
	}

	/** Global menu items */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(Menu.NONE, R.string.action_settings, 98, R.string.action_settings).setIcon(R.drawable.ic_action_logout).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(Menu.NONE, R.string.action_logout, 99, R.string.action_logout).setIcon(R.drawable.ic_action_logout).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.string.action_settings:
			final Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.string.action_logout:
			getMHApplication().reset();
			startActivity(new Intent(this, MissionHubActivity.class));
			finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
}