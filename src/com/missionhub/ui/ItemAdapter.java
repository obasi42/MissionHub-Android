/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modified by C.Roemmich 2012 for MissionHub Project
 */
package com.missionhub.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;

import com.missionhub.ui.item.Item;
import com.missionhub.ui.item.LayoutItem;
import com.missionhub.ui.item.SpinnerItem;
import com.missionhub.ui.itemview.ItemView;
import com.missionhub.ui.itemview.LayoutItemView;
import com.missionhub.ui.itemview.SpinnerItemView;
import com.missionhub.ui.widget.SelectableListView;
import com.missionhub.ui.widget.SelectableListView.SupportActivatable;

/**
 * <p>
 * A ListAdapter that acts like an ArrayAdapter. An ItemAdapter manages a ListView that is backed by an array of
 * {@link Item}s. This is more advanced than a simple ArrayAdapter because it handles different types of itemviews
 * internally. Adding, removing items from the internal array is also possible.
 * </p>
 * <p>
 * The ListView can be notified of underlying data changes manually using notifyDataSetChanged or automatically using
 * the {@link #setNotifyOnChange(boolean)} method.
 * </p>
 * <p>
 * Finally, an ItemAdapter can be created via XML code using the {@link #createFromXml(Context, int)} method. This is a
 * very powerful feature when you want to display static data or if you want to pre-populate your ItemAdapter.
 * </p>
 * 
 * @author Cyril Mottier
 */
public class ItemAdapter extends BaseAdapter implements SpinnerAdapter {

	private static final int DEFAULT_MAX_VIEW_TYPE_COUNT = 10;

	private static final int TYPE_NORMAL = 1;
	private static final int TYPE_DROPDOWN = 2;

	private static class TypeInfo {
		int count;
		int type;
	}

	/** the list of all item in the adpter */
	private final List<Item> mAllItems;

	/** the list of items currently displayed in the list */
	private final List<Item> mShownItems = Collections.synchronizedList(new ArrayList<Item>());

	/** the list of hidden item */
	private final List<Item> mHiddenItems = Collections.synchronizedList(new ArrayList<Item>());

	private final HashMap<Class<? extends Item>, TypeInfo> mTypes;
	private final Context mContext;

	private boolean mNotifyOnChange;
	private int mMaxViewTypeCount;

	/**
	 * Constructs an empty ItemAdapter.
	 * 
	 * @param context
	 *            The context associated with this array adapter.
	 */
	public ItemAdapter(final Context context) {
		this(context, new ArrayList<Item>());
	}

	/**
	 * Constructs an ItemAdapter using the specified items.
	 * <p>
	 * <em><strong>Note</strong>: Using this constructor implies the internal array will be
	 * immutable. As a result, adding or removing items will result in an
	 * exception.</em>
	 * </p>
	 * 
	 * @param context
	 *            The context associated with this array adapter.
	 * @param items
	 *            The array of {@link Item}s use as underlying data for this ItemAdapter
	 */
	public ItemAdapter(final Context context, final Item[] items) {
		this(context, Arrays.asList(items), DEFAULT_MAX_VIEW_TYPE_COUNT);
	}

	/**
	 * Constructs an ItemAdapter using the specified items.
	 * <p>
	 * 
	 * @param context
	 *            The context associated with this array adapter.
	 * @param items
	 *            The list of {@link Item}s used as data for this ItemAdapter
	 */
	public ItemAdapter(final Context context, final List<Item> items) {
		this(context, items, DEFAULT_MAX_VIEW_TYPE_COUNT);
	}

	/**
	 * Constructs an ItemAdapter using the specified items.
	 * <p>
	 * <em><strong>Note</strong>: Using this constructor implies the internal array will be
	 * immutable. As a result, adding or removing items will result in an
	 * exception.</em>
	 * </p>
	 * <p>
	 * <em><strong>Note</strong>: A ListAdapter doesn't handle variable view type 
	 * count (even after a notifyDataSetChanged). An ItemAdapter handles several 
	 * types of cell are therefore use a trick to overcome the previous problem.
	 * This trick is to fool the ListView several types exist. If you already
	 * know the number of item types you'll possibly have, simply set it using this method</em>
	 * </p>
	 * 
	 * @param context
	 *            The context associated with this array adapter.
	 * @param items
	 *            The array of {@link Item}s use as underlying data for this ItemAdapter
	 * @param maxViewTypeCount
	 *            The maximum number of view type that may be generated by this ItemAdapter
	 */
	public ItemAdapter(final Context context, final Item[] items, final int maxViewTypeCount) {
		this(context, Arrays.asList(items), maxViewTypeCount);
	}

