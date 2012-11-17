package com.missionhub.fragment;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.missionhub.R;
import com.missionhub.ui.ContactListProvider;
public class ContactListFragment extends BaseFragment {

	/** the logging tag */
	public static final String TAG = ContactListFragment.class.getSimpleName();

	/** the contact list view */
//	@InjectView(R.id.contact_list) private ContactListView mListView;

	/** the list provider handle */
	private ContactListProvider mProvider;

//	/** returns the listview */
//	public ContactListView getListView() {
//		return mListView;
//	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return new View(inflater.getContext());
		//return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null) {
			//mListView.setProvider(mProvider);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		//mProvider = mListView.getProvider();
	}

	/**
	 * Returns the list provider
	 */
	public ContactListProvider getProvider() {
		return mProvider;
	}

	/**
	 * Sets the contact list provider
	 */
	public void setProvider(final ContactListProvider provider) {
		mProvider = provider;
		//mListView.setProvider(mProvider);
	}
}