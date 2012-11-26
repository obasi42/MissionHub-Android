package com.missionhub.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * An "super" array adapter for generic objects
 */
public abstract class ObjectArrayAdapter extends BaseAdapter {

	/** the context for generating view */
	private Context mContext;

	/** all of the objects in the adapter */
	private final List<Object> mObjects = new ArrayList<Object>();

	/** the objects the list uses */
	private final List<Object> mActiveObjects = new ArrayList<Object>();

	/** the hidden objects */
	private final List<Object> mHiddenObjects = new ArrayList<Object>();

	/** lock to synchronize on */
	private final Object mLock = new Object();

	/** notify on all changes to the list */
	private boolean mNotify = true;

	/** the types of views */
	private final List<Class<? extends Object>> mTypes = new ArrayList<Class<? extends Object>>();

	/** the max number of view types */
	private final int mMaxViewTypes;

	public ObjectArrayAdapter(final Context context) {
		this(context, 10);
	}

	public ObjectArrayAdapter(final Context context, final int maxViewTypes) {
		mContext = context;
		mMaxViewTypes = maxViewTypes;
	}

	@Override
	public int getCount() {
		return mActiveObjects.size();
	}

	@Override
	public Object getItem(final int position) {
		return mActiveObjects.get(position);
	}

	@Override
	public long getItemId(final int position) {
		final Object object = mActiveObjects.get(position);
		if (object != null && object instanceof ItemIdProvider) {
			return ((ItemIdProvider) object).getItemId();
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return mMaxViewTypes;
	}

	public int getActualViewTypeCount() {
		return mTypes.size();
	}

	private void addType(final Class<? extends Object> clss) {
		if (!mTypes.contains(clss)) {
			mTypes.add(clss);
		}
		if (mTypes.size() > mMaxViewTypes) {
			throw new RuntimeException("Max view types limit reached.");
		}
	}

	@Override
	public int getItemViewType(final int position) {
		final Object object = getItem(position);
		if (object == null) return IGNORE_ITEM_VIEW_TYPE;
		return mTypes.indexOf(object.getClass());
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	@Override
	public abstract View getDropDownView(int position, View convertView, ViewGroup parent);

	public interface ItemIdProvider {
		long getItemId();
	}

	public void add(final Object object) {
		synchronized (mLock) {
			mObjects.add(object);
			mActiveObjects.add(object);
			addType(object.getClass());
		}
		maybeNotify();
	}

	private void unsafeInsert(final Object object, final int index) {
		mObjects.add(index, object);
		addType(object.getClass());
		refreshHidden();
	}

	public void insert(final Object object, final int index) {
		synchronized (mLock) {
			unsafeInsert(object, index);
		}
		maybeNotify();
	}

	public void insertBefore(final Object object, final Object beforeObject) {
		synchronized (mLock) {
			final int index = mObjects.indexOf(beforeObject);
			if (index == -1) return;
			unsafeInsert(object, index);
		}
		maybeNotify();
	}

	public void insertAfter(final Object object, final Object afterObject) {
		synchronized (mLock) {
			int index = mObjects.indexOf(afterObject);
			if (index == -1) return;

			index += 1;

			if (index > mObjects.size()) index = mObjects.size();

			unsafeInsert(object, index);
		}
		maybeNotify();
	}

	public void remove(final Object object) {
		synchronized (mLock) {
			mObjects.remove(object);
			mActiveObjects.remove(object);
			mHiddenObjects.remove(object);
		}
		maybeNotify();
	}

	public void replace(final Object oldObject, final Object newObject) {
		synchronized (mLock) {
			replaceObject(oldObject, newObject, mObjects);
			replaceObject(oldObject, newObject, mActiveObjects);
			replaceObject(oldObject, newObject, mHiddenObjects);
			addType(newObject.getClass());
		}
		maybeNotify();
	}

	private void replaceObject(final Object oldObject, final Object newObject, final List<Object> list) {
		final int index = list.indexOf(oldObject);
		if (index == -1) return;
		list.set(index, newObject);
	}

	public void clear() {
		synchronized (mLock) {
			mObjects.clear();
			mActiveObjects.clear();
			mHiddenObjects.clear();
		}
		maybeNotify();
	}

	public void hide(final Object object) {
		synchronized (mLock) {
			mHiddenObjects.add(object);
			mActiveObjects.remove(object);
		}
		maybeNotify();
	}

	public void show(final Object object) {
		synchronized (mLock) {
			mHiddenObjects.remove(object);
			refreshHidden();
		}
		maybeNotify();
	}

	public void showAll() {
		synchronized (mLock) {
			mHiddenObjects.clear();
			refreshHidden();
		}
		maybeNotify();
	}

	private void refreshHidden() {
		mActiveObjects.clear();
		for (final Object object : mObjects) {
			if (!mHiddenObjects.contains(object)) {
				mActiveObjects.add(object);
			}
		}
	}

	private void maybeNotify() {
		if (mNotify) {
			notifyDataSetChanged();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		setNotifyOnChange(true);
	}

	public void setNotifyOnChange(final boolean notify) {
		mNotify = notify;
	}

	public void sort(final Comparator<? super Object> comparator) {
		Collections.sort(mObjects, comparator);
		Collections.sort(mActiveObjects, comparator);
		maybeNotify();
	}

	@Override
	public boolean isEmpty() {
		return mActiveObjects.isEmpty();
	}

	@Override
	public boolean isEnabled(final int position) {
		final Object object = getItem(position);
		if (object != null && object instanceof SupportEnable) {
			return ((SupportEnable) object).isEnabled();
		}
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		for (int i = 0; i < getCount(); i++) {
			if (!isEnabled(i)) {
				return false;
			}
		}
		return super.areAllItemsEnabled();
	}

	public interface SupportEnable {
		public boolean isEnabled();
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

	public Context getContext() {
		return mContext;
	}
	
	public LayoutInflater getLayoutInflater() {
		return (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public abstract static class DisabledItem implements SupportEnable {
		@Override
		public boolean isEnabled() {
			return false;
		}
	}
}