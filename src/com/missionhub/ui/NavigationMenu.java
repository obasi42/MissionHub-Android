package com.missionhub.ui;

import android.graphics.drawable.Drawable;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubBaseActivity;
import com.missionhub.R;
import com.missionhub.ui.widget.item.NavigationItem;

/**
 * The MissionHub Main Action Bar List Menu
 */
public class NavigationMenu implements OnNavigationListener {

	/** the context */
	private final MissionHubBaseActivity mActivity;

	/** the navigation menu activity interface */
	private final NavigationMenuActivity mNavigationMenuInterface;

	/** the navigation list adapter */
	private final ListItemAdapter mAdapter;

	/** logo cache */
	private static Drawable mLogo;

	/**
	 * Creates a MainMenu Object
	 */
	public NavigationMenu(final MissionHubBaseActivity activity) {
		if (!(activity instanceof NavigationMenuActivity)) {
			throw new RuntimeException("NavigationMenu context must implement the NavigationMenuActivity interface");
		}

		mActivity = activity;
		mNavigationMenuInterface = (NavigationMenuActivity) mActivity;
		mAdapter = new ListItemAdapter(mActivity);

		setup();
	}

	/**
	 * Instantiate a new NavigationMenu
	 * 
	 * @param activity
	 */
	public static void instantiate(final MissionHubBaseActivity activity) {
		new NavigationMenu(activity);
	}

	/**
	 * Sets up the navigation menu items and attaches it to the action bar
	 */
	private void setup() {
		mAdapter.setNotifyOnChange(false);
		mNavigationMenuInterface.onCreateNavigationMenu(this);
		if (!mAdapter.isEmpty()) {
			mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
			if (mActivity.getDisplayMode().isTablet()) {
				mActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
				if (mLogo == null) {
					mActivity.getResources().getDrawable(R.drawable.logo);
				}
				mActivity.getSupportActionBar().setLogo(mLogo);
			}
			mActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			mActivity.getSupportActionBar().setListNavigationCallbacks(mAdapter, this);
		}
	}

	/**
	 * Passes navigation selection events to NavigationMenuActivity interface
	 */
	@Override
	public synchronized boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		final NavigationItem item = (NavigationItem) mAdapter.getItem(itemPosition);
		if (item != null) {
			return mNavigationMenuInterface.onNavigationItemSelected(item);
		}
		return false;
	}

	/**
	 * Adds a menu item to the menu
	 * 
	 * @param itemId
	 * @return the menu item
	 */
	public NavigationItem add(final int itemId) {
		return new NavigationItem(itemId, mActivity);
	}

	/**
	 * Interface for NavigationMenu events
	 */
	public interface NavigationMenuActivity {
		public void onCreateNavigationMenu(NavigationMenu menu);

		public boolean onNavigationItemSelected(NavigationItem item);
	}
}