	/**
	 * Constructs an ItemAdapter using the specified items.
	 * <p>
	 * <em><strong>Note:</strong> A ListAdapter doesn't handle variable view type 
	 * count (even after a notifyDataSetChanged). An ItemAdapter handles several 
	 * types of cell are therefore use a trick to overcome the previous problem.
	 * This trick is to fool the ListView several types exist. If you already
	 * know the number of item types you'll possibly have, simply set it using this method</em>
	 * </p>
	 * 
	 * @param context
	 *            The context associated with this array adapter.
	 * @param items
	 *            The list of Items used as data for this ItemAdapter
	 * @param maxViewTypeCount
	 *            The maximum number of view type that may be generated by this ItemAdapter
	 */
	public ItemAdapter(final Context context, final List<Item> items, final int maxViewTypeCount) {
		mContext = context;
		mAllItems = items;

		mTypes = new HashMap<Class<? extends Item>, TypeInfo>();
		mMaxViewTypeCount = Integer.MAX_VALUE;

		for (final Item item : mAllItems) {
			mShownItems.add(item);
			addItem(item);
		}

		mMaxViewTypeCount = Math.max(1, Math.max(mTypes.size(), maxViewTypeCount));
	}

	private void addItem(final Item item) {
		final Class<? extends Item> klass = item.getClass();
		final TypeInfo info = mTypes.get(klass);

		if (info == null) {
			final int type = mTypes.size();
			if (type >= mMaxViewTypeCount) {
				throw new RuntimeException("This ItemAdapter may handle only " + mMaxViewTypeCount + " different view types.");
			}
			final TypeInfo newInfo = new TypeInfo();
			newInfo.count = 1;
			newInfo.type = type;
			mTypes.put(klass, newInfo);
		} else {
			info.count++;
		}
	}

	private void removeItem(final Item item) {
		final Class<? extends Item> klass = item.getClass();
		final TypeInfo info = mTypes.get(klass);

		if (info != null) {
			info.count--;
			if (info.count == 0) {
				// TODO cyril: Creating a pool to keep all TypeInfo instances
				// could be a great idea in the future.
				mTypes.remove(klass);
			}
		}
	}

	/**
	 * Returns the context associated with this array adapter. The context is used to create Views from the resource
	 * passed to the constructor.
	 * 
	 * @return The Context associated to this ItemAdapter
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * Returns the current number of different views types used in this ItemAdapter. Having a
	 * <em>getCurrentViewTypeCount</em> equal to <em>getViewTypeCount</em> means you won't be able to add a new type of
	 * view in this adapter (The Adapter class doesn't allow variable view type count).
	 * 
	 * @return The current number of different view types
	 */
	public int getActualViewTypeCount() {
		return mTypes.size();
	}

