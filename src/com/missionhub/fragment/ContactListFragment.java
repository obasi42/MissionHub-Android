package com.missionhub.fragment;

import greendroid.widget.ItemAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.widget.item.ContactListItem;

public class ContactListFragment extends MissionHubFragment implements OnItemClickListener {

	/** the list view */
	private ListView mListView;

	/** the list view adapter */
	private ItemAdapter mAdapter;

	/** map of person to contact list item */
	private final Map<Person, ContactListItem> mAdapterMap = Collections.synchronizedMap(new HashMap<Person, ContactListItem>());

	/** contact click listener */
	private OnContactClickListener mListener;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);

		mAdapter = new ItemAdapter(inflater.getContext());
		mAdapter.setNotifyOnChange(false);

		mListView.setAdapter(mAdapter);
		return view;
	}

	/**
	 * Adds a list of People to the listview
	 * 
	 * @param people
	 */
	public void addPeople(final List<Person> people) {
		for (final Person person : people) {
			final ContactListItem item = new ContactListItem(person);
			mAdapter.add(item);
			mAdapterMap.put(person, item);
		}

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Removes a list of People from the listview
	 * 
	 * @param people
	 */
	public void removePeople(final List<Person> people) {
		for (final Person person : people) {
			final ContactListItem item = mAdapterMap.get(person);
			mAdapter.remove(item);
			mAdapterMap.remove(person);
		}

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Interface definition for a callback to be invoked when an item in the
	 * contact list is selected.
	 */
	public interface OnContactClickListener {
		public void onContactClick(Person person);
	}

	/**
	 * Sets the OnContactClickListner to listen for clicks on a contact item
	 * 
	 * @param listener
	 */
	public void setOnContactClickListener(final OnContactClickListener listener) {
		mListener = listener;
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		// nothing to report click to
		if (mListener == null) {
			return;
		}

		final ContactListItem item = (ContactListItem) mAdapter.getItem(position);
		mListener.onContactClick(item.mPerson);
	}
}