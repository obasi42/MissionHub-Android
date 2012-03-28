package com.missionhub.ui;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubMainActivity;
import com.missionhub.R;
import com.missionhub.fragment.MissionHubFragment;
import com.missionhub.ui.widget.item.NavigationItem;

/**
 * The MissionHub Main Action Bar List Menu
 */
public class MainMenu implements OnNavigationListener {

	/** the main MissionHub Activity */
	private final MissionHubMainActivity activity;

	/** the navigation list adapter */
	private final ItemAdapter adapter;

	/** the fragments currently controlling the menu */
	private final List<WeakReference<MissionHubFragment>> fragments = Collections.synchronizedList(new ArrayList<WeakReference<MissionHubFragment>>());

	/** the default menu items - used as a cache */
	private List<Item> defaultItems;

	/**
	 * Creates a MainMenu Object
	 */
	public MainMenu(final MissionHubMainActivity activity) {
		this.activity = activity;

		final Context context = activity.getSupportActionBar().getThemedContext();
		adapter = new ItemAdapter(context);
		adapter.setNotifyOnChange(false);

		initializeDefaultAdapter();
	}

	/**
	 * Initializes the default menu content from resources
	 */
	private synchronized void initializeDefaultAdapter() {
		adapter.clear();

		if (defaultItems == null) {
			defaultItems = Collections.synchronizedList(new ArrayList<Item>());

			final Resources res = activity.getResources();
			final String[] titles = res.getStringArray(R.array.menu_titles);
			final String[] subtitles = res.getStringArray(R.array.menu_subtitles);

			for (int i = 0; i < titles.length; i++) {
				final String title = titles[i];
				String subtitle = null;
				try {
					subtitle = subtitles[i];
				} catch (final Exception e) {}

				defaultItems.add(new NavigationItem(title, subtitle));
			}
		}

		for (final Item item : defaultItems) {
			adapter.add(item);
		}
	}

	/**
	 * Resets the adapter and content
	 */
	public synchronized void resetAdapter() {
		initializeDefaultAdapter();
		addFragmentMenuItems();
		adapter.notifyDataSetChanged();
	}

	/**
	 * Attaches the navigation list to the actionbar
	 */
	public void attach() {
		adapter.notifyDataSetChanged();
		activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
		activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		activity.getSupportActionBar().setListNavigationCallbacks(adapter, this);
	}

	/**
	 * Removes the navigation list from the actionbar
	 */
	public void detatch() {
		activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
		activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		activity.getSupportActionBar().setListNavigationCallbacks(null, null);
	}

	@Override
	public synchronized boolean onNavigationItemSelected(final int itemPosition, final long itemId) {
		for (final WeakReference<MissionHubFragment> reference : fragments) {
			final MissionHubFragment fragment = reference.get();
			if (fragment != null) {
				if (fragment.onNavigationItemSelected((NavigationItem) adapter.getItem(itemPosition))) {
					return true;
				}
			}
		}
		if (activity.onNavigationItemSelected((NavigationItem) adapter.getItem(itemPosition))) {
			return true;
		}
		return false;
	}

	/**
	 * Adds a controlling fragment
	 * 
	 * @param fragment
	 */
	public synchronized void addFragment(final MissionHubFragment fragment) {
		fragments.add(new WeakReference<MissionHubFragment>(fragment));
		resetAdapter();
	}

	/**
	 * Removes a fragment from the list of controlling fragments
	 * 
	 * @param fragment
	 */
	public synchronized void removeFragment(final MissionHubFragment fragment) {
		WeakReference<MissionHubFragment> ref = null;
		for (final WeakReference<MissionHubFragment> reference : fragments) {
			final MissionHubFragment frag = reference.get();
			if (frag == fragment) {
				ref = reference;
			}
		}
		if (ref != null) {
			fragments.remove(ref);
		}
		resetAdapter();
	}

	/**
	 * Runs the onCreateMainMenu method in each fragment
	 */
	private synchronized void addFragmentMenuItems() {
		final ArrayList<WeakReference<MissionHubFragment>> nulls = new ArrayList<WeakReference<MissionHubFragment>>();
		for (final WeakReference<MissionHubFragment> reference : fragments) {
			final MissionHubFragment fragment = reference.get();
			if (fragment != null) {
				fragment.onCreateMainMenu(this, adapter);
			} else {
				nulls.add(reference);
			}
		}
		for (final WeakReference<MissionHubFragment> reference : nulls) {
			fragments.remove(reference);
		}
	}
}