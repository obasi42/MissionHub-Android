package com.missionhub.people;

import android.content.Context;

import com.missionhub.model.Person;

import java.util.Collection;

/**
 * A dynamic {@link PeopleListProvider} that supports dynamic loading of people.
 */
public abstract class DynamicPeopleListProvider extends PeopleListProvider {

    /**
     * The android logging tag
     */
    public static final String TAG = DynamicPeopleListProvider.class.getSimpleName();
    /**
     * The loading list item object.
     */
    private final LoadingItem mLoadingItem = new LoadingItem();
    /**
     * The empty list item object
     */
    private final EmptyItem mEmptyItem = new EmptyItem();
    /**
     * The started state flag. True if the provider has begun loading people.
     */
    private boolean mStarted = true;
    /**
     * The paused state flag. True when the provider is paused.
     */
    private boolean mPaused = false;
    /**
     * The loading state flag. True when the provider is loading people.
     */
    private boolean mLoading = false;
    /**
     * The done state flag. True when all of the possible people have been loaded.
     */
    private boolean mDone = false;
    /**
     * Listener used to dispatch loading events
     */
    private OnLoadingListener mLoadingListener;
    /**
     * Listener used to dispatch exception events
     */
    private OnExceptionListener mOnExceptionListener;

    /**
     * Constructs a new DynamicPeopleListProvider.
     *
     * @param context The current context.
     */
    public DynamicPeopleListProvider(Context context) {
        this(context, true);
    }

    /**
     * Constructs a new DynamicPeopleListProvider.
     *
     * @param context The current context.
     * @param start   True if the provider should begin loading immediately.
     */
    public DynamicPeopleListProvider(Context context, boolean start) {
        this(context, start, 3);
    }

