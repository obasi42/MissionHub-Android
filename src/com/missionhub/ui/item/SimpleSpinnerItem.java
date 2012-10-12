package com.missionhub.ui.item;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.missionhub.R;
import com.missionhub.ui.itemview.ItemView;
import com.missionhub.ui.itemview.SpinnerItemView;

public class SimpleSpinnerItem extends SpinnerItem {

	public String text;
	public boolean showBubble;

	public SimpleSpinnerItem(final String text) {
		this(text, false);
	}

	public SimpleSpinnerItem(final String text, final boolean showBubble) {
		this.text = text;
		this.showBubble = showBubble;
	}

	@Override
	public SpinnerItemView newDropDownView(final Context context, final ViewGroup parent) {
		final ItemView v = createCellFromXml(context, R.layout.item_simple_spinner_dropdown, parent);
		if (!showBubble && v instanceof CheckedTextView) {
			((CheckedTextView) v).setCheckMarkDrawable(null);
		}
		return (SpinnerItemView) v;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.item_simple_spinner, parent);
	}

}