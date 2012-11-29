//package com.missionhub.ui;
//
//import java.util.List;
//import java.util.concurrent.CancellationException;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.FutureTask;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import roboguice.util.SafeAsyncTask;
//import android.util.Log;
//
//import com.missionhub.api.Api;
//import com.missionhub.api.Api.ContactListOptions;
//import com.missionhub.contactlist.ContactListProvider;
//import com.missionhub.model.Person;
//
//public class ContactListOptionsProvider extends ContactListProvider {
//
//	/** the logging tag */
//	public static final String TAG = ContactListOptionsProvider.class.getSimpleName();
//
//	/** Single thread executor to ensure sequential results */
//	private final Executor mExecutor = Executors.newSingleThreadExecutor();
//
//	/** the contact list options item */
//	private ContactListOptions mOptions;
//
//	/** the future task for fetching more contacts */
//	private FutureTask<Void> mTask;
//
//	/** the future task for the api request such that is can be canceled */
//	private FutureTask<List<Person>> mApiTask;
//
//	/** true when the provider is doing work in the background */
//	private final AtomicBoolean mWorking = new AtomicBoolean(false);
//
//	public ContactListOptionsProvider() {
//		mOptions = new ContactListOptions();
//	}
//
//	public ContactListOptionsProvider(final ContactListOptions options) {
//		mOptions = options;
//	}
//
//	@Override
//	public void getMore() {
//		if (mWorking.get()) return;
//		mWorking.set(true);
//
//		mTask = (new SafeAsyncTask<List<Person>>() {
//			@Override
//			public List<Person> call() throws Exception {
//				if (mOptions == null) return null;
//
//				mApiTask = Api.getContactList(mOptions);
//
//				final List<Person> people = mApiTask.get();
//
//				if (future.isCancelled()) return null;
//
//				// set up the options for the next run
//				if (people.size() < mOptions.getLimit()) {
//					mOptions.setIsAtEnd(true);
//					mOptions.incrementStart(people.size());
//				} else {
//					mOptions.advanceStart();
//				}
//
//				return people;
//			}
//
//			@Override
//			protected void onPreExecute() {
//				notifyWorking();
//			}
//
//			@Override
//			protected void onSuccess(final List<Person> people) {
//				if (people == null) return;
//				addPeople(people);
//			}
//
//			@Override
//			protected void onException(final Exception e) {
//				if (e instanceof InterruptedException || e instanceof CancellationException) {
//					return;
//				}
//				onError(e);
//			}
//
//			@Override
//			protected void onFinally() {
//				mWorking.set(false);
//				notifyWorking();
//			}
//
//			@Override
//			protected void onInterrupted(final Exception e) {
//				// allows for cancel(true);
//			}
//
//		}).future();
//
//		mExecutor.execute(mTask);
//	}
//
//	@Override
//	public boolean hasMore() {
//		if (mOptions == null) return false;
//		return !mOptions.isAtEnd();
//	}
//
//	@Override
//	public boolean isWorking() {
//		return (mWorking.get());
//	}
//
//	public void cancelTasksQuietly() {
//		if (mWorking.get()) {
//			if (mApiTask != null) mApiTask.cancel(true);
//			if (mTask != null) mTask.cancel(true);
//		}
//		mWorking.set(false);
//	}
//
//	public ContactListOptions getOptions() {
//		return mOptions;
//	}
//
//	public void setOptions(final ContactListOptions options) {
//		cancelTasksQuietly();
//		mOptions = options;
//		clearPeople();
//		getMore();
//	}
//
//	public void onError(final Exception e) {
//		Log.e(TAG, e.getMessage(), e);
//	}
// }