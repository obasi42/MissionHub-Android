package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.ui.widget.item.NavigationDividerItem;

public class NavigationDividerView extends LinearLayout implements ListItemView {

	private TextView mTitle;
	private TextView mSubtitle;

	public NavigationDividerView(final Context context) {
		this(context, null);
	}

	public NavigationDividerView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return NavigationDividerItem.class;
	}

	@Override
	public void prepareItemView() {
		mTitle = (TextView) findViewById(R.id.title);
		mSubtitle = (TextView) findViewById(R.id.subtitle);
	}

	@Override
	public void setObject(final Item item) {
		setObject(item, null, -1);
	}

	@Override
	public void setObject(final Item item, final ViewGroup parent, final int position) {
		final NavigationDividerItem navItem = (NavigationDividerItem) item;

		if (mTitle != null && navItem.getTitle() != null) {
			mTitle.setText(navItem.getTitle());
			mTitle.setVisibility(View.VISIBLE);
		} else if (mTitle != null) {
			mTitle.setVisibility(View.GONE);
		}

		if (mSubtitle != null && navItem.getSubtitle() != null) {
			mSubtitle.setText(navItem.getSubtitle());
			mSubtitle.setVisibility(View.VISIBLE);
		} else if (mSubtitle != null) {
			mSubtitle.setVisibility(View.GONE);
		}
	}
}