package com.missionhub.ui.widget;

import android.content.Context;
import android.widget.FrameLayout;

import com.missionhub.R;

public class ContactSurveysView extends FrameLayout {

	public ContactSurveysView(final Context context) {
		super(context);
		setBackgroundResource(R.color.gray);
		setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT));
	}

}