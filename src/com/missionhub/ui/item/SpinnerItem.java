package com.missionhub.ui.item;

import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.ui.itemview.SpinnerItemView;

public abstract class SpinnerItem extends Item {

	public abstract SpinnerItemView newDropDownView(Context context, ViewGroup parent);
	
}