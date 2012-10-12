package com.missionhub.ui.item;

import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.ui.itemview.ItemView;

public class LayoutItem extends Item {

	public int layout;

	public LayoutItem(final int layout) {
		this.layout = layout;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, layout, parent);
	}
}