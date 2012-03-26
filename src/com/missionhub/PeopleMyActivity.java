package com.missionhub;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.ui.MainMenu;

import android.os.Bundle;
import android.util.Log;

public class PeopleMyActivity extends MissionHubBaseActivity implements OnNavigationListener {

	/** logging tag */
	public static final String TAG = PeopleMyActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainMenu.initialize(this, MainMenu.PEOPLE_MY);
	}
	
	/** Creates the menu bar items */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, R.string.action_add, 0, R.string.action_add).setIcon(R.drawable.ic_action_user_add).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		MenuItem refresh = menu.add(Menu.NONE, R.string.action_refresh, 1, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
		setSupportProgressBarIndeterminateItem(refresh);	
		menu.add(Menu.NONE, R.string.action_settings, 2, R.string.action_settings).setIcon(R.drawable.ic_action_logout).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(Menu.NONE, R.string.action_logout, 3, R.string.action_logout).setIcon(R.drawable.ic_action_logout).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
		
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.e("Selected", "" + itemPosition);
        return true;
    }
	
	private boolean visible = false;
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.string.action_add:
			visible = !visible;
			setSupportProgressBarIndeterminateVisibility(visible);
			return true;
		default: return false;
		}
	}
}