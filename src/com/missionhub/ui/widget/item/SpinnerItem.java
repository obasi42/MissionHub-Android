package com.missionhub.ui.widget.item;

import android.content.Context;
import android.view.ViewGroup;
import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;

public abstract class SpinnerItem extends Item {
	
	public abstract ItemView newDropdownView(final Context context, final ViewGroup parent);
	
	public interface OnSpinnerItemChangedListener {
		public void onSpinnerItemChanged(SpinnerItem item);
	}
	
}