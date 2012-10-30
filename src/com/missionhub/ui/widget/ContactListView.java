package com.missionhub.ui.widget;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;

import com.missionhub.R;
import com.missionhub.application.ObjectStore;
import com.missionhub.model.Person;
import com.missionhub.ui.ContactListProvider;
import com.missionhub.ui.ContactListProvider.ContactListProviderListener;
import com.missionhub.ui.ItemAdapter;
import com.missionhub.ui.item.ContactListItem;
import com.missionhub.ui.item.ProgressItem;
import com.missionhub.ui.widget.SelectableListView.OnItemCheckedListener;
import com.missionhub.util.U;

/**
 * A generic list view for contacts
 */
public class ContactListView extends SelectableListView implements ContactListProviderListener, OnItemCheckedListener, OnItemClickListener, OnItemLongClickListener {

	/** the logging tag */
	public static final String TAG = ContactListView.class.getSimpleName();

	/** the adapter backing the listview */
	private final ItemAdapter mAdapter;

	/** the on contact checked listener */
	private OnContactCheckedListener mOnContactCheckedListener;

	/** the on contact click listener */
	private OnContactClickListener mOnContactClickListener;

	/** the on contact long click listener */
	private OnContactLongClickListener mOnContactLongClickListener;

	/** the layout resource for contact list items */
	private int mContactItemViewResource = R.layout.item_contact;

	/** maintains a map of people to contact list items so the items don't have to be recreated on data changes */
	private final Map<Person, ContactListItem> mPersonItemMap = Collections.synchronizedMap(new WeakHashMap<Person, ContactListItem>());

	/** the contacts list provider */
	private ContactListProvider mProvider;

	/** the progress item */
	private final ProgressItem mProgressItem = new ProgressItem();

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

		if (attrs != null) {
			final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContactListView, 0, 0);

			try {
				mContactItemViewResource = a.getResourceId(R.styleable.ContactListView_item_layout_resource, R.layout.item_contact);
			} finally {
				a.recycle();
			}
		}

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
				if (mProvider != null && !mProvider.isAtEnd() && (totalItemCount - firstVisibleItem < 2.5 * visibleItemCount || totalItemCount == 0)) {
					mProvider.getMoreAsync();
					updateProgressItem();
				}
			}

			@Override
			public void onScrollStateChanged(final AbsListView view, final int scrollState) {}
		});
	}

	/**
	 * Set the layout resource used when creating list items
	 * 
	 * @param resourceId
	 */
	public void setContactItemView(final int resourceId) {
		mContactItemViewResource = resourceId;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	@Deprecated
	public void setAdapter(final ListAdapter adapter) {
		throw new RuntimeException("An adapter is bound to this view. Use custom ContactListProvider to provided the list data.");
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

	@Override
	public Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("superState", super.onSaveInstanceState());
		bundle.putString("mProvider", ObjectStore.getInstance().storeObject(mProvider));
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(final Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			setProvider((ContactListProvider) ObjectStore.getInstance().retrieveObject(bundle.getString("mProvider")));
			super.onRestoreInstanceState(bundle.getParcelable("superState"));
			return;
		}
		super.onRestoreInstanceState(state);
	}

	public void setProvider(final ContactListProvider provider) {
		mProvider = provider;
		mProvider.setContactListProviderListener(this);
	}

	public ContactListProvider getProvider() {
		return mProvider;
	}

	@Override
	public void onContactsChanged(final List<Person> people) {
		if (people == null) return;

		mAdapter.setNotifyOnChange(false);
		mAdapter.clear();

		for (final Person person : people) {
			ContactListItem item = mPersonItemMap.get(person);

			if (item == null) {
				item = new ContactListItem(person, mContactItemViewResource);
				mPersonItemMap.put(person, item);
			}

			mAdapter.add(item);
		}

		updateProgressItem();

		mAdapter.notifyDataSetChanged();
	}

	public void updateProgressItem() {
		if (mProvider == null) return;

		mAdapter.remove(mProgressItem);

		if (mProvider.isGettingMore()) {
			mAdapter.add(mProgressItem);
		}
	}
}