package com.missionhub.fragment;

import greendroid.widget.ItemAdapter;
import android.app.Activity;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.MissionHubBaseActivity;
import com.missionhub.MissionHubMainActivity;
import com.missionhub.ui.MainMenu;
import com.missionhub.ui.widget.item.NavigationItem;

/**
 * The base MissionHubFragment
 */
public class MissionHubFragment extends SherlockFragment {

	/**
	 * Returns the activity cast to the MissionHubBaseActivity
	 */
	public MissionHubBaseActivity getMHActivity() {
		return (MissionHubBaseActivity) getSherlockActivity();
	}

	/**
	 * Sets the menu item to use as a indeterminate progress bar
	 * 
	 * @param item
	 */
	public void setSupportProgressBarIndeterminateItem(final MenuItem item) {
		getMHActivity().setSupportProgressBarIndeterminateItem(item);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		addToMenu(getMHActivity());
	}

	/**
	 * Adds the fragment to the MainMenu
	 * 
	 * @param activity
	 */
	private void addToMenu(final Activity activity) {
		if (activity instanceof MissionHubMainActivity) {
			final MainMenu menu = ((MissionHubMainActivity) activity).getMenu();
			if (menu != null) {
				menu.addFragment(this);
			}
		}
	}

	@Override
	public void onDetach() {
		final Activity activity = getSherlockActivity();
		if (activity != null) {
			if (activity instanceof MissionHubMainActivity) {
				final MainMenu menu = ((MissionHubMainActivity) activity).getMenu();
				if (menu != null) {
					menu.removeFragment(this);
				}
			}
		}
		super.onDetach();
	}

	/**
	 * Called when the navigation menu is created. Used to override or add items
	 * to the navigation menu.
	 * 
	 * @param adapter
	 */
	public void onCreateMainMenu(final MainMenu menu, final ItemAdapter adapter) {

	}

	/**
	 * Called when a navigation item is selected
	 * 
	 * @param itemPosition
	 * @param itemId
	 * @return true if event was handled
	 */
	public boolean onNavigationItemSelected(final NavigationItem item) {
		return false;
	}
}
