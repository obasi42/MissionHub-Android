package com.missionhub.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.ContactListProvider;
import com.missionhub.util.U;

public class MyContactsFragment extends MainFragment {

	/** the view pager */
	private ViewPager mPager;

	/** the view pager adapter */
	private FragmentStatePagerAdapter mAdapter;

	/** the all contacts fragment */
	private MyContactsContactListFragment mAll;

	/** the in-progress contacts fragment */
	private MyContactsContactListFragment mInProgress;

	/** the completed contacts fragment */
	private MyContactsContactListFragment mCompleted;

	/** the all contacts data provider */
	private ContactListProvider mInProgressProvider;

	/** the in-progress contacts data provider */
	private ContactListProvider mAllProvider;

	/** the completed contacts data provider */
	private ContactListProvider mCompletedProvider;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
			@Override
			public Fragment getItem(final int index) {
				switch (index) {
				case 0:
					mAll = new MyContactsContactListFragment();
					mAll.setProvider(mAllProvider);
					return mAll;
				case 1:
					mInProgress = new MyContactsContactListFragment();
					mInProgress.setProvider(mInProgressProvider);
					return mInProgress;
				case 2:
					mCompleted = new MyContactsContactListFragment();
					mCompleted.setProvider(mCompletedProvider);
					return mCompleted;
				}
				throw new RuntimeException("Invalid pager index.");
			}

			@Override
			public String getPageTitle(final int index) {
				switch (index) {
				case 0:
					return getString(R.string.my_contacts_all);
				case 1:
					return getString(R.string.my_contacts_in_progress);
				case 2:
					return getString(R.string.my_contacts_completed);
				default:
					return "Page " + index;
				}
			}

			@Override
			public int getCount() {
				return 3;
			}
		};
	}

	public class MyContactsContactListFragment extends ContactListFragment {

	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		U.resetActionBar(getSherlockActivity());
		getSherlockActivity().getSupportActionBar().setTitle(R.string.my_contacts_title);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_contacts, null);
		mPager = (ViewPager) v.findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(2);
		// mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(1);
		return v;
	}

	// /** the search menu item helper */
	// private SearchMenuItemHelper mSearchMenuItemHelper;
	//
	// /** the contact list options provider */
	// ContactListOptionsProvider mProvider;
	//
	// /** the provider for search results */
	// ContactListOptionsProvider mSearchProvider;
	//
	// /** the contact list options */
	// ContactListOptions mOptions = new ContactListOptions();
	//
	// @Override
	// public void onCreate(final Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// if (savedInstanceState != null) {
	// mProvider = (ContactListOptionsProvider)
	// ObjectStore.getInstance().retrieveObject(savedInstanceState.getString("mProvider"));
	// mSearchProvider = (ContactListOptionsProvider)
	// ObjectStore.getInstance().retrieveObject(savedInstanceState.getString("mSearchProvider"));
	// }
	//
	// setHasOptionsMenu(true);
	// }
	//
	// @Override
	// public void onAttach(final Activity activity) {
	// super.onAttach(activity);
	//
	// mSearchMenuItemHelper = new SearchMenuItemHelper(this);
	// mSearchMenuItemHelper.setListener(this);
	// }
	//
	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	//
	// if (mSearchMenuItemHelper != null) mSearchMenuItemHelper.onRestoreInstanceState(savedInstanceState);
	// }
	//
	// @Override
	// public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
	// super.onCreateOptionsMenu(menu, inflater);
	//
	// mSearchMenuItemHelper.onCreateOptionsMenu(menu, inflater);
	// }
	//
	// @Override
	// public ContactListProvider onCreateProvider() {
	// if (mProvider == null)
	// mProvider = new ContactListOptionsProvider(mOptions);
	//
	// if (mSearchProvider == null)
	// mSearchProvider = new ContactListOptionsProvider();
	//
	// return mProvider;
	// }
	//
	// @Override
	// public void onSearchTextChange(final String query) {
	// if (query.length() == 0) {
	// setProvider(mProvider);
	// } else {
	// final ContactListOptions options = new ContactListOptions();
	// options.addFilter("name", query);
	// mSearchProvider.setOptions(options);
	// setProvider(mSearchProvider);
	// }
	// }
	//
	// @Override
	// public void onSearchSubmit(final String query) {
	// onSearchTextChange(query);
	// }
	//
	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	//
	// outState.putString("mProvider", ObjectStore.getInstance().storeObject(mProvider, this));
	// outState.putString("mSearchProvider", ObjectStore.getInstance().storeObject(mSearchProvider, this));
	//
	// if (mSearchMenuItemHelper != null) mSearchMenuItemHelper.onSaveInstanceState(outState);
	// }
}