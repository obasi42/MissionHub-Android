package com.missionhub.ui;

import android.content.Context;

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

	/** is a sidebar menu */
	private final boolean mIsSidebar;

	/** the context */
	private final Context mContext;

	/** the navigation menu activity interface */
	private final NavigationMenuInterface mNavigationMenuInterface;

	/** the navigation selected listener */
	private final OnNavigationItemSelectedListener mNavigationSelectedListener;

	/** the navigation list adapter */
	private final SpinnerItemAdapter mAdapter;

	/** logo cache */
	// private static Drawable mLogo;

	/** if the menu is in setup stage */
	private boolean mInSetup;

	public NavigationMenu(final Context context) {
		this(false, context, (NavigationMenuInterface) context, (OnNavigationItemSelectedListener) context);
	}

	public NavigationMenu(final boolean isSidebar, final Context context, final NavigationMenuInterface intface, final OnNavigationItemSelectedListener listener) {
		mIsSidebar = isSidebar;
		mContext = context;
		mNavigationMenuInterface = intface;
		mNavigationSelectedListener = listener;
		mAdapter = new SpinnerItemAdapter(mContext);

		setup();
	}

	/**
	 * Sets up the navigation menu items and attaches it to the action bar
	 */
	private void setup() {
		mInSetup = true;

		mAdapter.setNotifyOnChange(false);

		if (!mIsSidebar) {
			final MissionHubBaseActivity activity = (MissionHubBaseActivity) mContext;
			mNavigationMenuInterface.onCreateNavigationMenu(this);
			if (!mAdapter.isEmpty()) {
				activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
				// if (mActivity.getDisplayMode().isTablet()) {
				// if (mLogo == null) {
				// mLogo =
				// mActivity.getResources().getDrawable(R.drawable.logo);
				// }
				// mActivity.getSupportActionBar().setLogo(mLogo);
				// mActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
				// }
				activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				activity.getSupportActionBar().setListNavigationCallbacks(mAdapter, this);
			}
		} else {
			mNavigationMenuInterface.onCreateSideNavigationMenu(this);
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
		NavigationItem item;
		if (!mIsSidebar) {
			item = new NavigationItem(itemId, mContext, this, false);
		} else {
			item = new NavigationItem(itemId, mContext, this, true);
		}
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
		NavigationDividerItem item;
		if (!mIsSidebar) {
			item = new NavigationDividerItem(itemId, mContext, this, false);
		} else {
			item = new NavigationDividerItem(itemId, mContext, this, true);
		}
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

		public void onCreateSideNavigationMenu(NavigationMenu menu);
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
		if (!mIsSidebar) {
			final MissionHubBaseActivity activity = (MissionHubBaseActivity) mContext;
			if (position > -1) {
				activity.getSupportActionBar().setSelectedNavigationItem(position);
			}
		}
	}

	/**
	 * Returns the NavigationItem from the list by its position
	 * 
	 * @param position
	 * @return
	 */
	public NavigationItem findItemByPosition(final int position) {
		try {
			return (NavigationItem) mAdapter.getItem(position);
		} catch (Exception e) {}
		return null;
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
	public int findPositionById(final int id) {
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

	/**
	 * Returns the adapter backing the menu
	 * 
	 * @return
	 */
	public SpinnerItemAdapter getAdapter() {
		return mAdapter;
	}

	/**
	 * Hide a navigation item by its id
	 * 
	 * @param itemId
	 */
	public void hide(final int itemId) {
		mAdapter.hide(findItemById(itemId), true);
	}

	/**
	 * Show a navigation item by its id
	 * 
	 * @param itemId
	 */
	public void show(final int itemId) {
		mAdapter.show(findItemById(itemId), true);
	}

	/**
	 * Hide a specified navigation item
	 * 
	 * @param item
	 */
	public void hide(final NavigationItem item) {
		hide(item.getItemId());
	}

	/**
	 * Show the specified navigation item
	 * 
	 * @param item
	 */
	public void show(final NavigationItem item) {
		show(item.getItemId());
	}

	/**
	 * Show all hidden navigation items
	 */
	public void showAll() {
		mAdapter.showAll();
	}
}