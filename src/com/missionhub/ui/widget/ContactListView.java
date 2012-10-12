package com.missionhub.ui.widget;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import roboguice.util.SafeAsyncTask;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;

import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.ContactListOptions;
import com.missionhub.application.Application;
import com.missionhub.model.Person;
import com.missionhub.ui.ItemAdapter;
import com.missionhub.ui.item.ContactListItem;
import com.missionhub.ui.widget.SelectableListView.OnItemCheckedListener;
import com.missionhub.util.U;

/**
 * A generic list view for contacts
 * 
 * TODO: properly implement saved state such that view can be recreated with state
 */
public class ContactListView extends SelectableListView implements OnItemCheckedListener, OnItemClickListener, OnItemLongClickListener {

	/** the logging tag */
	public static final String TAG = ContactListView.class.getSimpleName();

	/** the adapter backing the listview */
	private final ItemAdapter mAdapter;

	/** the optional contact list options that controls the content of the view */
	private ContactListOptions mContactListOptions;

	/** the on contact checked listener */
	private OnContactCheckedListener mOnContactCheckedListener;

	/** the on contact click listener */
	private OnContactClickListener mOnContactClickListener;

	/** the on contact long click listener */
	private OnContactLongClickListener mOnContactLongClickListener;

	/** the default layout resource for contact list items */
	private int mDefaultContactItemViewResource = R.layout.item_contact;

	/** if data updates are paused */
	private final AtomicBoolean mPaused = new AtomicBoolean(false);

	/** the task used for fetching contacts */
	private SafeAsyncTask<List<Person>> mFetchTask;

	/** a map holding reference from a person to a list item */
	private final WeakHashMap<Person, ContactListItem> mPersonItemMap = new WeakHashMap<Person, ContactListItem>();

	/**
	 * Creates a new ContactListView
	 * 
	 * @param context
	 */
	public ContactListView(final Context context) {
		this(context, null, android.R.attr.listViewStyle);
	}

	/**
	 * Creates a new ContactListView
	 * 
	 * @param context
	 * @param attrs
	 */
	public ContactListView(final Context context, final AttributeSet attrs) {
		this(context, attrs, android.R.attr.listViewStyle);
	}

	/**
	 * Creates a new ContactListView
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ContactListView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		setDivider(getResources().getDrawable(R.drawable.abs__list_divider_holo_light));
		setDividerHeight(Math.round(U.dpToPixel(1, getContext())));
		setSelector(R.drawable.abs__list_selector_holo_light);

		// create the internal adapter
		mAdapter = new ItemAdapter(context);
		super.setAdapter(mAdapter);

		// set the listeners to this object which passes events to the onContact listeners
		super.setOnItemCheckedListener(this);
		super.setOnItemClickListener(this);
		super.setOnItemLongClickListener(this);
		super.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
				if (totalItemCount - firstVisibleItem < 2.5 * visibleItemCount) {
					fetchContacts();
				}
			}

			@Override
			public void onScrollStateChanged(final AbsListView view, final int scrollState) {}
		});
	}

	/**
	 * Set the defaut layout resource used when creating list items
	 * 
	 * @param resourceId
	 */
	public void setDefaultContactItemView(final int resourceId) {
		mDefaultContactItemViewResource = resourceId;
	}

	/**
	 * Sets the options which control the data of the list. Setting the options will clear the list. To modify the
	 * options without resetting the list, use getContactListOptions().
	 * 
	 * @param options
	 */
	public synchronized void setContactListOptions(final ContactListOptions options) {
		mContactListOptions = options;
		if (mFetchTask != null) {
			mFetchTask.cancel(true);
		}
		mFetchTask = null;
		clearList();
		fetchContacts();
	}

	/**
	 * Returns the options controlling the data in the list
	 * 
	 * @return
	 */
	public ContactListOptions getContactListOptions() {
		return mContactListOptions;
	}

