package com.missionhub.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * Adapted from https://github.com/Gnod/ParallaxListView/blob/master/src/com/gnod/parallaxlistview/ParallaxScollListView.java
 */
public class ParallaxListView extends ListView implements OnScrollListener {

    private View mView;
    private int mViewHeight = -1;
    private int mMaxHeight = Integer.MAX_VALUE;

    private float xDistance, yDistance, lastX, lastY;

    public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParallaxListView(Context context) {
        super(context);
        init();
    }

    // set the view bounds once the view is drawn
    public void init() {
        this.post(new Runnable() {
            @Override
            public void run() {
                setViewsBounds();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if (xDistance > yDistance)
                    return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private interface OnOverScrollByListener {
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                    int scrollY, int scrollRangeX, int scrollRangeY,
                                    int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
    }

    private interface OnTouchEventListener {
        public void onTouchEvent(MotionEvent ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {

        boolean isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY, scrollX,
                scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

        return isCollapseAnimation || super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        touchListener.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    public void setParallaxView(View v) {
        mView = v;
    }

    public void setViewsBounds() {
        if (mViewHeight == -1) {
            mViewHeight = mView.getHeight();
        }
    }

    // overrides API >= V9
    private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
        @Override
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                    int scrollY, int scrollRangeX, int scrollRangeY,
                                    int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            if (mView.getHeight() <= mMaxHeight && isTouchEvent) {
                if (deltaY < 0) {
                    if (mView.getHeight() - deltaY / 2 >= mViewHeight) {
                        mView.getLayoutParams().height = mView
                                .getHeight() - deltaY / 2 < mMaxHeight ? mView
                                .getHeight() - deltaY / 2
                                : mMaxHeight;
                        mView.requestLayout();
                    }
                } else {
                    if (mView.getHeight() > mViewHeight) {
                        mView.getLayoutParams().height = mView
                                .getHeight() - deltaY > mViewHeight ? mView
                                .getHeight() - deltaY
                                : mViewHeight;
                        mView.requestLayout();
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private OnTouchEventListener touchListener = new OnTouchEventListener() {
        @Override
        public void onTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (mViewHeight - 1 < mView.getHeight()) {
                    ResetAnimation animation = new ResetAnimation(mView, mViewHeight);
                    animation.setDuration(300);
                    mView.startAnimation(animation);
                }
            }
        }
    };

    public class ResetAnimation extends Animation {
        private int targetHeight;
        private int originalHeight;
        private int extraHeight;
        private View mView;

        protected ResetAnimation(View view, int targetHeight) {
            this.mView = view;
            this.targetHeight = targetHeight;
            originalHeight = view.getHeight();
            extraHeight = this.targetHeight - originalHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight = (int) (targetHeight - extraHeight * (1 - interpolatedTime));
            mView.getLayoutParams().height = newHeight;
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }
    }
}