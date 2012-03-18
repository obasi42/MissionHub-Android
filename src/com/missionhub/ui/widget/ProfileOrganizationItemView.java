package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.missionhub.ui.widget.item.ProfileOrganizationItem;

public class ProfileOrganizationItemView extends TextView implements ItemView {

	public ProfileOrganizationItemView(final Context context) {
		this(context, null);
	}

	public ProfileOrganizationItemView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProfileOrganizationItemView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void prepareItemView() {}

	@Override
	public void setObject(final Item object) {
		final ProfileOrganizationItem item = (ProfileOrganizationItem) object;
		setText(item.text);

		this.setPadding(item.level * 20, 0, 0, 0);

	}

	@Override
	public Class<? extends Item> getItemClass() {
		return ProfileOrganizationItem.class;
	}
}