package com.missionhub.ui;

import android.content.Context;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubMainActivity;
import com.missionhub.fragment.NavigationMenuFragment;
import com.missionhub.ui.widget.item.NavigationDividerItem;
import com.missionhub.ui.widget.item.NavigationItem;
import com.missionhub.ui.widget.item.SpinnerItem;
import com.missionhub.ui.widget.item.SpinnerItem.OnSpinnerItemChangedListener;

/**
 * The MissionHub Main Action Bar List Menu
 */
public class NavigationMenu implements OnNavigationListener, OnSpinnerItemChangedListener {

	/** the activity */
	private final MissionHubMainActivity mActivity;

	/** the navigation menu interface */
	private final NavigationMenuInterface mNavigationMenuInterface;

	/** the navigation menu fragment interface */
	private final NavigationMenuFragmentInterface mNavigationMenuFragmentInterface;

	/** the navigation selected listener */
	private final OnNavigationItemSelectedListener mNavigationSelectedListener;

	/** the navigation list adapter */
	private final SpinnerItemAdapter mAdapter;

	/** if the menu is in setup stage */
	private boolean mInSetup;

	/** the currently selected item */
	private NavigationItem mSelectedItem;

	/**
	 * Creates a new Navigation Menu
	 * 
	 * @param activity
	 */
	public NavigationMenu(final MissionHubMainActivity activity) {
		this(activity, activity, activity, null);
	}

	public NavigationMenu(final MissionHubMainActivity activity, final NavigationMenuFragment fragment) {
		this(activity, null, null, fragment);
	}

	/**
	 * Creates a new NavigationMenu
	 * 
	 * @param activity
	 */
	public NavigationMenu(final MissionHubMainActivity activity, final NavigationMenuInterface navigationMenuInterface,
			final OnNavigationItemSelectedListener navigationItemSelectedListener, final NavigationMenuFragmentInterface navigationMenuFragmentInterface) {
		mActivity = activity;

		mNavigationMenuInterface = navigationMenuInterface;
		mNavigationMenuFragmentInterface = navigationMenuFragmentInterface;
		mNavigationSelectedListener = navigationItemSelectedListener;

		mAdapter = new SpinnerItemAdapter(mActivity);

		setup();
	}

	/**
	 * Sets up the navigation menu items and attaches it to the action bar
	 */
	private void setup() {
		mInSetup = true;

		mAdapter.setNotifyOnChange(false);

		// main menu
		if (mNavigationMenuInterface != null) {
			mNavigationMenuInterface.onCreateNavigationMenu(this);
			if (!mAdapter.isEmpty()) {
				mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				mActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				mActivity.getSupportActionBar().setListNavigationCallbacks(mAdapter, this);
			}
		}

		// fragment menu
		if (mNavigationMenuFragmentInterface != null) {
			mNavigationMenuFragmentInterface.onCreateNavigationMenu(this);
			mNavigationMenuFragmentInterface.setAdapter(mAdapter);
		}

		mInSetup = false;
	}