    /**
     * Constructs a new DynamicPeopleListProvider.
     *
     * @param context
     * @param start        True if the provider should begin loading immediately.
     * @param maxViewTypes The maximum number of view types the {@link PeopleListView} will accept.
     */
    public DynamicPeopleListProvider(Context context, boolean start, int maxViewTypes) {
        super(context, maxViewTypes);
        mStarted = start;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onListScroll(final PeopleListView peopleListView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        if (shouldLoad() && (totalItemCount - firstVisibleItem < 2.5 * visibleItemCount || totalItemCount == 0)) {
            load();
        }
    }

    /**
     * Determines if the provider should load more people
     *
     * @return true if the provider can load more people
     */
    public boolean shouldLoad() {
        synchronized (getLock()) {
            return isStarted() && !isPaused() && !isLoading() && !isDone();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setPeopleList(PeopleListView peopleListView) {
        super.setPeopleList(peopleListView);
        if (shouldLoad()) {
            load();
        }
    }

    /**
     * Loads more people for the {@link PeopleListView}
     */
    public void load() {
        synchronized (getLock()) {
            setLoading(true);

            Collection<Person> people = null;
            try {
                people = loadMore();
            } catch (Exception e) {
                onException(e);
            }
            onAfterLoad(people);

            setLoading(false);
        }
    }

    /**
     * Method to be invoked to load more people for the list.
     *
     * @return Collection of people to be added to the list in {@link #onAfterLoad(java.util.Collection)}
     */
    public abstract Collection<Person> loadMore();

    /**
     * Method to be invoked to cancel loading
     */
    public abstract void cancelLoadMore();

    /**
     * Callback method to be invoked after data has been loaded. This callback is responsible for
     * adding the loaded people to the list and setting state flags.
     *
     * @param people
     */
    public void onAfterLoad(Collection<Person> people) {
        if (people != null) {
            synchronized (getLock()) {
                addAll(people);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * Clears all data and state parameters and begins loading from the beginning
     */
    public void reload() {
        synchronized (getLock()) {
            try {
                cancelLoadMore();
            } catch (Exception e) {
                onException(e);
            }
            clear();
            setLoading(false);
            setPaused(false);
            setDone(false);
            if (getPeopleList() != null) {
                getPeopleList().clearChoices();
            }
            load();
        }
    }

    /**
     * @return true if the provider is loading
     */
    public boolean isLoading() {
        synchronized (getLock()) {
            return mLoading;
        }
    }

    /**
     * Sets the loading state of the provider
     *
     * @param loading true if the provider is loading
     */
    public void setLoading(boolean loading) {
        synchronized (getLock()) {
            mLoading = loading;
            updateLoadingEmptyItems(true);
            onLoading(loading);
        }
    }

    private void updateLoadingEmptyItems(boolean notify) {
        setNotifyOnChange(false);
        super.remove(mLoadingItem);
        if (isLoading()) {
            super.add(mLoadingItem);
        }
        super.remove(mEmptyItem);
        if (!isLoading() && isEmpty()) {
            super.add(mEmptyItem);
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(final Object object) {
        super.remove(object);
        updateLoadingEmptyItems(true);
    }

    @Override
    public void removeAll(Collection objects) {
        super.removeAll(objects);
        updateLoadingEmptyItems(true);
    }

    /**
     * @return true if the provider has been started
     */
    public boolean isStarted() {
        synchronized (getLock()) {
            return mStarted;
        }
    }

    /**
     * Sets the started state value. If the adapter is not started, begins loading data.
     *
     * @param started
     */
    public void setStarted(boolean started) {
        synchronized (getLock()) {
            if (started && !mStarted) {
                mStarted = started;
                load();
            }
            mStarted = started;
        }
    }

    /**
     * @return true when the provider is paused
     */
    public boolean isPaused() {
        synchronized (getLock()) {
            return mPaused;
        }
    }

    /**
     * Sets the paused state. True cancels current load operation and prevents future operations.
     *
     * @param paused true pauses the provider
     */
    public void setPaused(boolean paused) {
        synchronized (getLock()) {
            if (mPaused != paused && paused) {
                try {
                    mPaused = paused;
                    cancelLoadMore();
                } catch (Exception e) {
                    onException(e);
                }
            } else {
                mPaused = paused;
            }
        }
    }

    /**
     * @return true when the last person record has been loaded
     */
    public boolean isDone() {
        synchronized (getLock()) {
            return mDone;
        }
    }

    /**
     * Sets the done state
     *
     * @param done true if the last person record has been loaded
     */
    public void setDone(boolean done) {
        synchronized (getLock()) {
            mDone = done;
        }
    }

    /**
     * Register a callback to be invoked when the provider loading state has changed
     *
     * @param loadingListener The callback that will run
     */
    public void setLoadingListener(OnLoadingListener loadingListener) {
        mLoadingListener = loadingListener;
    }

    /**
     * Register a callback to be invoked when an exception is thrown by the provider while loading
     * people.
     *
     * @param onExceptionListener The callback that will run
     */
    public void setOnExceptionListener(OnExceptionListener onExceptionListener) {
        mOnExceptionListener = onExceptionListener;
    }

    /**
     * Method invoked when the loading state has changed. Passes events to the registered
     * {@link OnLoadingListener}
     *
     * @param loading true when the provider is loading
     */
    public void onLoading(boolean loading) {
        if (mLoadingListener != null) {
            mLoadingListener.onLoading(loading);
        }
    }

    /**
     * Method invoked when an exception is thrown while loading people or canceling a current loading
     * operation. Invokes the {@link OnExceptionListener} if registered.
     *
     * @param t The exception that was thrown.
     */
    public void onException(Throwable t) {
        if (mOnExceptionListener != null) {
            mOnExceptionListener.onException(t);
        }
    }

    /**
     * Interface definition for callbacks to be invoked when the loading state has changed
     */
    public static interface OnLoadingListener {

        /**
         * Callback method to be invoked when the provider loading state has changed.
         *
         * @param loading true when the provider is loading more people
         */
        public void onLoading(boolean loading);
    }

    /**
     * Interface definition for callbacks to be invoked when an exception has been thrown while
     * loading contacts or canceling a current load operation.
     */
    public static interface OnExceptionListener {

        /**
         * Callback method to be invoked when an exception has been thrown.
         *
         * @param t The exception throwable
         */
        public void onException(Throwable t);
    }

    /**
     * Simple object to define the loading item in the list.
     */
    public static class LoadingItem extends DisabledItem implements ItemIdProvider {
        @Override
        public long getItemId() {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Simple object to define the empty item in the list
     */
    public static class EmptyItem extends DisabledItem implements ItemIdProvider {
        @Override
        public long getItemId() {
            return Integer.MIN_VALUE + 1;
        }
    }
}