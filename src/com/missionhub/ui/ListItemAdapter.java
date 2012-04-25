package com.missionhub.ui;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.missionhub.ui.widget.ListItemView;
import com.missionhub.ui.widget.SelectableListView;
import com.missionhub.ui.widget.SelectableListView.SupportActivatable;

public class ListItemAdapter extends ItemAdapter {

	final private Context mContext;

	/** all of the adapter items */
	private final List<Item> mItems = Collections.synchronizedList(new ArrayList<Item>());

	/** hidden adapter items */
	private final List<Item> mHiddenItems = Collections.synchronizedList(new ArrayList<Item>());

	public ListItemAdapter(final Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final Item item = (Item) getItem(position);
		ListItemView cell = (ListItemView) convertView;

		if ((cell == null || cell.getItemClass() != item.getClass()) || (cell != null && parent != null && ((View) cell).getWidth() != parent.getWidth())) {
			cell = (ListItemView) item.newView(mContext, null);
			cell.prepareItemView();
		}

		cell.setObject(item, parent, position);

		final View view = (View) cell;

		if (parent instanceof SelectableListView && view instanceof SupportActivatable) {
			final boolean activated = ((SelectableListView) parent).isItemActivated(position);
			((SupportActivatable) view).setSupportActivated(activated);
		}

		return view;
	}

	@Override
	public boolean isEnabled(final int position) {
		if (getItem(position) instanceof DisabledItem) {
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public boolean areAllItemsEnabled() {
		for (int i=0; i < getCount(); i++) {
			if (getItem(i) instanceof DisabledItem) {
				return false;
			}
		}
		return super.areAllItemsEnabled();
	}

	public interface DisabledItem {}

	public synchronized void hide(final Item item, final boolean notify) {
		setNotifyOnChange(notify);
		mHiddenItems.add(item);
		super.remove(item);
		setNotifyOnChange(true);
	}

	public synchronized void show(final Item item, final boolean notify) {
		setNotifyOnChange(notify);
		if (mHiddenItems.remove(item)) {
			final int index = mItems.indexOf(item);
			if (index > -1) {
				super.insert(item, mItems.indexOf(item));
			}
		}
		setNotifyOnChange(true);
	}

	public synchronized void showAll() {
		for (final Item item : mHiddenItems) {
			show(item, false);
		}
		mHiddenItems.clear();
		notifyDataSetChanged();
	}

	@Override
	public synchronized void add(final Item item) {
		super.add(item);
		mItems.add(item);
		mHiddenItems.remove(item);
	}

	@Override
	public synchronized void insert(final Item item, final int index) {
		super.insert(item, index);
		mItems.add(index, item);
		mHiddenItems.remove(item);
	}

	@Override
	public synchronized void remove(final Item item) {
		super.remove(item);
		mItems.remove(item);
		mHiddenItems.remove(item);
	}

	@Override
	public synchronized void clear() {
		super.clear();
		mItems.clear();
		mHiddenItems.clear();
	}
}