package com.missionhub.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubBaseActivity;
import com.missionhub.ui.widget.item.NavigationDividerItem;
import com.missionhub.ui.widget.item.NavigationItem;
import com.missionhub.ui.widget.item.SpinnerItem;
import com.missionhub.ui.widget.item.SpinnerItem.OnSpinnerItemChangedListener;

/**
 * The MissionHub Main Action Bar List Menu
 */
public class NavigationMenu implements OnNavigationListener, OnSpinnerItemChangedListener {

	/** the context */
	private final MissionHubBaseActivity mActivity;

	/** the navigation menu activity interface */
	private final NavigationMenuActivity mNavigationMenuInterface;

	/** the navigation selected listener */
	private final OnNavigationItemSelectedListener mNavigationSelectedListener;

	/** the navigation list adapter */
	private final SpinnerItemAdapter mAdapter;

	/** logo cache */
	// private static Drawable mLogo;

	/** if the menu is in setup stage */
	private boolean mInSetup;

	/**
	 * Creates a MainMenu Object
	 */
	public NavigationMenu(final MissionHubBaseActivity activity) {
		if (!(activity instanceof NavigationMenuActivity)) {
			throw new RuntimeException("NavigationMenu context must implement the NavigationMenuActivity interface");
		}
		if (!(activity instanceof OnNavigationItemSelectedListener)) {
			throw new RuntimeException("NavigationMenu context must implement the OnNavigationItemSelectedListener interface");
		}

		mActivity = activity;
		mNavigationMenuInterface = (NavigationMenuActivity) mActivity;
		mNavigationSelectedListener = (OnNavigationItemSelectedListener) mActivity;
		mAdapter = new SpinnerItemAdapter(mActivity);

		setup();
	}

	/**
	 * Instantiate a new NavigationMenu
	 * 
	 * @param activity
	 * @return
	 */
	public static NavigationMenu instantiate(final MissionHubBaseActivity activity) {
		return new NavigationMenu(activity);
	}

	/**
	 * Sets up the navigation menu items and attaches it to the action bar
	 */
	private void setup() {
		mInSetup = true;

		mAdapter.setNotifyOnChange(false);
		mNavigationMenuInterface.onCreateNavigationMenu(this);
		if (!mAdapter.isEmpty()) {
			mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
			// if (mActivity.getDisplayMode().isTablet()) {
			// if (mLogo == null) {
			// mLogo = mActivity.getResources().getDrawable(R.drawable.logo);
			// }
			// mActivity.getSupportActionBar().setLogo(mLogo);
			// mActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
			// }
			mActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			mActivity.getSupportActionBar().setListNavigationCallbacks(mAdapter, this);
		}

		mInSetup = false;
	}

	/**
	 * Passes navigation selection events to OnNavigationItemSelectedListener
	 * interface
	 */
	@Override
	public synchronized boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		final NavigationItem item = (NavigationItem) mAdapter.getItem(itemPosition);
		if (item != null) {
			return mNavigationSelectedListener.onNavigationItemSelected(item);
		}
		return false;
	}

	/**
	 * Adds a navigation item to the list
	 * 
	 * @param itemId
	 * @return the navigation item
	 */
	public NavigationItem add(final int itemId) {
		final NavigationItem item = new NavigationItem(itemId, mActivity, this);
		mAdapter.add(item);
		return item;
	}

	/**
	 * Adds a divider to the list
	 * 
	 * @param itemId
	 * @return
	 */
	public NavigationDividerItem addDivider(final int itemId) {
		final NavigationDividerItem item = new NavigationDividerItem(itemId, mActivity, this);
		mAdapter.add(item);
		return item;
	}

	/**
	 * Removes a navigation item from the list
	 * 
	 * @param itemId
	 */
	public void remove(final int itemId) {
		final int position = findPositionById(itemId);
		remove((NavigationItem) mAdapter.getItem(position));
	}

	/**
	 * Removes a navigation item from the list
	 * 
	 * @param item
	 */
	public void remove(final SpinnerItem item) {
		if (item != null) {
			mAdapter.remove(item);
		}
	}

	@Override
	public void onSpinnerItemChanged(final SpinnerItem item) {
		if (!mInSetup) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Activities that implement the NavigationMenu must implement this
	 */
	public interface NavigationMenuActivity {
		public void onCreateNavigationMenu(NavigationMenu menu);
	}

	/**
	 * Interface for receiving navigation selection events
	 */
	public interface OnNavigationItemSelectedListener {
		public boolean onNavigationItemSelected(NavigationItem item);
	}

	/**
	 * Sets the selected menu item
	 * 
	 * @param item
	 */
	public void setSelectedNavigationItem(final NavigationItem item) {
		setSelectedNavigationItem(item.getItemId());
	}

	/**
	 * Sets the selected menu item by id
	 * 
	 * @param id
	 */
	public void setSelectedNavigationItem(final int id) {
		final int position = findPositionById(id);
		if (position > -1) {
			mActivity.getSupportActionBar().setSelectedNavigationItem(position);
		}
	}

	/**
	 * Returns the NavigationItem from the list by its position
	 * 
	 * @param position
	 * @return
	 */
	public NavigationItem findItemByPosition(final int position) {
		return (NavigationItem) mAdapter.getItem(position);
	}

	/**
	 * Returns the NavigationItem from the list by its id
	 * 
	 * @param id
	 * @return
	 */
	public NavigationItem findItemById(final int id) {
		return findItemByPosition(findPositionById(id));
	}

	/**
	 * Finds the position of the navigation item in the adapter
	 * 
	 * @param id
	 * @return
	 */
	private int findPositionById(final int id) {
		for (int i = 0; i < mAdapter.getCount(); i++) {
			final SpinnerItem item = (SpinnerItem) mAdapter.getItem(i);
			if (item instanceof NavigationItem) {
				if (((NavigationItem) item).getItemId() == id) {
					return i;
				}
			} else if (item instanceof NavigationDividerItem) {
				if (((NavigationDividerItem) item).getId() == id) {
					return i;
				}
			}
		}
		return -1;
	}
}