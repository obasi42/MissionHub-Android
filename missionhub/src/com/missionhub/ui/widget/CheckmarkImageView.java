package com.missionhub.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.missionhub.R;

public class CheckmarkImageView extends ImageView {

    public static final int STATE_NONE = 0;
    public static final int STATE_SOME = 1;
    public static final int STATE_ALL = 2;

    private int mState = STATE_NONE;

    public CheckmarkImageView(Context context) {
        super(context);
    }

    public CheckmarkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckmarkImageView(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
    }

    public int getCheckmarkState() {
        return mState;
    }

    public void setCheckmarkState(int state) {
        if (mState != state) {
            mState = state;
            refreshDrawableState();
        }
    }

    public boolean isCheckmarkStateNone() {
        return getCheckmarkState() == STATE_NONE;
    }

    public boolean isCheckmarkStateSome() {
        return getCheckmarkState() == STATE_SOME;
    }

    public boolean isCheckmarkStateAll() {
        return getCheckmarkState() == STATE_ALL;
    }

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isCheckmarkStateSome()) {
            mergeDrawableStates(drawableState, new int[]{R.attr.state_checkmark_some});
        } else if (isCheckmarkStateAll()) {
            mergeDrawableStates(drawableState, new int[]{R.attr.state_checkmark_all});
        }
        return drawableState;
    }
}