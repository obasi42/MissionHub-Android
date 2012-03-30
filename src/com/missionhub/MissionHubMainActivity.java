package com.missionhub;

import android.content.Intent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.widget.item.NavigationItem;

/**
 * The main activity for MissionHub. Manages the Action Bar and fragment
 * loading.
 */
public class MissionHubMainActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = MissionHubMainActivity.class.getSimpleName();

	/** the navigation menu */
	private final NavigationMenu mNavigationMenu = new NavigationMenu(this);

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
		case R.string.action_logout:
			getMHApplication().reset();
			startActivity(new Intent(this, MissionHubActivity.class));
			finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	public boolean onNavigationItemSelected(final NavigationItem item) {
		return false;
	}

	public NavigationMenu getNavigationMenu() {
		return mNavigationMenu;
	}
}