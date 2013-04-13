package com.missionhub.ui.drilldown;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DrillDownListAdapter extends BaseAdapter {

    private final List<Class<? extends Object>> mTypes = new ArrayList<Class<? extends Object>>();
    private final int mMaxViewTypes;
    private List<DrillDownItem> mItems = new ArrayList<DrillDownItem>();
    private DrillDownAdapter mAdapter;
    private final Object mLock = new Object();
    private boolean mNotify = true;

    public DrillDownListAdapter(DrillDownAdapter adapter) {
        this(adapter, 3);
    }

    public DrillDownListAdapter(DrillDownAdapter adapter, int maxViewTypes) {
        setAdapter(adapter);
        mMaxViewTypes = maxViewTypes;
    }

    public DrillDownListAdapter(DrillDownAdapter adapter, Collection<DrillDownItem> items) {
        if (items != null) {
            for (DrillDownItem item : items) {
                item.setListAdapter(this);
                addType(item.getClass(), false);
            }
            mItems.addAll(items);
        }
        setAdapter(adapter);
        if (mTypes.size() > 3) {
            mMaxViewTypes = mTypes.size();
        } else {
            mMaxViewTypes = 3;
        }
    }

    public void setAdapter(DrillDownAdapter adapter) {
        mAdapter = adapter;
        notifyDataSetChanged();
    }

    public void setNotifyOnChange(final boolean notify) {
        mNotify = notify;
    }

    private void maybeNotify() {
        if (mNotify) {
            notifyDataSetChanged();
        }
    }

    public void addItem(DrillDownItem item) {
        synchronized (mLock) {
            item.setListAdapter(this);
            addType(item.getClass(), true);
            mItems.add(item);
        }
        maybeNotify();
    }

    public void addItems(Collection<DrillDownItem> items) {
        synchronized (mLock) {
            for (DrillDownItem item : items) {
                item.setListAdapter(this);
                addType(item.getClass(), true);
            }
            mItems.addAll(items);
        }
        maybeNotify();
    }

    public void removeItem(DrillDownItem item) {
        synchronized (mLock) {
            item.setListAdapter(null);
            mItems.remove(item);
        }
        maybeNotify();
    }

    public void removeItems(Collection<DrillDownItem> items) {
        synchronized (mLock) {
            for (DrillDownItem item : items) {
                item.setListAdapter(null);
            }
            mItems.removeAll(items);
        }
        maybeNotify();
    }

    public void clear() {
        synchronized (mLock) {
            for (DrillDownItem item : mItems) {
                item.setListAdapter(null);
            }
            mItems.clear();
        }
        maybeNotify();
    }

    private void addType(final Class<? extends Object> clss, boolean enforce) {
        if (!mTypes.contains(clss)) {
            mTypes.add(clss);
        }
        if (enforce) {
            if (mTypes.size() > mMaxViewTypes) {
                throw new RuntimeException("Max view types limit reached.");
            }
        }
    }

    public int indexOf(DrillDownItem item) {
        synchronized (mLock) {
            return mItems.indexOf(item);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        final Object object = getItem(position);
        if (object == null) return IGNORE_ITEM_VIEW_TYPE;
        return mTypes.indexOf(object.getClass());
    }

    @Override
    public int getViewTypeCount() {
        return mMaxViewTypes;
    }

    @Override
    public int getCount() {
        synchronized (mLock) {
            return mItems.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (mLock) {
            return mItems.isEmpty();
        }
    }

    @Override
    public Object getItem(int position) {
        synchronized (mLock) {
            return mItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        synchronized (mLock) {
            return mItems.get(position).getId();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mAdapter.createItemView((DrillDownItem) getItem(position), convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        synchronized (mLock) {
            return mItems.get(position).isEnabled();
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        synchronized (mLock) {
            for (DrillDownItem item : mItems) {
                if (!item.isEnabled()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        setNotifyOnChange(true);
    }
}