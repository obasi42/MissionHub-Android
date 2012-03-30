package com.missionhub.ui.widget.item;

import greendroid.widget.item.TextItem;

public class NavigationItem extends TextItem {

	public int mId = -1;
	public Class<?> mActivityClass = null;

	public NavigationItem(final Class<?> c, final String title) {
		super(title);
		mActivityClass = c;
	}

	public NavigationItem(final int id, final String title) {
		super(title);
		mId = id;
	}
}