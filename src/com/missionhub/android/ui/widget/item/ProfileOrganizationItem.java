package com.missionhub.android.ui.widget.item;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;

public class ProfileOrganizationItem extends TextItem {

	/** indentation level */
	public int level = 0;

	/** organization id */
	public long organizationId = -1;

	/**
	 * @hide
	 */
	public ProfileOrganizationItem() {
		this(null);
	}

	/**
	 * Construct a SeparatorItem made of the given text
	 * 
	 * @param text
	 *            The text for this SeparatorItem
	 */
	public ProfileOrganizationItem(final String text) {
		super(text);
	}

	public ProfileOrganizationItem(final String text, final Long organizationId, final int level) {
		super(text);
		this.organizationId = organizationId;
		this.level = level;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_profile_organization_item, parent);
	}

}