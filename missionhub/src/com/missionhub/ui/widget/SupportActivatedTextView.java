package com.missionhub.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.missionhub.R;

import org.holoeverywhere.widget.TextView;

public class SupportActivatedTextView extends TextView implements SupportActivated {
    private boolean mActivated = false;

    public SupportActivatedTextView(Context context) {
        super(context);
    }

    public SupportActivatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SupportActivatedTextView(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
    }

    @Override
    public boolean isSupportActivated() {
        return mActivated;
    }

    @Override
    public void setSupportActivated(final boolean activated) {
        if (mActivated != activated) {
            mActivated = activated;
            refreshDrawableState();
        }
    }

    @Override
    protected int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isSupportActivated()) {
            mergeDrawableStates(drawableState, new int[]{R.attr.state_support_activated});
        }
        return drawableState;
    }
}