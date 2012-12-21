package com.missionhub.contactlist;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import roboguice.util.SafeAsyncTask;
import android.content.Context;

import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.PersonListOptions;
import com.missionhub.model.Person;

/**
 * Contact list provider that uses the api to fetch contacts
 */
public class ApiContactListProvider extends ContactListProvider {

	/** true when all available contacts have been fetched */
	private boolean mDone = false;

	/** true when the provider is started */
	private boolean mStarted = false;

	/** true if fetching contacts is paused */
	private boolean mPaused = false;

	/** the contact list options */
	private PersonListOptions mOptions;

	/** Single thread executor to ensure sequential results */
	private final Executor mExecutor = Executors.newSingleThreadExecutor();

	/** the task used to fetch contacts */
	private SafeAsyncTask<List<Person>> mTask;

	public ApiContactListProvider(final Context context) {
		super(context);
	}

	public ApiContactListProvider(final Context context, final boolean start) {
		super(context);
		mStarted = start;
	}

	public ApiContactListProvider(final Context context, final PersonListOptions options) {
		this(context, options, true);
	}

	public ApiContactListProvider(final Context context, final PersonListOptions options, final boolean start) {
		super(context);
		mStarted = start;
		mOptions = options;
	}

	@Override
	protected void afterCreate() {
		reload();
	}

	public void setOptions(final PersonListOptions options) {
		mOptions = options;
		reload();
	}

	public PersonListOptions getOptions() {
		if (mOptions != null) {
			return (PersonListOptions) mOptions.clone();
		}
		return null;
	}

	public void start() {
		if (mStarted) return;

		mStarted = true;
		fetchMore();
	}

	public void resume() {
		if (!mPaused) return;

		mPaused = false;
		fetchMore();
	}

	public void pause(final boolean mayInterruptIfRunning) {
		if (mayInterruptIfRunning && mTask != null) {
			mTask.cancel(true);
		}
		mPaused = true;
	}

	private void fetchMore() {
		if (mTask != null || !mStarted || mDone || mPaused || mOptions == null) return;

		mTask = new SafeAsyncTask<List<Person>>() {
			@Override
			public List<Person> call() throws Exception {
				return Api.listPeople(
						mOptions,
						ApiOptions.builder() //
								.include(Include.assigned_tos) //
								.include(Include.current_address) //
								.include(Include.email_addresses) //
								.include(Include.organizational_roles) //
								.include(Include.phone_numbers) //
								.build()).get();
			}

			@Override
			public void onSuccess(final List<Person> people) {
				mOptions.advanceOffset();
				if (people.size() < mOptions.getLimit()) {
					mDone = true;
				}
				setNotifyOnChange(false);
				addPeople(people);
				notifyDataSetChanged();
			}

			@Override
			public void onFinally() {
				mTask = null;
				updateProgress();
			}

			@Override
			public void onException(final Exception e) {
				mPaused = true;
				postException(e);
			}

			@Override
			public void onInterrupted(final Exception e) {

			}

		};
		mExecutor.execute(mTask.future());

		updateProgress();
	}

	@Override
	public void onScroll(final ContactListView contactListView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
		if (!mDone && mStarted && !mPaused && (totalItemCount - firstVisibleItem < 2.5 * visibleItemCount || totalItemCount == 0)) {
			fetchMore();
		}
	}

	/**
	 * True while fetching contacts
	 * 
	 * @return
	 */
	@Override
	public boolean isWorking() {
		return mTask != null;
	}

	public void updateProgress() {
		if (getProgressItem() == null) return;

		setNotifyOnChange(false);
		getAdapter().remove(getProgressItem());
		if (isWorking()) {
			getAdapter().add(getProgressItem());
		}
		notifyDataSetChanged();

		postWorking(isWorking());
	}

	@Override
	public void reload() {
		if (mStarted) {
			clearPeople();
			if (mTask != null) {
				mTask.cancel(true);
				mTask = null;
			}
			if (mOptions != null) {
				mOptions.setOffset(0);
			}
			mDone = false;
			mPaused = false;
			fetchMore();
		}
	}
}