	@Override
	@Deprecated
	public void setAdapter(final ListAdapter adapter) {
		throw new RuntimeException("An adapter is bound to this view. Use addContact, removeContact, insertContact, etc on this view.");
	}

	@Override
	@Deprecated
	public void setOnItemCheckedListener(final OnItemCheckedListener listener) {
		throw new RuntimeException("use setOnContactCheckedListener");
	}

	@Override
	@Deprecated
	public void setOnItemClickListener(final OnItemClickListener listener) {
		throw new RuntimeException("use setOnContactClickListener");
	}

	@Override
	@Deprecated
	public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
		throw new RuntimeException("use setOnContactLongClickListener");
	}

	@Override
	@Deprecated
	public void setOnItemSelectedListener(final OnItemSelectedListener listener) {
		throw new RuntimeException("use setOnContactCheckedListener for selection status");
	}

	@Override
	@Deprecated
	public void setOnScrollListener(final OnScrollListener listener) {
		throw new RuntimeException("the scroll listener is already bound to the ContactListView and cannot be set");
	}

	/**
	 * Called when a list item is long clicked. Passes the event to the OnContactLongClickListener if non-null.
	 */
	@Override
	public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (mOnContactLongClickListener != null) {
			return mOnContactLongClickListener.onContactLongClick(findPersonAt(position), position, id);
		}
		return false;
	}

	/**
	 * Interface definition for a callback to be invoked when a contact is long clicked.
	 */
	public interface OnContactLongClickListener {
		public boolean onContactLongClick(Person person, int position, long id);
	}

	/**
	 * Sets the OnContactLongClickListener
	 */
	public void setOnContactLongClickListener(final OnContactLongClickListener listener) {
		mOnContactLongClickListener = listener;
	}

	/**
	 * Called when a list item is clicked. Passes the event to the OnContactClickListener if non-null.
	 */
	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (mOnContactClickListener != null) {
			mOnContactClickListener.onContactClick(findPersonAt(position), position, id);
		}
	}

	/**
	 * Interface definition for a callback to be invoked when a contact is clicked.
	 */
	public interface OnContactClickListener {
		public void onContactClick(Person person, int position, long id);
	}

	/**
	 * Sets the OnContactClickListener
	 * 
	 * @param listener
	 */
	public void setOnContactClickListener(final OnContactClickListener listener) {
		mOnContactClickListener = listener;
	}

	/**
	 * Called when a list item is checked or unchecked.
	 */
	@Override
	public void onSetItemChecked(final int position, final boolean checked) {
		if (mOnContactCheckedListener != null) {
			mOnContactCheckedListener.onContactChecked(findPersonAt(position), position, checked);
		}
	}

	/**
	 * Called when all items in the list have been unchecked.
	 */
	@Override
	public void onAllUnchecked() {
		if (mOnContactCheckedListener != null) {
			mOnContactCheckedListener.onAllContactsUnchecked();
		}
	}

	/**
	 * Interface definition for callbacks to be invoked when contacts are checked and unchecked.
	 */
	public interface OnContactCheckedListener {
		public void onContactChecked(Person person, int position, boolean checked);

		public void onAllContactsUnchecked();
	}

	/**
	 * Sets the OnContactCheckedListener
	 * 
	 * @param listener
	 */
	public void setOnContactCheckedListener(final OnContactCheckedListener listener) {
		mOnContactCheckedListener = listener;
	}

	/**
	 * Finds the person object for an item at a given adapter position
	 * 
	 * @param position
	 * @return
	 */
	private Person findPersonAt(final int position) {
		return ((ContactListItem) mAdapter.getItem(position)).person;
	}

	/**
	 * Adds a contact list item to the list
	 * 
	 * @param item
	 */
	public void addItem(final ContactListItem item) {
		mPersonItemMap.put(item.person, item);
		mAdapter.add(item);
	}

	/**
	 * Removes a contact list item from the list
	 * 
	 * @param item
	 */
	public void removeItem(final ContactListItem item) {
		mPersonItemMap.remove(item.person);
		mAdapter.remove(item);
	}

	/**
	 * Inserts a contact list item into the list at a given index
	 * 
	 * @param item
	 * @param index
	 */
	public void insertItem(final ContactListItem item, final int index) {
		mPersonItemMap.put(item.person, item);
		mAdapter.insert(item, index);
	}

	/**
	 * Adds a person to the list.
	 * 
	 * @param person
	 */
	public void addPerson(final Person person) {
		final ContactListItem item = new ContactListItem(person, mDefaultContactItemViewResource);
		addItem(item);
	}

	/**
	 * Removes a person from the list.
	 * 
	 * @param person
	 */
	public void removePerson(final Person person) {
		final ContactListItem item = mPersonItemMap.get(person);
		removeItem(item);
	}

	/**
	 * Inserts a person into the list at a given index.
	 * 
	 * @param person
	 * @param index
	 */
	public void insertPerson(final Person person, final int index) {
		final ContactListItem item = new ContactListItem(person, mDefaultContactItemViewResource);
		insertItem(item, index);
	}

	/**
	 * Removes all people from the list.
	 */
	public void clearList() {
		mPersonItemMap.clear();
		mAdapter.clear();
	}

	/**
	 * Adds multiple people the the list in efficiently.
	 * 
	 * @param people
	 */
	public void addPeople(final List<Person> people) {
		mAdapter.setNotifyOnChange(false);
		for (final Person p : people) {
			addPerson(p);
		}
		mAdapter.setNotifyOnChange(true);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Removed multiple people from the list efficiently.
	 * 
	 * @param people
	 */
	public void removePeople(final List<Person> people) {
		mAdapter.setNotifyOnChange(false);
		for (final Person p : people) {
			removePerson(p);
		}
		mAdapter.setNotifyOnChange(true);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Sets if list updates are paused. This only has an effect if the list is operating off of contact list options.
	 * 
	 * @param paused
	 */
	public void setPaused(final boolean paused) {
		mPaused.set(paused);
	}

	/**
	 * Internal method for fetching the next set of contacts from the api based on the contact list options. Calling
	 * this while a fetch is occurring will have no effect.
	 */
	private synchronized void fetchContacts() {
		if (mFetchTask != null || mPaused.get() || mContactListOptions == null || mContactListOptions.isAtEnd()) return;

		mFetchTask = new SafeAsyncTask<List<Person>>() {

			/** store the options when the task was started to track if the options item changes */
			final ContactListOptions options = mContactListOptions;

			@Override
			public List<Person> call() throws Exception {
				return Api.getContactList(mContactListOptions).get();
			}

			@Override
			public void onPreExecute() {
				// TODO: start progress bar
			}

			@Override
			public void onSuccess(final List<Person> people) {
				// the options have changed and this response is no longer valid
				if (options != mContactListOptions) {
					return;
				}

				// set up the options for the next run
				if (people.size() < mContactListOptions.getLimit()) {
					mContactListOptions.setIsAtEnd(true);
					mContactListOptions.incrementStart(people.size());
				} else {
					mContactListOptions.advanceStart();
				}

				// add the people to the list
				addPeople(people);
			}

			@Override
			public void onInterrupted(final Exception e) {
				// pass this to onThrowable so we only have to handle exceptions one place.
				onException(e);
			}

			@Override
			public void onException(final Exception e) {
				// pass this to onThrowable so we only have to handle exceptions one place.
				onThrowable(e);
			}

			@Override
			public void onThrowable(final Throwable t) {
				// TODO handle error
				Log.e(TAG, t.getMessage(), t);

				// the options have changed and this response is no longer valid
				if (options != mContactListOptions) {
					return;
				}

				// put ui code here
			}

			@Override
			public void onFinally() {
				// TODO: stop progress bar

				// the options have changed and this response is no longer valid
				if (options != mContactListOptions) {
					return;
				}

				// this is after checking for changed options, as setting the options will null the task.
				mFetchTask = null;
			}
		};
		Application.getExecutor().execute(mFetchTask.future());
	}
}