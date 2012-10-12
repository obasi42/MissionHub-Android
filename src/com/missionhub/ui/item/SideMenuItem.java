package com.missionhub.ui.item;

import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.itemview.ItemView;

public class SideMenuItem extends Item {
	
	public int id = -1;
	public String text;
	public int iconResource = -1;

	public SideMenuItem(int id) {
		this(id, null);
	}
	
	public SideMenuItem(int id, String text) {
		this(id, text, -1);
	}

	public SideMenuItem(int id, String text, int iconResource) {
		this.id = id;
		this.text = text;
		this.iconResource = iconResource;
	}

	@Override
	public ItemView newView(Context context, ViewGroup parent) {
		return createCellFromXml(context, R.layout.item_side_menu, parent);
	}
	
}