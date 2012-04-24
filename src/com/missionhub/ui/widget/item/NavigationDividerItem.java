package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.NavigationMenu;

public class NavigationDividerItem extends SpinnerItem {

	private final Context mContext;
	private final int mId;
	private CharSequence mTitle;
	private final OnSpinnerItemChangedListener mItemChangedListener;

	public NavigationDividerItem(final int id, final Context context, final NavigationMenu navigationMenu) {
		mId = id;
		mContext = context;
		mItemChangedListener = navigationMenu;
	}

	@Override
	public ItemView newDropdownView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_navigation_list_divider, parent);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_navigation_list_divider, parent);
	}

	private void notifyChanged() {
		if (mItemChangedListener != null) {
			mItemChangedListener.onSpinnerItemChanged(this);
		}
	}

	public int getId() {
		return mId;
	}

	public NavigationDividerItem setTitle(final CharSequence title) {
		mTitle = title;
		notifyChanged();
		return this;
	}

	public NavigationDividerItem setTitle(final int title) {
		setTitle(mContext.getString(title));
		return this;
	}

	public CharSequence getTitle() {
		return mTitle;
	}
}