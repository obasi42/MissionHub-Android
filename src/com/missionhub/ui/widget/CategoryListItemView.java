package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.ui.widget.item.CategoryListItem;

public class CategoryListItemView extends LinearLayout implements ItemView {

	private TextView mCategory;

	public CategoryListItemView(final Context context) {
		this(context, null);
	}

	public CategoryListItemView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void prepareItemView() {
		mCategory = (TextView) findViewById(R.id.text);
	}

	@Override
	public void setObject(final Item object) {
		final CategoryListItem item = (CategoryListItem) object;

		mCategory.setText(item.mCategory);
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return CategoryListItem.class;
	}

}