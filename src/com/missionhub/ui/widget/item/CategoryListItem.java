package com.missionhub.ui.widget.item;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;

public class CategoryListItem extends Item {

	public String mCategory;

	public CategoryListItem(final String category) {
		super();
		mCategory = category;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.widget_category_list_item, parent);
	}

}