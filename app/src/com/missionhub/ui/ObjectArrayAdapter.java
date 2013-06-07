package com.missionhub.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.holoeverywhere.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An "super" array adapter for generic objects
 */
public abstract class ObjectArrayAdapter<T> extends BaseAdapter {

    /**
     * List of all the objects in the adapter.
     */
    private final List<T> mObjects = new ArrayList<T>();
    /**
     * List of the active objects in the adapter.
     */
    private final List<T> mActiveObjects = new ArrayList<T>();
    /**
     * List of the hidden objects in the adpter.
     */
    private final List<T> mHiddenObjects = new ArrayList<T>();
    /**
     * Lock object to synchronize operations on.
     */
    private final Object mLock = new Object();
    /**
     * List of the type of views added to the adapter.
     */
    private final List<Class<? extends Object>> mTypes = new ArrayList<Class<? extends Object>>();
    /**
     * The maximum number of view types allowed.
     */
    private final int mMaxViewTypes;
    /**
     * The context for generating views and other context sensitive operations.
     */
    private Context mContext;
    /**
     * True when changes to the adapter should notify the list.
     */
    private boolean mNotify = true;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public ObjectArrayAdapter(final Context context) {
        this(context, 10);
    }

    /**
     * Constructor
     *
     * @param context      The current context.
     * @param maxViewTypes The maximum number of view types allowed.
     */
    public ObjectArrayAdapter(final Context context, final int maxViewTypes) {
        mContext = context;
        mMaxViewTypes = maxViewTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return mActiveObjects.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getItem(final int position) {
        return mActiveObjects.get(position);
    }

    /**
     * @param object The object to find
     * @return True if the adapter contains the object
     */
    public boolean contains(final T object) {
        return mActiveObjects.contains(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(final int position) {
        try {
            final T object = mActiveObjects.get(position);
            if (object != null && object instanceof ItemIdProvider) {
                return ((ItemIdProvider) object).getItemId();
            }
        } catch (final Exception e) { /* ignore */}
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getViewTypeCount() {
        return mMaxViewTypes;
    }

    /**
     * Adds a view type.
     *
     * @param clss The view type class.
     */
    private void addType(final Class<? extends Object> clss) {
        if (!mTypes.contains(clss)) {
            mTypes.add(clss);
        }
        if (mTypes.size() > mMaxViewTypes) {
            throw new RuntimeException("Max view types limit reached.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(final int position) {
        final T object = getItem(position);
        if (object == null) return IGNORE_ITEM_VIEW_TYPE;
        return mTypes.indexOf(object.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    /**
     * Adds an object to the adapter.
     *
     * @param object The object to add.
     */
    public void add(final T object) {
        synchronized (mLock) {
            mObjects.add(object);
            mActiveObjects.add(object);
            addType(object.getClass());
        }
        maybeNotify();
    }

    /**
     * Adds a collection of objects to the adapter.
     *
     * @param objects The objects to add.
     */
    public void addAll(final Collection<T> objects) {
        synchronized (mLock) {
            mObjects.addAll(objects);
            mActiveObjects.addAll(objects);
            for (T object : objects) {
                addType(object.getClass());
            }
        }
        maybeNotify();
    }

    /**
     * Performs an unsynchronized insert.
     *
     * @param object The object to insert
     * @param index  The index
     */
    private void unsafeInsert(final T object, final int index) {
        mObjects.add(index, object);
        addType(object.getClass());
        refreshHidden();
    }

    /**
     * Inserts an object in to the array at a given index.
     *
     * @param object The object to insert
     * @param index  The index
     */
    public void insert(final T object, final int index) {
        synchronized (mLock) {
            unsafeInsert(object, index);
        }
        maybeNotify();
    }

    /**
     * Inserts an object before another object.
     *
     * @param object       The object to insert
     * @param beforeObject The object to insert the object before
     */
    public void insertBefore(final T object, final T beforeObject) {
        synchronized (mLock) {
            final int index = mObjects.indexOf(beforeObject);
            if (index == -1) return;
            unsafeInsert(object, index);
        }
        maybeNotify();
    }

    /**
     * Inserts an object after another object.
     *
     * @param object      The object to insert
     * @param afterObject The object to insert the object after
     */
    public void insertAfter(final T object, final T afterObject) {
        synchronized (mLock) {
            int index = mObjects.indexOf(afterObject);
            if (index == -1) return;

            index += 1;

            if (index > mObjects.size()) index = mObjects.size();

            unsafeInsert(object, index);
        }
        maybeNotify();
    }

    /**
     * Removes an object from the adapter.
     *
     * @param object The object to remove
     */
    public void remove(final T object) {
        synchronized (mLock) {
            mObjects.remove(object);
            mActiveObjects.remove(object);
            mHiddenObjects.remove(object);
        }
        maybeNotify();
    }

    /**
     * Removes a collection of objects from the adapter.
     *
     * @param objects The objects to remove.
     */
    public void removeAll(final Collection<T> objects) {
        synchronized (mLock) {
            mObjects.removeAll(objects);
            mActiveObjects.removeAll(objects);
            mHiddenObjects.removeAll(objects);
        }
        maybeNotify();
    }

    /**
     * Replaces an old object with a new one.
     *
     * @param oldObject The old object
     * @param newObject The new object
     */
    public void replace(final T oldObject, final T newObject) {
        synchronized (mLock) {
            replaceObject(oldObject, newObject, mObjects);
            replaceObject(oldObject, newObject, mActiveObjects);
            replaceObject(oldObject, newObject, mHiddenObjects);
            addType(newObject.getClass());
        }
        maybeNotify();
    }

    /**
     * Replaces an old object with a new one in the given list.
     *
     * @param oldObject The old object
     * @param newObject The new object
     * @param list      The list to work on
     */
    private void replaceObject(final T oldObject, final T newObject, final List<T> list) {
        final int index = list.indexOf(oldObject);
        if (index == -1) return;
        list.set(index, newObject);
    }

    /**
     * Removes all objects from the adapter.
     */
    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
            mActiveObjects.clear();
            mHiddenObjects.clear();
        }
        maybeNotify();
    }

    /**
     * Hides an object to prevent its display.
     *
     * @param object The object to hide
     */
    public void hide(final T object) {
        synchronized (mLock) {
            mHiddenObjects.add(object);
            mActiveObjects.remove(object);
        }
        maybeNotify();
    }

    /**
     * Shows an object to allow its display.
     *
     * @param object The object to show
     */
    public void show(final T object) {
        synchronized (mLock) {
            mHiddenObjects.remove(object);
            refreshHidden();
        }
        maybeNotify();
    }

    /**
     * Shows all currently hidden objects.
     */
    public void showAll() {
        synchronized (mLock) {
            mHiddenObjects.clear();
            refreshHidden();
        }
        maybeNotify();
    }

    /**
     * Keeps the list of hidden objects up to date after object changes.
     */
    private void refreshHidden() {
        mActiveObjects.clear();
        for (final T object : mObjects) {
            if (!mHiddenObjects.contains(object)) {
                mActiveObjects.add(object);
            }
        }
    }

    /**
     * Possibly notifies the list that the data set has changed.
     */
    private void maybeNotify() {
        if (mNotify) {
            notifyDataSetChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        setNotifyOnChange(true);
    }

    /**
     * Sets whether or not the list should be notified on adapter object changes.
     *
     * @param notify
     */
    public void setNotifyOnChange(final boolean notify) {
        mNotify = notify;
    }

    /**
     * Sorts the list by a given comparator.
     *
     * @param comparator The comparator
     */
    public void sort(final Comparator<? super T> comparator) {
        Collections.sort(mObjects, comparator);
        Collections.sort(mActiveObjects, comparator);
        maybeNotify();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return mActiveObjects.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled(final int position) {
        final T object = getItem(position);
        if (object != null && object instanceof SupportEnable) {
            return ((SupportEnable) object).isEnabled();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areAllItemsEnabled() {
        for (int i = 0; i < getCount(); i++) {
            if (!isEnabled(i)) {
                return false;
            }
        }
        return super.areAllItemsEnabled();
    }

    /**
     * @return The context
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Sets the context for the adapter. Ideally this should be set before the adapter is attached to the adapter view.
     *
     * @param context
     */
    public void setContext(final Context context) {
        synchronized (mLock) {
            mContext = context;
            notifyDataSetChanged();
        }
    }

    /**
     * @return A LayoutInflater for the current context.
     */
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    /**
     * Interface definition for callbacks to be invoked to determine an objects row id.
     */
    public interface ItemIdProvider {
        /**
         * @return The object's row id.
         * @see BaseAdapter#getItemId(int)
         */
        long getItemId();
    }

    /**
     * Interface definition to provide a callback to determine if a list item is enabled.
     */
    public interface SupportEnable {
        /**
         * @return True if the list item is enabled.
         */
        public boolean isEnabled();
    }

    /**
     * Class to extend to disable a list item.
     */
    public abstract static class DisabledItem implements SupportEnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEnabled() {
            return false;
        }
    }
}