package com.missionhub.android.contactlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import com.missionhub.android.contactlist.ContactListAdapter.ContactItem;
import com.missionhub.android.model.Person;
import com.missionhub.android.ui.widget.SelectableListView;
import com.missionhub.android.ui.widget.SelectableListView.OnItemCheckedListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * A generic list view for contacts
 */
public class ContactListView extends SelectableListView implements OnItemCheckedListener, OnItemClickListener, OnItemLongClickListener {

    /**
     * the logging tag
     */
    public static final String TAG = ContactListView.class.getSimpleName();

    /**
     * the on contact checked listener
     */
    private OnContactCheckedListener mOnContactCheckedListener;

    /**
     * the on contact click listener
     */
    private OnContactClickListener mOnContactClickListener;

    /**
     * the on contact long click listener
     */
    private OnContactLongClickListener mOnContactLongClickListener;

    /**
     * the contact list provider
     */
    private ContactListProvider mProvider;

    /**
     * Creates a new ContactListView
     *
     * @param context
     */
    public ContactListView(final Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new ContactListView
     *
     * @param context
     * @param attrs
     */
    public ContactListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
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
        init();
    }

    public void init() {
        super.setOnItemCheckedListener(this);
        super.setOnItemClickListener(this);
        super.setOnItemLongClickListener(this);

        super.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, new OnScrollListener() {

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                if (mProvider != null) {
                    mProvider.onScroll(ContactListView.this, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }

            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {

            }
        }));
    }

    @Override
    @Deprecated
    public void setAdapter(final ListAdapter adapter) {
        throw new RuntimeException("Use custom ContactListProvider to provided the list data.");
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
            final Object item = parent.getItemAtPosition(position);
            if (item instanceof ContactItem) {
                return mOnContactLongClickListener.onContactLongClick(((ContactItem) item).person, position, id);
            }
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
            final Object item = parent.getItemAtPosition(position);
            if (item instanceof ContactItem) {
                mOnContactClickListener.onContactClick(((ContactItem) item).person, position, id);
            }
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
            final Object item = getItemAtPosition(position);
            if (item instanceof ContactItem) {
                mOnContactCheckedListener.onContactChecked(((ContactItem) item).person, position, checked);
            }
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
     * Sets the contact list provider
     */
    public void setProvider(final ContactListProvider provider) {
        if (provider != null) {
            mProvider = provider;
            mProvider.setContactList(this);
            super.setAdapter(mProvider.getAdapter());
        }
    }

    /**
     * Returns the current contact list provider
     */
    public ContactListProvider getProvider() {
        return mProvider;
    }
}