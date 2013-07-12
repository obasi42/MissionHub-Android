package com.missionhub.people;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.model.Person;
import com.missionhub.ui.AdapterViewProvider;
import com.missionhub.ui.ObjectArrayAdapter;

import java.lang.ref.WeakReference;

/**
 * A PeopleListProvider acts as the bridge between a {@link com.missionhub.people.PeopleListView} and the
 * underlying data for that view. The Provider provides access to the data items. The provider
 * is also responsible for making a {@link android.view.View} for each item in the data set.
 */
public class PeopleListProvider extends ObjectArrayAdapter {

    /**
     * The android logging tag
     */
    public static final String TAG = PeopleListView.class.getSimpleName();
    /**
     * The {@link PeopleListView} this provider is attached to
     */
    private WeakReference<PeopleListView> mPeopleListView;

    /**
     * The {@link com.missionhub.ui.AdapterViewProvider} that provides views for provider items.
     */
    private AdapterViewProvider mAdapterViewProvider;

    /**
     * Constructs a new PeopleListProvider.
     *
     * @param context The current context.
     */
    public PeopleListProvider(Context context) {
        super(context);
    }

    /**
     * Constructs a new PeopleListProvider.
     *
     * @param context
     * @param maxViewTypes The maximum number of view types the {@link PeopleListView} will accept.
     */
    public PeopleListProvider(Context context, int maxViewTypes) {
        super(context, maxViewTypes);
    }

    /**
     * Callback method to be invoked when the list has been scrolled.
     * This will be called after the scroll has completed.
     *
     * @param peopleListView   The view whose scroll state is being reported
     * @param firstVisibleItem the index of the first visible cell (ignore if visibleItemCount == 0)
     * @param visibleItemCount the number of visible cells
     * @param totalItemCount   The number of items in the list adaptor
     */
    protected void onListScroll(PeopleListView peopleListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * @return The {@link PeopleListView} this provider is backing.
     */
    protected PeopleListView getPeopleList() {
        return mPeopleListView == null ? null : mPeopleListView.get();
    }

    /**
     * Method to bind the {@link PeopleListView} to the PeopleListProvider
     *
     * @param peopleListView
     */
    protected void setPeopleList(PeopleListView peopleListView) {
        mPeopleListView = new WeakReference<PeopleListView>(peopleListView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return onGetViewProvider().getView(this, position, convertView, parent);
    }

    /**
     * Returns the new default view provider used to create views for the list items.
     * @return
     */
    public AdapterViewProvider onCreateViewProvider() {
        return new SimplePersonAdapterViewProvider();
    }

    /**
     * Returns the view provider to be used to create views for the list items.
     *
     * @return The view provider
     */
    public AdapterViewProvider onGetViewProvider() {
        if (mAdapterViewProvider == null) {
            mAdapterViewProvider = onCreateViewProvider();
        }
        return mAdapterViewProvider;
    }


    /**
     * Returns the current adapter view provider
     *
     * @return the view provider
     */
    public AdapterViewProvider getAdapterViewProvider() {
        return mAdapterViewProvider;
    }

    /**
     * Sets the adapter view provider
     *
     * @param adapterViewProvider The view provider
     */
    public void setAdapterViewProvider(AdapterViewProvider adapterViewProvider) {
        mAdapterViewProvider = adapterViewProvider;
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasStableIds() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(final int position) {
        try {
            final Object object = getItem(position);
            if (object != null && object instanceof ItemIdProvider) {
                return ((ItemIdProvider) object).getItemId();
            }
            if (object instanceof Person) {
                return ((Person) object).getId();
            }
        } catch (final Exception e) { /* ignore */}
        return super.getItemId(position);
    }
}