package com.missionhub.ui;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

public class ViewArrayPagerAdapter extends PagerAdapter {

    public final Object mLock = new Object();
    private List<View> mViews = new ArrayList<View>();
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
            final int position = mViews.indexOf(item);
            if (position >= 0) {
                return position;
            }
            return POSITION_NONE;
        }
    }

    public void addView(View item) {
        synchronized (mLock) {
            mViews.add(item);
        }
        maybeNotifyDataSetChanged();
    }

    public void setView(int position, View view) {
        synchronized (mLock) {
            mViews.set(position, view);
        }
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