package com.missionhub.ui.itemview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.missionhub.ui.item.Item;
import com.missionhub.ui.item.SimpleSpinnerItem;
import com.missionhub.ui.item.SpinnerItem;

public class SimpleSpinnerItemView extends CheckedTextView implements SpinnerItemView {

	public SimpleSpinnerItemView(final Context context) {
		this(context, null);
	}

	public SimpleSpinnerItemView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimpleSpinnerItemView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void prepareItemView() {}
	
	@Override
	public void prepareDropdownItemView() {}

	@Override
	public void setObject(Item item, ViewGroup parent, int position) {
		setText(((SimpleSpinnerItem)item).text);
		
		this.setCheckMarkDrawable(null);
	}
	
	@Override
	public void setDropdownObject(SpinnerItem item, ViewGroup parent, int position) {
		setText(((SimpleSpinnerItem)item).text);
		
		this.setCheckMarkDrawable(null);
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return SimpleSpinnerItem.class;
	}	
}