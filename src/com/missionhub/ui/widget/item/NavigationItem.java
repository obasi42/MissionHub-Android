package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.missionhub.MissionHubBaseActivity;
import com.missionhub.R;
import com.missionhub.ui.NavigationMenu;

public class NavigationItem extends SpinnerItem {

	private final boolean mIsSidebar;
	private final Context mContext;
	private final OnSpinnerItemChangedListener mItemChangedListener;
	private final OnNavigationListener mNavigationListener;
	private int mItemId = -1;
	private CharSequence mTitle;
	private CharSequence mSubtitle;
	private Drawable mIcon;
	private boolean mEnabled = true;

	public NavigationItem(final int itemId, final Context context, final NavigationMenu navigationMenu, final boolean isSidebar) {
		mItemId = itemId;
		mContext = context;
		mItemChangedListener = navigationMenu;
		mNavigationListener = navigationMenu;
		mIsSidebar = isSidebar;
	}

	public NavigationItem(final MissionHubBaseActivity mActivity, final NavigationMenu navigationMenu) {
		this(-1, mActivity, navigationMenu, false);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		if (mIsSidebar) {
			return createCellFromXml(context, R.layout.widget_side_navigation_item, parent);
		}
		return createCellFromXml(context, R.layout.widget_navigation_item, parent);
	}

	@Override
	public ItemView newDropdownView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_navigation_dropdown_item, parent);
	}

	private void notifyChanged() {
		if (mItemChangedListener != null) {
			mItemChangedListener.onSpinnerItemChanged(this);
		}
	}

	public int getItemId() {
		return mItemId;
	}

	public NavigationItem setTitle(final CharSequence title) {
		mTitle = title;
		notifyChanged();
		return this;
	}

	public NavigationItem setTitle(final int title) {
		setTitle(mContext.getString(title));
		return this;
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	public NavigationItem setSubtitle(final CharSequence subtitle) {
		mSubtitle = subtitle;
		notifyChanged();
		return this;
	}

	public NavigationItem setSubtitle(final int subtitle) {
		setSubtitle(mContext.getString(subtitle));
		return this;
	}

	public CharSequence getSubtitle() {
		return mSubtitle;
	}

	public NavigationItem setIcon(final Drawable icon) {
		mIcon = icon;
		notifyChanged();
		return this;
	}

	public NavigationItem setIcon(final int iconRes) {
		setIcon(mContext.getResources().getDrawable(iconRes));
		return this;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public NavigationItem setEnabled(final boolean enabled) {
		mEnabled = enabled;
		notifyChanged();
		return this;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public OnNavigationListener getOnNavigationListener() {
		return mNavigationListener;
	}
}