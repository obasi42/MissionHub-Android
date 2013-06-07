package com.missionhub.people;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;

import com.missionhub.model.Person;
import com.missionhub.ui.widget.SelectableListView;
import com.missionhub.ui.widget.SelectableListView.OnItemCheckedListener;

/**
 * A generic ListView for displaying people.
 */
public class PeopleListView extends SelectableListView implements OnItemCheckedListener, OnItemClickListener, OnItemLongClickListener, AbsListView.OnScrollListener {

    /**
     * The android logging tag
     */
    public static final String TAG = PeopleListView.class.getSimpleName();
    /**
     * Listener used to dispatch person checked events
     */
    private OnPersonCheckedListener mOnPersonCheckedListener;
    /**
     * Listener used to dispatch person click events
     */
    private OnPersonClickListener mOnPersonClickListener;
    /**
     * Listener used to dispatch person long click events
     */
    private OnPersonLongClickListener mOnPersonLongClickListener;
    /**
     * Listener used to dispatch scroll events
     */
    private OnScrollListener mOnScrollListener;
    /**
     * the person list provider
     */
    private PeopleListProvider mProvider;

    /**
     * Construct a new PeopleListView with default styling.
     *
     * @param context The Context that will determine this widget's theming.
     */
    public PeopleListView(final Context context) {
        this(context, null);
    }

    /**
     * Construct a new PeopleListView with default styling.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs   Specification of attributes that should deviate from default styling.
     */
    public PeopleListView(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    /**
     * Construct a new PeopleListView with default styling.
     *
     * @param context  The Context that will determine this widget's theming.
     * @param attrs    Specification of attributes that should deviate from default styling.
     * @param defStyle An attribute ID within the active theme containing a reference to the
     *                 default style for this widget. e.g. android.R.attr.listViewStyle.
     */
    public PeopleListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        super.setOnItemCheckedListener(this);
        super.setOnItemClickListener(this);
        super.setOnItemLongClickListener(this);
        super.setOnScrollListener(this);
    }

    /**
     * @deprecated Use {@link #setProvider(PeopleListProvider)}
     */
    @Override
    @Deprecated
    public void setAdapter(final ListAdapter adapter) {
        throw new RuntimeException("Use PeopleListProvider to provided the list data.");
    }

    /**
     * @deprecated Use {@link #setOnPersonCheckedListener(PeopleListView.OnPersonCheckedListener)}
     */
    @Override
    @Deprecated
    public void setOnItemCheckedListener(final OnItemCheckedListener listener) {
        throw new RuntimeException("use setOnPersonCheckedListener");
    }

    /**
     * @deprecated Use {@link #setOnPersonClickListener(PeopleListView.OnPersonClickListener)}
     */
    @Override
    @Deprecated
    public void setOnItemClickListener(final OnItemClickListener listener) {
        throw new RuntimeException("use setOnPersonClickListener");
    }

    /**
     * @deprecated Use {@link #setOnPersonLongClickListener(PeopleListView.OnPersonLongClickListener)}
     */
    @Override
    @Deprecated
    public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
        throw new RuntimeException("use setOnPersonLongClickListener");
    }

    /**
     * @deprecated Use {@link #setOnPersonCheckedListener(PeopleListView.OnPersonCheckedListener)}
     */
    @Override
    @Deprecated
    public void setOnItemSelectedListener(final OnItemSelectedListener listener) {
        throw new RuntimeException("use setOnPersonCheckedListener for selection status");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnScrollListener(final OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (mProvider != null) {
            mProvider.onListScroll(this, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (mOnPersonLongClickListener != null) {
            final Object item = parent.getItemAtPosition(position);
            if (item instanceof Person) {
                return mOnPersonLongClickListener.onPersonLongClick(this, (Person) item, position, id);
            }
        }
        return false;
    }

    /**
     * Register a callback to be invoked when a person in the PeopleListView has been long clicked.
     *
     * @param listener The callback that will run
     */
    public void setOnPersonLongClickListener(final OnPersonLongClickListener listener) {
        mOnPersonLongClickListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (mOnPersonClickListener != null) {
            final Object item = parent.getItemAtPosition(position);
            if (item instanceof Person) {
                mOnPersonClickListener.onPersonClick(this, (Person) item, position, id);
            }
        }
    }

    /**
     * Register a callback to be invoked when a person in the PeopleListView has been clicked.
     *
     * @param listener The callback that will run
     */
    public void setOnPersonClickListener(final OnPersonClickListener listener) {
        mOnPersonClickListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSetItemChecked(final int position, final boolean checked) {
        if (mOnPersonCheckedListener != null) {
            final Object item = getItemAtPosition(position);
            if (item instanceof Person) {
                mOnPersonCheckedListener.onPersonChecked(this, (Person) item, position, checked);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAllUnchecked() {
        if (mOnPersonCheckedListener != null) {
            mOnPersonCheckedListener.onAllPeopleUnchecked();
        }
    }

    /**
     * Register a callback to be invoked when a contacts in the PeopleListView have been checked or unchecked.
     *
     * @param listener The callback that will run
     */
    public void setOnPersonCheckedListener(final OnPersonCheckedListener listener) {
        mOnPersonCheckedListener = listener;
    }

    /**
     * Returns the provider currently associated with this widget
     *
     * @return The provider used to create this view's content
     */
    public PeopleListProvider getProvider() {
        return mProvider;
    }

    /**
     * Sets the {@link PeopleListProvider} that provides the data and the views to represent
     * the data in this widget.
     *
     * @param provider The provider to use to create this view's content.
     */
    public void setProvider(final PeopleListProvider provider) {
        mProvider = provider;
        mProvider.setPeopleList(this);
        super.setAdapter(mProvider);
    }

    /**
     * Interface definition for a callback to be invoked when a contact is long clicked.
     */
    public interface OnPersonLongClickListener {

        /**
         * Callback method to be invoked when a person has been long clicked.
         *
         * @param list     The PeopleListView where the long click happened
         * @param person   The person that was long clicked
         * @param position The position of the view in the list
         * @param id       The row id of the item that was long clicked
         * @return true if the callback consumed the long click, false otherwise
         */
        public boolean onPersonLongClick(PeopleListView list, Person person, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when a contact is clicked.
     */
    public interface OnPersonClickListener {

        /**
         * Callback method to be invoked when a person has been clicked.
         *
         * @param list     The PeopleListView where the click happened
         * @param person   The person that was clicked
         * @param position The position of the view in the list
         * @param id       The row id of the item that was clicked
         */
        public void onPersonClick(PeopleListView list, Person person, int position, long id);
    }

    /**
     * Interface definition for callbacks to be invoked when people are checked and unchecked.
     */
    public interface OnPersonCheckedListener {

        /**
         * Callback method to be invoked when a person has been checked or unchecked
         *
         * @param list     The PeopleListView where the click happened
         * @param person   The person that was clicked
         * @param position The position of the view in the list
         * @param checked  True if the person is checked
         */
        public void onPersonChecked(PeopleListView list, Person person, int position, boolean checked);

        /**
         * Callback method to be invoked when all people in the list have been unchecked
         */
        public void onAllPeopleUnchecked();
    }
}