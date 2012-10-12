package com.missionhub.ui.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.ui.item.Item;
import com.missionhub.ui.item.SideMenuItem;
import com.missionhub.util.U;

/**
 * Views for use in side menu
 */
public class SideMenuItemView extends RelativeLayout implements ItemView {

	ImageView mIcon;
	TextView mText;
	ImageView mCountIcon;

	public SideMenuItemView(final Context context) {
		this(context, null);
	}

	public SideMenuItemView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SideMenuItemView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void prepareItemView() {
		mIcon = (ImageView) findViewById(R.id.icon);
		mText = (TextView) findViewById(R.id.text);
		mCountIcon = (ImageView) findViewById(R.id.count_icon);
	}

	@Override
	public void setObject(final Item item, final ViewGroup parent, final int position) {
		final SideMenuItem menuitem = (SideMenuItem) item;
		if (menuitem.iconResource != -1) {
			mIcon.setImageResource(menuitem.iconResource);
		} else {
			mIcon.setVisibility(View.INVISIBLE);
		}

		if (!U.isNullEmpty(menuitem.text)) {
			mText.setText(menuitem.text);
			mText.setVisibility(View.VISIBLE);
		} else {
			mText.setVisibility(View.INVISIBLE);
		}

		// TODO: add count support
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return SideMenuItem.class;
	}

}