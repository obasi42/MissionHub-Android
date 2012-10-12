package com.missionhub.ui.itemview;

import android.view.ViewGroup;

import com.missionhub.ui.item.SpinnerItem;

public interface SpinnerItemView extends ItemView {

	void prepareDropdownItemView();

	void setDropdownObject(SpinnerItem item, ViewGroup parent, int position);

}