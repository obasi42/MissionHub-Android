package com.missionhub.ui;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubMainActivity;
import com.missionhub.PeopleAllActivity;
import com.missionhub.PeopleDirectoryActivity;
import com.missionhub.PeopleMyActivity;
import com.missionhub.R;
import com.missionhub.fragment.MissionHubFragment;
import com.missionhub.ui.widget.item.NavigationItem;
import com.missionhub.util.U;

/**
 * The MissionHub Main Action Bar List Menu
 */
public class NavigationMenu implements OnNavigationListener {

	/** the activity */
	private final MissionHubMainActivity mActivity;

	/** the navigation list mAdapter */
	private final ItemAdapter mAdapter;

	/** the mFragments currently controlling the menu */
	private final List<WeakReference<MissionHubFragment>> mFragments = Collections.synchronizedList(new ArrayList<WeakReference<MissionHubFragment>>());

	/** the default menu items - used as a cache */
	private List<Item> mDefaultItems;

	/** the currently selected navigation item */
	private NavigationItem mCurrentNavigationItem;

	/**
	 * Creates a MainMenu Object
	 */
	public NavigationMenu(final MissionHubMainActivity missionHubMainActivity) {
		mActivity = missionHubMainActivity;

		mAdapter = new ItemAdapter(missionHubMainActivity);
		mAdapter.setNotifyOnChange(false);

		initializeDefaultAdapter();
	}

	/**
	 * Initializes the default menu content from resources
	 */
	private synchronized void initializeDefaultAdapter() {
		mAdapter.clear();

		if (mDefaultItems == null) {
			mDefaultItems = Collections.synchronizedList(new ArrayList<Item>());
			mDefaultItems.add(new NavigationItem(PeopleMyActivity.class, "My Contacts"));
			mDefaultItems.add(new NavigationItem(PeopleAllActivity.class, "All Contacts"));
			mDefaultItems.add(new NavigationItem(PeopleDirectoryActivity.class, "Contact Directory"));
		}

		for (final Item item : mDefaultItems) {
			mAdapter.add(item);
		}
	}

	/**
	 * Resets the mAdapter and content
	 */
	public synchronized void resetAdapter() {
		initializeDefaultAdapter();
		addFragmentMenuItems();
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Attaches the navigation list to the actionbar
	 * 
	 * @param defaultItemId
	 */
	public void attach(final int defaultItemId) {
		attach();
		for (int i = 0; i < mAdapter.getCount(); i++) {
			final NavigationItem item = (NavigationItem) mAdapter.getItem(i);
			if (item.mId > -1 && item.mId == defaultItemId) {
				mActivity.getSupportActionBar().setSelectedNavigationItem(i);
				mCurrentNavigationItem = item;
			}
		}
	}

	/**
	 * Attaches the navigation list to the actionbar
	 * 
	 * @param defaultItemClass
	 */
	public void attach(final Class<?> defaultItemClass) {
		attach();
		for (int i = 0; i < mAdapter.getCount(); i++) {
			final NavigationItem item = (NavigationItem) mAdapter.getItem(i);
			if (item.mActivityClass != null && item.mActivityClass == defaultItemClass) {
				mActivity.getSupportActionBar().setSelectedNavigationItem(i);
				mCurrentNavigationItem = item;
			}
		}
	}

	/**
	 * Attaches the navigation list to the actionbar
	 */
	private void attach() {
		mAdapter.notifyDataSetChanged();
		mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
		if (U.isTablet(mActivity)) {
			mActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
			mActivity.getSupportActionBar().setLogo(R.drawable.logo);
		}
		mActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActivity.getSupportActionBar().setListNavigationCallbacks(mAdapter, this);
	}

	/**
	 * Removes the navigation list from the actionbar
	 */
	public void detatch() {
		mActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
		mActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mActivity.getSupportActionBar().setListNavigationCallbacks(null, null);
	}

	@Override
	public synchronized boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		for (final WeakReference<MissionHubFragment> reference : mFragments) {
			final MissionHubFragment fragment = reference.get();
			if (fragment != null) {
				if (fragment.onNavigationItemSelected((NavigationItem) mAdapter.getItem(itemPosition))) {
					return true;
				}
			}
		}
		if (mActivity != null && mActivity.onNavigationItemSelected((NavigationItem) mAdapter.getItem(itemPosition))) {
			return true;
		}
		return onNavigationItemSelected((NavigationItem) mAdapter.getItem(itemPosition));
	}

	public synchronized boolean onNavigationItemSelected(final NavigationItem item) {
		if (mCurrentNavigationItem == null || item == mCurrentNavigationItem) {
			return false;
		}

		if (item.mActivityClass != null) {
			final Intent intent = new Intent(mActivity, item.mActivityClass);
			mActivity.startActivity(intent);
			mActivity.finish();
		}

		mCurrentNavigationItem = item;

		return true;
	}

	/**
	 * Adds a controlling fragment
	 * 
	 * @param fragment
	 */
	public synchronized void addFragment(final MissionHubFragment fragment) {
		mFragments.add(new WeakReference<MissionHubFragment>(fragment));
		resetAdapter();
	}

	/**
	 * Removes a fragment from the list of controlling mFragments
	 * 
	 * @param fragment
	 */
	public synchronized void removeFragment(final MissionHubFragment fragment) {
		WeakReference<MissionHubFragment> ref = null;
		for (final WeakReference<MissionHubFragment> reference : mFragments) {
			final MissionHubFragment frag = reference.get();
			if (frag == fragment) {
				ref = reference;
			}
		}
		if (ref != null) {
			mFragments.remove(ref);
		}
		resetAdapter();
	}

	/**
	 * Runs the onCreateMainMenu method in each fragment
	 */
	private synchronized void addFragmentMenuItems() {
		final ArrayList<WeakReference<MissionHubFragment>> nulls = new ArrayList<WeakReference<MissionHubFragment>>();
		for (final WeakReference<MissionHubFragment> reference : mFragments) {
			final MissionHubFragment fragment = reference.get();
			if (fragment != null) {
				fragment.onCreateMainMenu(this, mAdapter);
			} else {
				nulls.add(reference);
			}
		}
		for (final WeakReference<MissionHubFragment> reference : nulls) {
			mFragments.remove(reference);
		}
	}
}