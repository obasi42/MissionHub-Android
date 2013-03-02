package com.missionhub.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LockedViewPager extends ViewPager {

    private boolean mLocked;

    public LockedViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mLocked = true;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!mLocked) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        if (!mLocked) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingLocked(final boolean locked) {
        mLocked = locked;
    }

    public boolean isPagingLocked() {
        return mLocked;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("mLocked", mLocked);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mLocked = bundle.getBoolean("mLocked");
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}