package com.missionhub.android.ui.widget.item;

import greendroid.widget.item.TextItem;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;

public class CenteredTextItem extends TextItem {

	/**
	 * @hide
	 */
	public CenteredTextItem() {
		this(null);
	}

	/**
	 * Construct a SeparatorItem made of the given text
	 * 
	 * @param text
	 *            The text for this SeparatorItem
	 */
	public CenteredTextItem(final String text) {
		super(text);
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_centered_text_item, parent);
	}

}