	/**
	 * Passes navigation selection events to OnNavigationItemSelectedListener
	 * interface
	 */
	@Override
	public synchronized boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		if (mNavigationSelectedListener != null) {
			final NavigationItem item = (NavigationItem) mAdapter.getItem(itemPosition);
			if (item != null && item != mSelectedItem) {
				mSelectedItem = item;
				mNavigationSelectedListener.onNavigationItemSelected(item);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the selected item
	 * 
	 * @return
	 */
	public NavigationItem getSelectedItem() {
		return mSelectedItem;
	}

	/**
	 * Sets the selected navigation item
	 * 
	 * @param item
	 */
	public void setSelectedItem(final NavigationItem item) {
		if (item != null && mSelectedItem != item) {
			if (isFragmentMenu()) {
				mSelectedItem = item;
				mNavigationMenuFragmentInterface.setSelectedNavigationItem(item);
			} else {
				final int position = findPositionByItemId(mAdapter, item.getId());
				if (position > -1) {
					mActivity.getSupportActionBar().setSelectedNavigationItem(position);
				}
			}
		}
	}

	/**
	 * Adds a navigation item to the list
	 * 
	 * @param itemId
	 * @return the navigation item
	 */
	public NavigationItem add(final int itemId) {
		final NavigationItem item = new NavigationItem(itemId, this);
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
		final NavigationDividerItem item = new NavigationDividerItem(itemId, this);
		mAdapter.add(item);
		return item;
	}

	/**
	 * Removes a navigation item from the list
	 * 
	 * @param itemId
	 */
	public void remove(final int itemId) {
		final int position = findPositionByItemId(mAdapter, itemId);
		remove((SpinnerItem) mAdapter.getItem(position));
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
	public interface NavigationMenuInterface {
		public void onCreateNavigationMenu(NavigationMenu menu);
	}

	/**
	 * Activities that implement the NavigationMenu must implement this
	 */
	public interface NavigationMenuFragmentInterface {
		public void onCreateNavigationMenu(NavigationMenu navigationMenu);

		public void setAdapter(SpinnerItemAdapter adapter);

		public void setSelectedNavigationItem(NavigationItem item);
	}

	/**
	 * Interface for receiving navigation selection events
	 */
	public interface OnNavigationItemSelectedListener {
		public boolean onNavigationItemSelected(NavigationItem item);
	}

	/**
	 * Hide a navigation item by its id
	 * 
	 * @param itemId
	 */
	public void hide(final int itemId) {
		mAdapter.hide(findItemById(mAdapter, itemId), true);
	}

	/**
	 * Show a navigation item by its id
	 * 
	 * @param itemId
	 */
	public void show(final int itemId) {
		mAdapter.show(findItemById(mAdapter, itemId), true);
	}

	/**
	 * Hide a specified navigation item
	 * 
	 * @param item
	 */
	public void hide(final NavigationItem item) {
		hide(item.getId());
	}

	/**
	 * Show the specified navigation item
	 * 
	 * @param item
	 */
	public void show(final NavigationItem item) {
		show(item.getId());
	}

	/**
	 * Show all hidden navigation items
	 */
	public void showAll() {
		mAdapter.showAll();
	}

	/**
	 * @return the context of the navigation menu
	 */
	public Context getContext() {
		return mActivity;
	}

	/**
	 * @return true if the navigation menu is part of a fragment menu
	 */
	public boolean isFragmentMenu() {
		if (mNavigationMenuFragmentInterface != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the navigation item from the id
	 * @param id
	 * @return
	 */
	public NavigationItem findNavigationItemById(int id) {
		return (NavigationItem) findItemById(mAdapter, id);
	}
	
	/**
	 * Helper method for finding the navigation item in the adapter by the
	 * position
	 * 
	 * @param adapter
	 * @param position
	 * @return
	 */
	public static SpinnerItem findItemByPosition(final SpinnerItemAdapter adapter, final int position) {
		try {
			return (SpinnerItem) adapter.getItem(position);
		} catch (final Exception e) {}
		return null;
	}

	/**
	 * Helper method for finding the navigation item in the adapter by the item
	 * id
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 */
	public static SpinnerItem findItemById(final SpinnerItemAdapter adapter, final int id) {
		return findItemByPosition(adapter, findPositionByItemId(adapter, id));
	}

	/**
	 * Helper method for finding the position of a navigation item id
	 * 
	 * @param adapter
	 * @param id
	 * @return
	 */
	public static int findPositionByItemId(final SpinnerItemAdapter adapter, final int id) {
		for (int i = 0; i < adapter.getCount(); i++) {
			final SpinnerItem item = (SpinnerItem) adapter.getItem(i);
			if (item instanceof NavigationItem) {
				if (((NavigationItem) item).getId() == id) {
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

	/**
	 * Helper method for finding the position of a navigation item
	 * 
	 * @param adapter
	 * @param item
	 * @return
	 */
	public static int findPositionByItem(final SpinnerItemAdapter adapter, final NavigationItem item) {
		return findPositionByItemId(adapter, item.getId());
	}

	/**
	 * Helper method for finding the position of a navigation item
	 * 
	 * @param adapter
	 * @param item
	 * @return
	 */
	public static int findPositionByItem(final SpinnerItemAdapter adapter, final NavigationDividerItem dividerItem) {
		return findPositionByItemId(adapter, dividerItem.getId());
	}
}