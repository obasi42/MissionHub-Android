/*
 * Copyright (C) 2013 Chris Roemmich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.missionhub.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.View;

public class LockableViewPager extends ViewPager {

    public static final int LOCK_NONE = 0;
    public static final int LOCK_BOTH = 1;
    public static final int LOCK_BACKWARD = 2;
    public static final int LOCK_FORWARD = 3;

    private int mLock = LOCK_NONE;

    private float mLastX;
    private boolean mLocked;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLock != LOCK_NONE && ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (mLastX < ev.getX() && (mLock == LOCK_BOTH || mLock == LOCK_BACKWARD)) {
                mLocked = true;
            } else if (mLastX > ev.getX() && (mLock == LOCK_BOTH || mLock == LOCK_FORWARD)) {
                mLocked = true;
            }
        } else {
            mLocked = false;
        }
        mLastX = ev.getX();

        if (mLocked) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (currentFocused == this) currentFocused = null;

        boolean locked = false;

        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
        if (nextFocused != null && nextFocused != currentFocused) {
            if (direction == View.FOCUS_LEFT) {
                if (currentFocused != null && nextFocused.getLeft() >= currentFocused.getLeft()) {
                    if (mLock == LOCK_BOTH || mLock == LOCK_BACKWARD) {
                        locked = true;
                    }
                }
            } else if (direction == View.FOCUS_RIGHT) {
                if (currentFocused != null && nextFocused.getLeft() <= currentFocused.getLeft()) {
                    if (mLock == LOCK_BOTH || mLock == LOCK_FORWARD) {
                        locked = true;
                    }
                }
            }
        } else if (direction == FOCUS_LEFT || direction == FOCUS_BACKWARD) {
            if (mLock == LOCK_BOTH || mLock == LOCK_BACKWARD) {
                locked = true;
            }
        } else if (direction == FOCUS_RIGHT || direction == FOCUS_FORWARD) {
            if (mLock == LOCK_BOTH || mLock == LOCK_FORWARD) {
                locked = true;
            }
        }

        if (locked) {
            return true;
        } else {
            return super.arrowScroll(direction);
        }
    }

    public void setPagingLocked(final int lock) {
        mLock = lock;
    }

    public int getPagingLocked() {
        return mLock;
    }

    public boolean pageForward(boolean smoothScroll) {
        if (mLock == LOCK_BOTH || mLock == LOCK_FORWARD) return false;

        if (getAdapter() != null && getCurrentItem() < (getAdapter().getCount() - 1)) {
            setCurrentItem(getCurrentItem() + 1, smoothScroll);
            return true;
        }
        return false;
    }

    public boolean pageBackward(boolean smoothScroll) {
        if (mLock == LOCK_BOTH || mLock == LOCK_BACKWARD) return false;

        if (getCurrentItem() > 0) {
            setCurrentItem(getCurrentItem() - 1, smoothScroll);
            return true;
        }
        return false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mLock", mLock);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mLock = bundle.getInt("mLock");
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
