package com.missionhub.fragment;

import greendroid.widget.ItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.missionhub.R;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.ListItemAdapter;
import com.missionhub.ui.widget.SelectableListView;
import com.missionhub.ui.widget.SelectableListView.OnItemCheckedListener;
import com.missionhub.ui.widget.item.ContactListItem;

public class ContactListFragment extends MissionHubFragment implements OnItemClickListener, OnItemCheckedListener {

	/** the list view */
	private SelectableListView mListView;

	/** the list view adapter */
	private ItemAdapter mAdapter;

	/** map of person to contact list item */
	private final Map<Person, ContactListItem> mAdapterMap = Collections.synchronizedMap(new HashMap<Person, ContactListItem>());

	/** contact list listener */
	private OnContactListListener mListener;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
		mListView = (SelectableListView) view.findViewById(R.id.listView);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mListView.setSelectionWidth(56);
		mListView.setSelectionSide(SelectableListView.SIDE_RIGHT);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemCheckedListener(this);

		mAdapter = new ListItemAdapter(inflater.getContext());
		mListView.setAdapter(mAdapter);

		return view;
	}

	/**
	 * Adds a list of People to the listview
	 * 
	 * @param people
	 */
	public void addPeople(final List<Person> people) {
		mAdapter.setNotifyOnChange(false);

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
		mAdapter.setNotifyOnChange(false);

		for (final Person person : people) {
			final ContactListItem item = mAdapterMap.get(person);
			mAdapter.remove(item);
			mAdapterMap.remove(person);
		}

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Returns a list of the currently checked contacts
	 * 
	 * @return
	 */
	public List<Person> getCheckedContacts() {
		final ArrayList<Person> people = new ArrayList<Person>();

		final SparseBooleanArray positions = mListView.getCheckedItemPositions();
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i)) {
				people.add(getPersonAtPosition(i));
			}
		}

		return people;
	}

	/**
	 * Interface definition for a callback to be invoked when an item in the
	 * contact list is clicked or checked.
	 */
	public interface OnContactListListener {
		public void onContactClick(Person person);

		public void onCheckContact(Person person);

		public void onUncheckContact(Person person);

		public void onUncheckAllContacts();
	}

	/**
	 * Sets the OnContactListListener to listen for clicks or checks on a
	 * contact
	 * 
	 * @param listener
	 */
	public void setOnContactListListener(final OnContactListListener listener) {
		mListener = listener;
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (mListener != null) {
			mListener.onContactClick(getPersonAtPosition(position));
		}
	}

	@Override
	public void onSetItemChecked(final int position, final boolean checked) {
		if (mListener != null) {
			if (checked) {
				mListener.onCheckContact(getPersonAtPosition(position));
			} else {
				mListener.onUncheckContact(getPersonAtPosition(position));
			}
		}
	}

	@Override
	public void onAllUnchecked() {
		if (mListener != null) {
			mListener.onUncheckAllContacts();
		}
	}

	/**
	 * Gets the person from the adapter item at the specified position
	 * 
	 * @param position
	 * @return
	 */
	private Person getPersonAtPosition(final int position) {
		final ContactListItem item = (ContactListItem) mAdapter.getItem(position);
		if (item != null) {
			return item.mPerson;
		}
		return null;
	}
}