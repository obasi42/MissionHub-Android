package com.missionhub.ui.widget.item;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;

public class NavigationItem extends Item {

	private int mLayout;
	private Context mContext;
	private OnNavigationListener mNavigationListener;
	private int mItemId = -1;
	private CharSequence mTitle;
	private CharSequence mSubtitle;
	private Drawable mIcon;
	private boolean mEnabled;

	public NavigationItem(final int itemId, final Context context, final int layout) {
		if (context instanceof OnNavigationListener) {
			mItemId = itemId;
			mContext = context;
			mNavigationListener = (OnNavigationListener) context;
			mLayout = layout;
		} else {
			throw new RuntimeException("Context must implement OnNavigationListener");
		}
	}

	public NavigationItem(final int itemId, final Context context) {
		this(itemId, context, R.layout.widget_navigation_list_item);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, mLayout, parent);
	}

	public static NavigationItem instantiate(final MenuItem menuItem, final Context context) {
		final NavigationItem item = new NavigationItem(menuItem.getItemId(), context);
		return item.setTitle(menuItem.getTitle()).setIcon(menuItem.getIcon()).setEnabled(menuItem.isEnabled());
	}

	public int getItemId() {
		return mItemId;
	}

	public NavigationItem setTitle(final CharSequence title) {
		mTitle = title;
		return this;
	}

	public NavigationItem setTitle(final int title) {
		mTitle = mContext.getString(title);
		return this;
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	public NavigationItem setSubtitle(final CharSequence subtitle) {
		mSubtitle = subtitle;
		return this;
	}

	public NavigationItem setSubtitle(final int subtitle) {
		mSubtitle = mContext.getString(subtitle);
		return this;
	}

	public CharSequence getSubtitle() {
		return mSubtitle;
	}

	public NavigationItem setIcon(final Drawable icon) {
		mIcon = icon;
		return this;
	}

	public NavigationItem setIcon(final int iconRes) {
		mIcon = mContext.getResources().getDrawable(iconRes);
		return this;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public NavigationItem setEnabled(final boolean enabled) {
		mEnabled = enabled;
		return this;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public OnNavigationListener getOnNavigationListener() {
		return mNavigationListener;
	}
}