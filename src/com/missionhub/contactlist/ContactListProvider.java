package com.missionhub.contactlist;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.os.Handler;

import com.missionhub.application.Session.SessionOrganizationIdChanged;
import com.missionhub.contactlist.ContactListAdapter.ContactItem;
import com.missionhub.contactlist.ContactListAdapter.ProgressItem;
import com.missionhub.model.Person;

public abstract class ContactListProvider {

	/** the list adapter */
	private final ContactListAdapter mAdapter;

	/** map of person to contact item to allow for quick adapter actions */
	private final Map<Person, ContactItem> mItems = Collections.synchronizedMap(new ConcurrentHashMap<Person, ContactItem>());

	/** holds a reference to the contact list view */
	private WeakReference<ContactListView> mContactList;

	/** the exception listener */
	private WeakReference<OnContactListProviderExceptionListener> mOnContactListProviderExceptionListener;

	/** the working listener */
	private WeakReference<OnContactListAdapterWorkingListener> mOnContactListAdapterWorkingListener;

	/** the progress item */
	private ProgressItem mProgressItem;

	/**
	 * Creates a new contact list provider with the default adapter
	 * 
	 * @param context
	 */
	public ContactListProvider(final Context context) {
		mAdapter = new ContactListAdapter(context);
	}

	/**
	 * Creates a new list provider with the given adapter
	 * 
	 * @param adapter
	 */
	public ContactListProvider(final ContactListAdapter adapter) {
		if (adapter == null) throw new RuntimeException("The contact list adapter cannot be null");
		mAdapter = adapter;
	}

	protected void afterCreate() {}

	/** sets the context of the array adapter */
	public void setContext(final Context context) {
		if (context == null) throw new RuntimeException("The context cannot be null");
		if (mAdapter != null) {
			mAdapter.setContext(context);
		}
	}

	public void setNotifyOnChange(final boolean notify) {
		mAdapter.setNotifyOnChange(notify);
	}

	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	private ContactItem getOrCreateItem(final Person person) {
		ContactItem item = mItems.get(person);
		if (item == null) {
			item = new ContactItem(person);
			mItems.put(person, item);
		}
		return item;
	}

	public ContactItem getItem(final Person person) {
		return mItems.get(person);
	}

	public void addPerson(final Person person) {
		mAdapter.add(getOrCreateItem(person));
	}

	public void addPeople(final Collection<Person> people) {
		for (final Person person : people) {
			mAdapter.add(getOrCreateItem(person));
		}
	}

	public void removePerson(final Person person) {
		mAdapter.remove(mItems.get(person));
		mItems.remove(person);
	}

	public void removePeople(final Collection<Person> people) {
		for (final Person person : people) {
			mAdapter.remove(mItems.get(person));
			mItems.remove(person);
		}
	}

	public void insertPerson(final int index, final Person person) {
		mAdapter.insert(getOrCreateItem(person), index);
	}

	public void insertPersonAfter(final Person person, final Person after) {
		mAdapter.insertAfter(getOrCreateItem(person), getItem(after));
	}

	public void insertPersonBefore(final Person person, final Person before) {
		mAdapter.insertBefore(getOrCreateItem(person), getItem(before));
	}

	public void clearPeople() {
		mItems.clear();
		mAdapter.clear();
	}

	public int indexOfPerson(final Person person) {
		return mAdapter.indexOf(mItems.get(person));
	}

	public boolean containsPerson(final Person person) {
		return mAdapter.contains(mItems.get(person));
	}

	public int size() {
		return mAdapter.getCount();
	}

	public void onScroll(final ContactListView contactListView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {

	}

	protected void setContactList(final ContactListView contactList) {
		mContactList = new WeakReference<ContactListView>(contactList);
	}

	protected ContactListView getContactList() {
		if (mContactList != null) {
			return mContactList.get();
		}
		return null;
	}

	protected ContactListAdapter getAdapter() {
		return mAdapter;
	}

	/**
	 * Interface definition for callbacks to be invoked when an exception is thrown in the provider
	 */
	public static interface OnContactListProviderExceptionListener {
		public void onContactListProviderException(Exception exception);
	}

	public void setOnContactListExceptionListener(final OnContactListProviderExceptionListener listener) {
		mOnContactListProviderExceptionListener = new WeakReference<OnContactListProviderExceptionListener>(listener);
	}

	/**
	 * Posts an exception to the exception listener
	 * 
	 * @param exception
	 */
	protected void postException(final Exception exception) {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (mOnContactListProviderExceptionListener != null) {
					final OnContactListProviderExceptionListener listener = mOnContactListProviderExceptionListener.get();
					if (listener != null) {
						listener.onContactListProviderException(exception);
					}
				}
			}
		});
	}

	public ProgressItem getProgressItem() {
		return mProgressItem;
	}

	public void setProgressItem(final ProgressItem item) {
		mProgressItem = item;
	}

	public boolean isWorking() {
		return false;
	}

	public static interface OnContactListAdapterWorkingListener {
		public void onWorkingChanged(boolean working);
	}

	public void setOnContactListWorkingListener(final OnContactListAdapterWorkingListener listener) {
		mOnContactListAdapterWorkingListener = new WeakReference<OnContactListAdapterWorkingListener>(listener);
	}

	/**
	 * Posts to working listener
	 * 
	 * @param exception
	 */
	protected void postWorking(final boolean working) {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (mOnContactListAdapterWorkingListener != null) {
					final OnContactListAdapterWorkingListener listener = mOnContactListAdapterWorkingListener.get();
					if (listener != null) {
						listener.onWorkingChanged(working);
					}
				}
			}
		});
	}

	public abstract void reload();

	public void onEventMainThread(final SessionOrganizationIdChanged event) {
		reload();
	}
}