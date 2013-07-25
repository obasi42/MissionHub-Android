package com.missionhub.ui;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public class NetworkImageOnScrollListener implements OnScrollListener {

    private final boolean mStopOnScroll;
    private final boolean mStopOnFling;
    private final OnScrollListener mWrappedListener;

    public NetworkImageOnScrollListener(boolean stopOnScroll, boolean stopOnFling) {
        this(stopOnScroll, stopOnFling, null);
    }

    public NetworkImageOnScrollListener(boolean stopOnScroll, boolean stopOnFling, OnScrollListener listener) {
        mStopOnScroll = stopOnScroll;
        mStopOnFling = stopOnFling;
        mWrappedListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (view instanceof ImageLoaderProvider) {
            RequestQueue queue = ((ImageLoaderProvider) view).getVolleyRequestQueue();
            if (queue != null)
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        ((ImageLoaderProvider) view).getVolleyRequestQueue().start();
                        break;
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        if (mStopOnScroll) {
                            ((ImageLoaderProvider) view).getVolleyRequestQueue().stop();
                        }
                        break;
                    case OnScrollListener.SCROLL_STATE_FLING:
                        if (mStopOnFling) {
                            ((ImageLoaderProvider) view).getVolleyRequestQueue().stop();
                        }
                        break;
                }
        }
        if (mWrappedListener != null) {
            mWrappedListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mWrappedListener != null) {
            mWrappedListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public static interface ImageLoaderProvider {
        public ImageLoader getImageLoader();

        public RequestQueue getVolleyRequestQueue();
    }
}
