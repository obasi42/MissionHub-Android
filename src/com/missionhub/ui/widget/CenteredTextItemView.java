package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.missionhub.ui.widget.item.CenteredTextItem;

public class CenteredTextItemView extends TextView implements ItemView {

	public CenteredTextItemView(final Context context) {
		this(context, null);
	}

	public CenteredTextItemView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CenteredTextItemView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void prepareItemView() {}

	@Override
	public void setObject(final Item object) {
		final CenteredTextItem item = (CenteredTextItem) object;
		setText(item.text);
		setClickable(true);
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return CenteredTextItem.class;
	}
}