package com.missionhub.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.missionhub.model.Person;

public abstract class ContactListProvider {

	/** stores all of the currently fetched contacts */
	private final List<Person> mPeople = Collections.synchronizedList(new ArrayList<Person>());

	/** the contact list provider listener */
	private WeakReference<ContactListProviderListener> mListener;

	public synchronized boolean addPeople(final Person person) {
		final boolean ret = mPeople.add(person);
		notifyChanged();
		return ret;
	}

	public synchronized boolean addPeople(final List<Person> people) {
		final boolean ret = mPeople.addAll(people);
		notifyChanged();
		return ret;
	}

	public synchronized boolean removePeople(final Person person) {
		final boolean ret = mPeople.remove(person);
		notifyChanged();
		return ret;
	}

	public synchronized boolean removePeople(final List<Person> people) {
		final boolean ret = mPeople.removeAll(people);
		notifyChanged();
		return ret;
	}

	public synchronized void insertPeople(final int index, final Person person) {
		mPeople.add(index, person);
		notifyChanged();
	}

	public synchronized boolean insertPeople(final int index, final List<Person> people) {
		final boolean ret = mPeople.addAll(index, people);
		notifyChanged();
		return ret;
	}

	public synchronized boolean insertPeopleAfter(final Person person, final Person after) {
		int index = indexOfPerson(after);
		if (index == -1) index = mPeople.size() - 1;
		if (index + 1 > mPeople.size()) return false;

		insertPeople(index + 1, person);
		notifyChanged();
		return true;
	}

	public synchronized boolean insertPeopleBefore(final Person person, final Person before) {
		int index = indexOfPerson(before);
		if (index == -1) index = mPeople.size();
		if (index > mPeople.size()) return false;

		insertPeople(index, person);
		notifyChanged();
		return true;
	}

	public synchronized boolean insertPeopleAfter(final List<Person> people, final Person after) {
		int index = indexOfPerson(after);
		if (index == -1) index = mPeople.size() - 1;
		if (index + 1 > mPeople.size()) return false;

		insertPeople(index + 1, people);
		notifyChanged();
		return true;
	}

	public synchronized boolean insertPeopleBefore(final List<Person> people, final Person before) {
		int index = indexOfPerson(before);
		if (index == -1) index = mPeople.size();
		if (index > mPeople.size()) return false;

		insertPeople(index, people);
		notifyChanged();
		return true;
	}

	public synchronized void clearPeople() {
		mPeople.clear();
		notifyChanged();
	}

	public synchronized int indexOfPerson(final Person person) {
		return mPeople.indexOf(person);
	}

	public synchronized boolean containsPeople(final Person person) {
		return mPeople.contains(person);
	}

	public synchronized boolean containsPeople(final List<Person> people) {
		return mPeople.containsAll(people);
	}

	public synchronized List<Person> getAllPeople() {
		return new ArrayList<Person>(mPeople);
	}

	public synchronized int size() {
		return mPeople.size();
	}

	public synchronized void removeFirstPerson() {
		mPeople.remove(0);
		notifyChanged();
	}

	public synchronized void removeLastPerson() {
		if (mPeople.size() - 1 < 0) return;

		mPeople.remove(mPeople.size() - 1);
		notifyChanged();
	}

	public void notifyChanged() {
		if (mListener != null) {
			final ContactListProviderListener listener = mListener.get();
			if (listener != null) {
				listener.onContactsChanged();
			} else {
				mListener = null;
			}
		}
	}

	public void notifyWorking() {
		if (mListener != null) {
			final ContactListProviderListener listener = mListener.get();
			if (listener != null) {
				listener.onProviderWorkingChanged();
			} else {
				mListener = null;
			}
		}
	}

	public void setContactListProviderListener(final ContactListProviderListener listener) {
		mListener = new WeakReference<ContactListProviderListener>(listener);
		notifyChanged();
		notifyWorking();
	}

	/** called when the list view requests more contacts for the list */
	public abstract void getMore();

	/** returns whether or not all of the available contacts are in the list */
	public abstract boolean hasMore();

	/** returns true while the provider is working on a request */
	public abstract boolean isWorking();

	/** Interface with the contact list */
	public static interface ContactListProviderListener {

		public void onContactsChanged();

		public void onProviderWorkingChanged();

	}
}