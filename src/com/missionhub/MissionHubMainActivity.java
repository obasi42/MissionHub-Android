package com.missionhub;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.ui.NavigationMenu;
import com.missionhub.ui.NavigationMenu.NavigationMenuInterface;
import com.missionhub.ui.NavigationMenu.OnNavigationItemSelectedListener;
import com.missionhub.ui.widget.item.NavigationItem;

/**
 * The main activity for MissionHub. Manages the Action Bar and fragment
 * loading.
 */
public abstract class MissionHubMainActivity extends MissionHubBaseActivity implements NavigationMenuInterface, OnNavigationItemSelectedListener {

	/** logging tag */
	public static final String TAG = MissionHubMainActivity.class.getSimpleName();

	/** the navigation menu */
	private NavigationMenu mNavigationMenu;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNavigationMenu = new NavigationMenu(this);
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

	/**
	 * Returns the navigation menu
	 * 
	 * @return
	 */
	public NavigationMenu getNavigationMenu() {
		return mNavigationMenu;
	}

	@Override
	public void onCreateNavigationMenu(final NavigationMenu menu) {
		menu.add(R.id.nav_my_contacts).setTitle("My Contacts");
		menu.add(R.id.nav_all_contacts).setTitle("All Contacts");
		menu.add(R.id.nav_directory).setTitle("Directory");
		menu.add(R.id.nav_groups).setTitle("Groups");
		menu.add(R.id.nav_surveys).setTitle("Surveys");
	}
}