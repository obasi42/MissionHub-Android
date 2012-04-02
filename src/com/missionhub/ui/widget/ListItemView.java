package com.missionhub.ui.widget;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.view.ViewGroup;

public interface ListItemView extends ItemView {

	void setObject(Item item, ViewGroup parent, int position);

}