package com.missionhub.ui.item;

import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.itemview.ItemView;

public class SideMenuItem extends Item {

	public int id = -1;
	public String text;
	public int iconResource = -1;

	public SideMenuItem(final int id) {
		this(id, null);
	}

	public SideMenuItem(final int id, final String text) {
		this(id, text, -1);
	}

	public SideMenuItem(final int id, final String text, final int iconResource) {
		this.id = id;
		this.text = text;
		this.iconResource = iconResource;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.item_side_menu, parent);
	}

}