	/**
	 * Adds the specified object at the end of the array.
	 * 
	 * @param item
	 *            The object to add at the end of the array.
	 */
	public void add(final Item item) {
		addItem(item);
		mAllItems.add(item);
		mShownItems.add(item);
		mHiddenItems.remove(item);
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	public void insertBefore(final Item item, final Item beforeItem) {
		addItem(item);
		int index = mAllItems.indexOf(beforeItem);
		if (index > -1) {
			mAllItems.add(index, item);
			index = mShownItems.indexOf(beforeItem);
			if (index > -1) {
				mShownItems.add(index, item);
			}
			mHiddenItems.remove(item);
			if (mNotifyOnChange) {
				notifyDataSetChanged();
			}
		} else {
			add(item);
		}
	}

	public void insertAfter(final Item item, final Item afterItem) {
		addItem(item);
		int index = mAllItems.indexOf(afterItem);
		if (index > -1) {
			mAllItems.add(index + 1, item);
			index = mShownItems.indexOf(afterItem);
			if (index > -1) {
				mShownItems.add(index + 1, item);
			}
			mHiddenItems.remove(item);
			if (mNotifyOnChange) {
				notifyDataSetChanged();
			}
		} else {
			add(item);
		}
	}

	/**
	 * Inserts the specified object at the specified index in the array.
	 * 
	 * @param item
	 *            The object to insert into the array.
	 * @param index
	 *            The index at which the object must be inserted.
	 */
	public void insert(final Item item, final int index) {
		addItem(item);
		mAllItems.add(index, item);
		mShownItems.add(index, item);
		mHiddenItems.remove(item);
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	/**
	 * Removes the specified object from the array.
	 * 
	 * @param item
	 *            The object to remove.
	 */
	public void remove(final Item item) {
		if (mAllItems.remove(item)) {
			mShownItems.remove(item);
			mHiddenItems.remove(item);
			removeItem(item);
			if (mNotifyOnChange) {
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * Remove all elements from the list.
	 */
	public void clear() {
		mAllItems.clear();
		mShownItems.clear();
		mHiddenItems.clear();
		mTypes.clear();
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	/**
	 * Sorts the content of this adapter using the specified comparator.
	 * 
	 * @param comparator
	 *            The comparator used to sort the objects contained in this adapter.
	 */
	public void sort(final Comparator<? super Item> comparator) {
		Collections.sort(mAllItems, comparator);
		Collections.sort(mShownItems, comparator);
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	/**
	 * Control whether methods that change the list ({@link #add}, {@link #insert}, {@link #remove}, {@link #clear})
	 * automatically call notifyDataSetChanged(). If set to false, caller must manually call notifyDataSetChanged() to
	 * have the changes reflected in the attached view. The default is true, and calling notifyDataSetChanged() resets
	 * the flag to true.
	 * 
	 * @param notifyOnChange
	 *            if true, modifications to the list will automatically call notifyDataSetChanged()
	 */
	public void setNotifyOnChange(final boolean notifyOnChange) {
		mNotifyOnChange = notifyOnChange;
	}

	@Override
	public int getCount() {
		return mShownItems.size();
	}

	public int getCountAll() {
		return mAllItems.size();
	}

	@Override
	public Object getItem(final int position) {
		return mShownItems.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public int getItemViewType(final int position) {
		return mTypes.get(getItem(position).getClass()).type;
	}

	@Override
	public boolean isEnabled(final int position) {
		final Item item = (Item) getItem(position);
		if (item instanceof Enableable) {
			return ((Enableable) item).isEnabled();
		}
		return ((Item) getItem(position)).enabled;
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

	public interface Enableable {
		public boolean isEnabled();
	}

	public void hide(final Item item) {
		mHiddenItems.add(item);
		mShownItems.remove(item);
		removeItem(item);
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	public void show(final Item item) {
		if (mHiddenItems.remove(item)) {
			final int index = mAllItems.indexOf(item);
			if (index > -1) {
				addItem(item);
				mShownItems.add(index, item);
				mHiddenItems.remove(item);
			}
		}
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	public void showAll() {
		for (final Item item : mHiddenItems) {
			show(item);
		}
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
	}

	@Override
	public int getViewTypeCount() {
		return mMaxViewTypeCount;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getView(TYPE_NORMAL, position, convertView, parent);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		return getView(TYPE_DROPDOWN, position, convertView, parent);
	}

	/**
	 * A get view implementation that can handle either a spinner item or normal item
	 * 
	 * @param type
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	private View getView(final int type, final int position, final View convertView, final ViewGroup parent) {
		final Item item = (Item) getItem(position);
		ItemView cell = (ItemView) convertView;

		// clear the cell if the layout on the item does not match the cell's layout
		if (item instanceof LayoutItem && cell instanceof LayoutItemView) {
			if (((LayoutItem) item).layout != ((LayoutItemView) cell).getLayoutId()) {
				cell = null;
			}
		}

		// recreate the layout if the cell is null or the class does not match
		if (cell == null || cell.getItemClass() != item.getClass()) {
			if (type == TYPE_DROPDOWN && item instanceof SpinnerItem) {
				cell = ((SpinnerItem) item).newDropDownView(mContext, parent);
				((SpinnerItemView) cell).prepareDropdownItemView();
			} else {
				cell = item.newView(mContext, parent);
				cell.prepareItemView();
			}
		}

		// set the layout id
		if (item instanceof LayoutItem && cell instanceof LayoutItemView) {
			((LayoutItemView) cell).setLayoutId(((LayoutItem) item).layout);
		}

		// handle spinners
		if (type == TYPE_DROPDOWN && item instanceof SpinnerItem) {
			((SpinnerItemView) cell).setDropdownObject((SpinnerItem) item, parent, position);
		} else {
			cell.setObject(item, parent, position);
		}

		final View view = (View) cell;

		// handle support activatable
		if (parent instanceof SelectableListView && view instanceof SupportActivatable) {
			final boolean activated = ((SelectableListView) parent).isItemActivated(position);
			((SupportActivatable) view).setSupportActivated(activated);
		}

		return view;
	}
}