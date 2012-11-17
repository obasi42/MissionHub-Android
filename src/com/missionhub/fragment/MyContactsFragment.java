package com.missionhub.fragment;

import com.missionhub.R;
import com.missionhub.ui.ContactListProvider;
import com.missionhub.util.U;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyContactsFragment extends MainFragment {
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final TextView tv = new TextView(inflater.getContext());
		tv.setText("My Contacts Fragment");
		return tv;
	}
	

//	/** the view pager */
//	private ViewPager mPager;
//	
//	/** the view pager adapter */
//	private FragmentStatePagerAdapter mAdapter;
//	
//	/** the all contacts fragment */
//	//private AllContactListFragment mAllContacts;
//	
//	/** the in-progress contacts fragment */
//	//private InProgressContactListFragment mInProgressContacts;
//	
//	/** the completed contacts fragment */
//	//private CompletedContactListFragment mCompletedContacts;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
//		
//		mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
//			@Override
//			public Fragment getItem(int index) {
//				switch(index) {
////					case 0: mAllContacts = new AllContactListFragment(); return mAllContacts;
////					case 1: mInProgressContacts = new InProgressContactListFragment(); return mInProgressContacts;
////					case 2: mCompletedContacts = new CompletedContactListFragment(); return mCompletedContacts;
//				}
//				throw new RuntimeException("Invalid pager index.");
//			}
//
//			@Override
//			public String getPageTitle(int index) {
//				switch(index) {
//					case 0: return "All";
//					case 1: return "In-Progress";
//					case 2: return "Completed";
//					default: return "Page " + index;
//				}
//			}
//
//			@Override
//			public int getCount() {
//				return 3;
//			}
//		};
//	}
//	
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		
//		U.resetActionBar(getSherlockActivity());
//		getSherlockActivity().getSupportActionBar().setTitle("My Contacts");
//	}
//	
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_contacts, null);
//		mPager = (ViewPager) v.findViewById(R.id.pager);
//		mPager.setOffscreenPageLimit(2);
//		mPager.setAdapter(mAdapter);
//		mPager.setCurrentItem(1);
//		return v;
//	}
	

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