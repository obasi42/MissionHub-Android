package com.missionhub.ui;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewArrayPagerAdapter extends PagerAdapter {

    public final Object mLock = new Object();
    List<View> mViews = Collections.synchronizedList(new ArrayList<View>());
    private boolean mNotifyOnChange = true;

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        synchronized (mLock) {
            final View view = mViews.get(position);
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
            container.addView(view);
            return view;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        synchronized (mLock) {
            return mViews.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object item) {
        synchronized (mLock) {
            return mViews.indexOf(item);
        }
    }

    public void addView(View item) {
        synchronized (mLock) {
            mViews.add(item);
        }
        maybeNotifyDataSetChanged();
    }

    public void removeView(View item) {
        synchronized (mLock) {
            mViews.remove(item);
        }
        maybeNotifyDataSetChanged();
    }

    public void removeView(int position) {
        synchronized (mLock) {
            mViews.remove(position);
        }
        maybeNotifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mViews.clear();
        }
        maybeNotifyDataSetChanged();
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    private void maybeNotifyDataSetChanged() {
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mNotifyOnChange = true;
        super.notifyDataSetChanged();
    }
}