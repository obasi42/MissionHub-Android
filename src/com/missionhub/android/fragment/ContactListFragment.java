package com.missionhub.android.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.android.api.old.model.sql.Person;
import com.missionhub.android.app.DisplayMode;
import com.missionhub.android.ui.DynamicLayoutAdapter;
import com.missionhub.android.ui.widget.SelectableListView;
import com.missionhub.android.ui.widget.SelectableListView.OnItemCheckedListener;
import com.missionhub.android.ui.widget.item.ContactListItem;
import com.missionhub.android.util.U;

public class ContactListFragment extends MissionHubFragment implements OnItemClickListener, OnItemCheckedListener {

	/** the list view */
	private SelectableListView mListView;

	/** the list view adapter */
	private DynamicLayoutAdapter mAdapter;

	/** map of person to contact list item */
	private final Map<Person, ContactListItem> mAdapterMap = Collections.synchronizedMap(new HashMap<Person, ContactListItem>());

	/** contact list listener */
	private OnContactListListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, R.id.action_user_add, 0, R.string.action_user_add).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS).setIcon(R.drawable.ic_action_user_add);
		menu.add(Menu.NONE, R.id.action_refresh, 95, R.string.action_refresh).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS).setIcon(R.drawable.ic_action_refresh);
    }
	
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		mAdapter = new DynamicLayoutAdapter(activity);
		mAdapterMap.clear();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

		final DisplayMode dm = U.getMHApplication(inflater.getContext()).getDisplayMode();
		if (dm.isPhone()) {
			view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT));
		} else {
			view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getLayoutWeight()));
		}

		mListView = (SelectableListView) view.findViewById(R.id.listView);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mListView.setSelectionWidth(56);
		mListView.setSelectionSide(SelectableListView.SIDE_RIGHT);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemCheckedListener(this);

		mAdapter = new DynamicLayoutAdapter(inflater.getContext());

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
	 * Inserts a list of people in to the list view beginning at a specified
	 * position.
	 * 
	 * @param people
	 * @param position
	 */
	public void insertPeople(final List<Person> people, final int position) {
		mAdapter.setNotifyOnChange(false);

		int offset = 0;
		for (final Person person : people) {
			final ContactListItem item = new ContactListItem(person);
			mAdapter.insert(item, position + offset);
			mAdapter.add(item);
			mAdapterMap.put(person, item);
			offset++;
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
	 * Returns true if one or more contacts it checked
	 * 
	 * @return
	 */
	public boolean hasCheckedContacts() {
		@SuppressWarnings("deprecation") final int checkedCount = mListView.getCheckItemIds().length;
		if (checkedCount > 0) {
			return true;
		}
		return false;
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

	/**
	 * Override to inject contact listener methods
	 */
	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (mListener != null) {
			mListener.onContactClick(getPersonAtPosition(position));
		}
	}

	/**
	 * Override to inject contact listener methods
	 */
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

	/**
	 * Override to inject contact listener methods
	 */
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
	 * @return the person
	 */
	private Person getPersonAtPosition(final int position) {
		final ContactListItem item = (ContactListItem) mAdapter.getItem(position);
		if (item != null) {
			return item.mPerson;
		}
		return null;
	}

	/**
	 * Finds the position of the person in the adapter
	 * 
	 * @param person
	 * @return the position or -1
	 */
	private int getPositionOfPerson(final Person person) {
		for (int i = 0; i < mAdapter.getCount(); i++) {
			final ContactListItem item = (ContactListItem) mAdapter.getItem(i);
			if (item.mPerson == person) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param position
	 * @param activated
	 */
	public void setListItemActivated(final int position) {
		mListView.setItemActivated(position);
	}

	/**
	 * Sets the activated contact
	 * 
	 * @param person
	 * @param activated
	 */
	public void setContactActivated(final Person person) {
		if (person == null) {
			setListItemActivated(-1);
		} else {
			setListItemActivated(getPositionOfPerson(person));
		}
	}

	/**
	 * Scrolls the listview to the activated contact
	 */
	public void scrollToActivatedContact() {
		mListView.scrollToItemActivated();
	}
}