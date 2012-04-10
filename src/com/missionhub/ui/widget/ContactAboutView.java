package com.missionhub.ui.widget;

import android.content.Context;
import android.widget.FrameLayout;

import com.missionhub.R;

public class ContactAboutView extends FrameLayout {

	public ContactAboutView(final Context context) {
		super(context);
		setBackgroundResource(R.color.green);
		setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT));
	}

}