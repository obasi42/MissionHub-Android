package com.missionhub.android.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.android.ui.NavigationMenu;

public class NavigationItem extends SpinnerItem {

	private final NavigationMenu mNavigationMenu;

	private int mId = -1;
	private CharSequence mTitle;
	private CharSequence mSubtitle;
	private Drawable mIcon;

	public NavigationItem(final int id, final NavigationMenu navigationMenu) {
		mId = id;
		mNavigationMenu = navigationMenu;
	}

	public NavigationItem(final NavigationMenu navigationMenu) {
		this(-1, navigationMenu);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		if (mNavigationMenu.isFragmentMenu()) {
			return createCellFromXml(context, R.layout.widget_fragment_navigation_item, parent);
		}
		return createCellFromXml(context, R.layout.widget_navigation_item, parent);
	}

	@Override
	public ItemView newDropdownView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_navigation_dropdown_item, parent);
	}

	private void notifyChanged() {
		if (mNavigationMenu != null) {
			mNavigationMenu.onSpinnerItemChanged(this);
		}
	}

	public int getId() {
		return mId;
	}

	public NavigationItem setTitle(final CharSequence title) {
		mTitle = title;
		notifyChanged();
		return this;
	}

	public NavigationItem setTitle(final int title) {
		setTitle(mNavigationMenu.getContext().getString(title));
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
		setSubtitle(mNavigationMenu.getContext().getString(subtitle));
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
		setIcon(mNavigationMenu.getContext().getResources().getDrawable(iconRes));
		return this;
	}

	public Drawable getIcon() {
		return mIcon;
	}
}