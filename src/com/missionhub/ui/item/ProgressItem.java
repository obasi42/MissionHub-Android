package com.missionhub.ui.item;

import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.ui.ItemAdapter.Enableable;
import com.missionhub.ui.itemview.ItemView;

public class ProgressItem extends Item implements Enableable {

	public boolean indeterminate = true;
	public int progress = 0;

	public ProgressItem() {}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.item_progress, parent);
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}