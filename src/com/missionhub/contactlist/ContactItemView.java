package com.missionhub.contactlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class ContactItemView extends RelativeLayout implements Checkable {

	private boolean mChecked = false;
	
	public ContactItemView(Context context) {
		super(context);
	}
	
	public ContactItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ContactItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(final boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
	}

	@Override
	protected int[] onCreateDrawableState(final int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, new int[] { android.R.attr.state_checked });
		}
		return drawableState;
	}
	
	
}