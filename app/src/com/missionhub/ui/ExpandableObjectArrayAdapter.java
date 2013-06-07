package com.missionhub.ui;

import android.content.Context;

import com.google.common.collect.ArrayListMultimap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableObjectArrayAdapter extends BaseExpandableListAdapter {

    private final Object mLock = new Object();
    private final List<Class<? extends Object>> mGroupTypes = new ArrayList<Class<? extends Object>>();
    private final List<Class<? extends Object>> mChildTypes = new ArrayList<Class<? extends Object>>();
    private final int mMaxGroupViewTypes;
    private final int mMaxChildViewTypes;
    ArrayListMultimap mItems = ArrayListMultimap.create();
    private ArrayList<Object> mGroups = new ArrayList<Object>();
    private Context mContext;
    private boolean mNotify = true;

    public ExpandableObjectArrayAdapter(final Context context) {
        this(context, 5, 5);
    }

    public ExpandableObjectArrayAdapter(final Context context, final int maxGroupViewTypes, final int maxChildViewTypes) {
        mContext = context;
        mMaxGroupViewTypes = maxGroupViewTypes;
        mMaxChildViewTypes = maxChildViewTypes;
    }

    @Override
    public int getGroupCount() {
        synchronized (mLock) {
            return mGroups.size();
        }
    }

    @Override
    public int getChildrenCount(int i) {
        synchronized (mLock) {
            return mItems.get(mGroups.get(i)).size();
        }
    }

    @Override
    public Object getGroup(int i) {
        synchronized (mLock) {
            return mGroups.get(i);
        }
    }

    @Override
    public Object getChild(int i, int i2) {
        synchronized (mLock) {
            return mItems.get(mGroups.get(i)).get(i2);
        }
    }

    @Override
    public long getGroupId(int i) {
        try {
            synchronized (mLock) {
                final Object object = mGroups.get(i);
                if (object != null && object instanceof ObjectArrayAdapter.ItemIdProvider) {
                    return ((ObjectArrayAdapter.ItemIdProvider) object).getItemId();
                }
            }
        } catch (final Exception e) { /* ignore */}
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        try {
            synchronized (mLock) {
                final Object object = mItems.get(mGroups.get(i)).get(i2);
                if (object != null && object instanceof ObjectArrayAdapter.ItemIdProvider) {
                    return ((ObjectArrayAdapter.ItemIdProvider) object).getItemId();
                }
            }
        } catch (final Exception e) { /* ignore */}
        return 0;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        final Object object = getChild(i, i2);
        if (object != null && object instanceof ObjectArrayAdapter.SupportEnable) {
            return ((ObjectArrayAdapter.SupportEnable) object).isEnabled();
        }
        return true;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        synchronized (mLock) {
            final Object object = getChild(groupPosition, childPosition);
            if (object == null) return 0;
            return mChildTypes.indexOf(object.getClass());
        }
    }

    @Override
    public int getChildTypeCount() {
        return mMaxChildViewTypes;
    }

    @Override
    public int getGroupType(int groupPosition) {
        synchronized (mLock) {
            final Object object = getGroup(groupPosition);
            if (object == null) return 0;
            return mGroupTypes.indexOf(object.getClass());
        }
    }

    @Override
    public int getGroupTypeCount() {
        return mMaxGroupViewTypes;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        setNotifyOnChange(true);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(final Context context) {
        synchronized (mLock) {
            mContext = context;
            notifyDataSetChanged();
        }
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    private void addGroupType(final Class<? extends Object> clss) {
        synchronized (mLock) {
            if (!mGroupTypes.contains(clss)) {
                mGroupTypes.add(clss);
            }
            if (mGroupTypes.size() > mMaxGroupViewTypes) {
                throw new RuntimeException("Max group view types limit reached.");
            }
        }
    }

    private void addChildType(final Class<? extends Object> clss) {
        synchronized (mLock) {
            if (!mChildTypes.contains(clss)) {
                mChildTypes.add(clss);
            }
            if (mChildTypes.size() > mMaxChildViewTypes) {
                throw new RuntimeException("Max child view types limit reached.");
            }
        }
    }

    private void maybeNotify() {
        if (mNotify) {
            notifyDataSetChanged();
        }
    }

    public void setNotifyOnChange(final boolean notify) {
        mNotify = notify;
    }

    public void add(Object group, Object item) {
        synchronized (mLock) {
            if (!mGroups.contains(group)) {
                mGroups.add(group);
            }
            mItems.put(group, item);
        }
        maybeNotify();
    }

    public void remove(Object group) {
        synchronized (mLock) {
            mGroups.remove(group);
            mItems.removeAll(group);
        }
        maybeNotify();
    }

    public void remove(Object group, Object item) {
        synchronized (mLock) {
            mItems.remove(group, item);
        }
        maybeNotify();
    }

    public void clear() {
        synchronized (mLock) {
            mItems.clear();
        }
        maybeNotify();
    }

    public void clear(Object group) {
        synchronized (mLock) {
            mItems.removeAll(group);
        }
        maybeNotify();
    }
}
