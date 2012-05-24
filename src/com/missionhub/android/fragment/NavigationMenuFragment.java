package com.missionhub.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.android.app.MissionHubMainActivity;
import com.missionhub.android.ui.NavigationMenu;
import com.missionhub.android.ui.SpinnerItemAdapter;
import com.missionhub.android.ui.NavigationMenu.NavigationMenuFragmentInterface;
import com.missionhub.android.ui.NavigationMenu.OnNavigationItemSelectedListener;
import com.missionhub.android.ui.widget.item.NavigationItem;

public class NavigationMenuFragment extends MissionHubFragment implements OnItemClickListener, NavigationMenuFragmentInterface {

	/** the activity */
	private MissionHubMainActivity mActivity;

	/** the navigation menu backing the fragment */
	private NavigationMenu mNavigationMenu;

	/** the list view */
	private ListView mListView;

	/** the list view adapter */
	private SpinnerItemAdapter mAdapter;

	/** the on navigation item selected listener **/
	private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

	/**
	 * Called when the fragment is attached to the activity. Sets up the
	 * navigation menu
	 */
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof MissionHubMainActivity)) {
			throw new RuntimeException("NavigationMenuFragment must be attached to a MissionHubMainActivity");
		}
		mActivity = (MissionHubMainActivity) activity;
		mOnNavigationItemSelectedListener = mActivity;
		mNavigationMenu = new NavigationMenu(mActivity, this);
	}

	/**
	 * Called when the fragment presents its view Sets up the navigation list
	 * view
	 */
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_navigation_menu, container, false);
		view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getLayoutWeight()));

		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setOnItemClickListener(this);

		mListView.setAdapter(mAdapter);

		return view;
	}

	/**
	 * Called when an item in the listview is clicked
	 */
	@Override
	public void onItemClick(final AdapterView<?> adapter, final View listView, final int position, final long id) {
		final NavigationItem item = (NavigationItem) adapter.getItemAtPosition(position);
		setSelectedNavigationItem(item);
	}

	@Override
	public void setAdapter(final SpinnerItemAdapter adapter) {
		mAdapter = adapter;
	}

	/**
	 * Sets the selected navigation item
	 */
	@Override
	public void setSelectedNavigationItem(final NavigationItem item) {
		if (item != null && item != mNavigationMenu.getSelectedItem()) {
			mNavigationMenu.setSelectedItem(item);
			final int position = NavigationMenu.findPositionByItem(mAdapter, item);
			if (mListView != null) {
				mListView.setItemChecked(position, true);
			}
			if (mOnNavigationItemSelectedListener != null) {
				mOnNavigationItemSelectedListener.onNavigationItemSelected(item);
			}
		}
	}

	/**
	 * Returns the navigation menu backing the fragment
	 * 
	 * @return
	 */
	public NavigationMenu getNavigationMenu() {
		return mNavigationMenu;
	}

	/**
	 * Called when the navigation menu is setup, pass it to the
	 * NavigationMenuFragmentInterface interface.
	 */
	@Override
	public void onCreateNavigationMenu(final NavigationMenu navigationMenu) {
		if (mActivity instanceof NavigationMenuFragmentInterface) {
			((NavigationMenuFragmentInterface) mActivity).onCreateFragmentNavigationMenu(navigationMenu);
		}
	}

	/**
	 * Interface an activity must implement to use the navigation menu fragment
	 */
	public interface NavigationMenuFragmentInterface {
		public void onCreateFragmentNavigationMenu(NavigationMenu navigationMenu);
	}
}