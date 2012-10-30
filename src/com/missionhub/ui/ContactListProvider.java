package com.missionhub.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import roboguice.util.RoboAsyncTask;

import com.missionhub.application.Application;
import com.missionhub.model.Person;

public abstract class ContactListProvider {
	
	/** stores all of the currently fetched contacts */
	private List<Person> mPeople = Collections.synchronizedList(new ArrayList<Person>());
	
	/** the contact list provider listener */
	private WeakReference<ContactListProviderListener> mListener;
	
	/** true while getting more contacts */
	private AtomicBoolean mGettingContacts = new AtomicBoolean(false);
	
	public synchronized boolean addPeople(Person person) {
		boolean ret = mPeople.add(person);
		notifyChanged();
		return ret;
	}
	
	public synchronized boolean addPeople(List<Person> people) {
		boolean ret = mPeople.addAll(people);
		notifyChanged();
		return ret;
	}
	
	public synchronized boolean removePeople(Person person) {
		boolean ret = mPeople.remove(person);
		notifyChanged();
		return ret;
	}
	
	public synchronized boolean removePeople(List<Person> people) {
		boolean ret = mPeople.removeAll(people);
		notifyChanged();
		return ret;
	}
	
	public synchronized void insertPeople(int index, Person person) {
		mPeople.add(index, person);
		notifyChanged();
	}
	
	public synchronized boolean insertPeople(int index, List<Person> people) {
		boolean ret = mPeople.addAll(index, people);
		notifyChanged();
		return ret;
	}
	
	public synchronized boolean insertPeopleAfter(Person person, Person after) {
		int index = indexOfPerson(after);
		if (index == -1) index = mPeople.size() -1;
		if (index + 1 > mPeople.size()) return false;
		
		insertPeople(index + 1, person);
		notifyChanged();
		return true;
	}
	
	public synchronized boolean insertPeopleBefore(Person person, Person before) {
		int index = indexOfPerson(before);
		if (index == -1) index = mPeople.size();
		if (index > mPeople.size()) return false;
		
		insertPeople(index, person);
		notifyChanged();
		return true;
	}
	
	public synchronized boolean insertPeopleAfter(List<Person> people, Person after) {
		int index = indexOfPerson(after);
		if (index == -1) index = mPeople.size() - 1;
		if (index + 1 > mPeople.size()) return false;
		
		insertPeople(index + 1, people);
		notifyChanged();
		return true;
	}
	
	public synchronized boolean insertPeopleBefore(List<Person> people, Person before) {
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
	
	public int indexOfPerson(Person person) {
		return mPeople.indexOf(person);
	}
	
	public boolean containsPeople(Person person) {
		return mPeople.contains(person);
	}
	
	public boolean containsPeople(List<Person> people) {
		return mPeople.containsAll(people);
	}
	
	public List<Person> getAllPeople() {
		return new ArrayList<Person>(mPeople);
	}
	
	public int size() {
		return mPeople.size();
	}
	
	public void removeFirstPerson() {
		mPeople.remove(0);
	}
	
	public void removeLastPerson() {
		mPeople.remove(mPeople.size() - 1);
	}
	
	private void notifyChanged() {
		if (mListener != null) {
			final ContactListProviderListener listener = mListener.get();
			if (listener != null) {
				listener.onContactsChanged(getAllPeople());
			} else {
				mListener = null;
			}
		}
	}
	
	public void setContactListProviderListener(ContactListProviderListener listener) {
		mListener = new WeakReference<ContactListProviderListener>(listener);
		listener.onContactsChanged(getAllPeople());
	}
	
	public void getMoreAsync() {		
		if (mGettingContacts.get() || isAtEnd()) return;
		mGettingContacts.set(true);
		
		RoboAsyncTask<List<Person>> task = new RoboAsyncTask<List<Person>>(Application.getContext()) {
			@Override
			public List<Person> call() throws Exception {
				return getMore();
			}
		    
		    @Override 
		    protected void onSuccess(List<Person> people) {
		    	addPeople(people);
		    } 
		    
		    @Override 
		    protected void onException(Exception e) { 
		        onError(e);
		    } 
		    
		    @Override 
		    protected void onFinally() {
		    	mGettingContacts.set(false);
		    	
		    	// updates the progress item
		    	if (isAtEnd()) {
		    		notifyChanged();
		    	}
		    }
		};
		Application.getExecutor().execute(task.future());
	}
	
	public boolean isGettingMore() {
		return mGettingContacts.get();
	}
	
	/** returns more contacts and adds them to the list 
	 * @throws Exception */
	public abstract List<Person> getMore() throws Exception;
	
	/** returns whether or not all of the available contacts are in the list */
	public abstract boolean isAtEnd();
	
	/** called when an exception is thrown in the provider */
	public abstract void onError(Exception e);
	
	public static interface ContactListProviderListener {
		
		public void onContactsChanged(List<Person> people);
		
	}
}