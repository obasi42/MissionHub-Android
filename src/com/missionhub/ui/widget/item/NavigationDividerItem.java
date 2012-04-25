package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.ListItemAdapter.DisabledItem;
import com.missionhub.ui.NavigationMenu;

public class NavigationDividerItem extends SpinnerItem implements DisabledItem {

	private final boolean mIsSidebar;
	private final Context mContext;
	private final int mId;
	private CharSequence mTitle;
	private CharSequence mSubtitle;
	private final OnSpinnerItemChangedListener mItemChangedListener;

	public NavigationDividerItem(final int id, final Context context, final NavigationMenu navigationMenu, boolean isSidebar) {
		mId = id;
		mContext = context;
		mItemChangedListener = navigationMenu;
		mIsSidebar = isSidebar;
	}

	@Override
	public ItemView newDropdownView(final Context context, final ViewGroup parent) {
		return newView(context, parent);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		if (mIsSidebar) {
			return createCellFromXml(context, R.layout.widget_side_navigation_divider, parent);
		}
		return createCellFromXml(context, R.layout.widget_navigation_divider, parent);
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

	public NavigationDividerItem setSubtitle(final CharSequence subtitle) {
		mSubtitle = subtitle;
		notifyChanged();
		return this;
	}

	public NavigationDividerItem setSubtitle(final int subtitle) {
		setSubtitle(mContext.getString(subtitle));
		return this;
	}

	public CharSequence getSubtitle() {
		return mSubtitle;
	}
}