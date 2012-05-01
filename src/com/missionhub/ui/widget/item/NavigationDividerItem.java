package com.missionhub.ui.widget.item;

import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.ListItemAdapter.Enableable;
import com.missionhub.ui.NavigationMenu;

public class NavigationDividerItem extends SpinnerItem implements Enableable {

	private final NavigationMenu mNavigationMenu;

	private int mId = -1;
	private CharSequence mTitle;
	private CharSequence mSubtitle;

	public NavigationDividerItem(final int id, final NavigationMenu navigationMenu) {
		enabled = false;
		mId = id;
		mNavigationMenu = navigationMenu;
	}

	public NavigationDividerItem(final NavigationMenu navigationMenu) {
		this(-1, navigationMenu);
	}

	@Override
	public ItemView newDropdownView(final Context context, final ViewGroup parent) {
		return newView(context, parent);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		if (mNavigationMenu.isFragmentMenu()) {
			return createCellFromXml(context, R.layout.widget_fragment_navigation_divider, parent);
		}
		return createCellFromXml(context, R.layout.widget_navigation_divider, parent);
	}

	private void notifyChanged() {
		if (mNavigationMenu != null) {
			mNavigationMenu.onSpinnerItemChanged(this);
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
		setTitle(mNavigationMenu.getContext().getString(title));
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
		setSubtitle(mNavigationMenu.getContext().getString(subtitle));
		return this;
	}

	public CharSequence getSubtitle() {
		return mSubtitle;
	}

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}