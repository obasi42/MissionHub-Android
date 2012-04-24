package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.MissionHubBaseActivity;
import com.missionhub.R;
import com.missionhub.ui.NavigationMenu;

public class NavigationItem extends SpinnerItem {

	private final Context mContext;
	private final OnSpinnerItemChangedListener mItemChangedListener;
	private final OnNavigationListener mNavigationListener;
	private int mItemId = -1;
	private CharSequence mTitle;
	private CharSequence mSubtitle;
	private Drawable mIcon;
	private boolean mEnabled = true;

	public NavigationItem(final int itemId, final Context context, final NavigationMenu navigationMenu, final int layout) {
		mItemId = itemId;
		mContext = context;
		mItemChangedListener = navigationMenu;
		mNavigationListener = navigationMenu;
	}

	public NavigationItem(final MissionHubBaseActivity mActivity, final NavigationMenu navigationMenu, final int layout) {
		this(-1, mActivity, navigationMenu, layout);
		setEnabled(false);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_navigation_list_item, parent);
	}

	@Override
	public ItemView newDropdownView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_navigation_list_item, parent);
	}

	public static NavigationItem instantiate(final MenuItem menuItem, final Context context, final NavigationMenu navigationMenu, final int layout) {
		final NavigationItem item = new NavigationItem(menuItem.getItemId(), context, navigationMenu, layout);
		return item.setTitle(menuItem.getTitle()).setIcon(menuItem.getIcon()).setEnabled(menuItem.isEnabled());
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