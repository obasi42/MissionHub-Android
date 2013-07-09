package com.missionhub.ui.drilldown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DrillDownItem {

    private DrillDownItem mParent;
    private List<DrillDownItem> mChildren = new ArrayList<DrillDownItem>();
    private DrillDownAdapter mAdapter;
    private DrillDownListAdapter mListAdapter;
    private boolean mNotify = true;
    private final Object mLock = new Object();
    private int mId = 0;
    private CharSequence mText1;
    private boolean mEnabled = true;

    /**
     * Creates a new item with the given parent
     *
     * @param parent
     */
    public DrillDownItem(DrillDownItem parent) {
        this(null, parent);
    }

    /**
     * Creates a new item with the given text
     *
     * @param text
     */
    public DrillDownItem(CharSequence text) {
        this(text, null);
    }

    /**
     * Creates a new item with the given text and parent
     *
     * @param text
     * @param parent
     */
    public DrillDownItem(CharSequence text, DrillDownItem parent) {
        mText1 = text;
        mParent = parent;
        if (parent != null) {
            parent.addItem(this);
        }
    }

    protected void setAdapter(DrillDownAdapter adapter) {
        synchronized (mLock) {
            mAdapter = adapter;
            for (DrillDownItem item : mChildren) {
                item.setAdapter(adapter);
            }
        }
    }

    protected void setListAdapter(DrillDownListAdapter adapter) {
        synchronized (mLock) {
            mListAdapter = adapter;
        }
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    /**
     * Returns the text of the item
     *
     * @return
     */
    public CharSequence getText() {
        return mText1;
    }

    /**
     * Sets the text of the item
     *
     * @param text
     */
    public void setText(CharSequence text) {
        mText1 = text;
        maybeNotifyDataChanged();
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Adds a child item
     *
     * @param item
     */
    public void addItem(DrillDownItem item) {
        synchronized (mLock) {
            setupItem(item);
            mChildren.add(item);
        }
        maybeNotifyChildrenChanged();
    }

    /**
     * Adds multiple children items
     *
     * @param items
     */
    public void addItems(Collection<DrillDownItem> items) {
        synchronized (mLock) {
            for (DrillDownItem item : items) {
                setupItem(item);
            }
            mChildren.addAll(items);
        }
        maybeNotifyChildrenChanged();
    }

    private void setupItem(DrillDownItem item) {
        item.mParent = this;
        item.mAdapter = mAdapter;
    }

    /**
     * Removes a child item
     *
     * @param item
     */
    public void removeItem(DrillDownItem item) {
        synchronized (mLock) {
            tearDownItem(item);
            mChildren.remove(item);
        }
        maybeNotifyChildrenChanged();
    }

    /**
     * Removes multiple child item
     *
     * @param items
     */
    public void removeItems(Collection<DrillDownItem> items) {
        synchronized (mLock) {
            for (DrillDownItem item : items) {
                tearDownItem(item);
            }
            mChildren.removeAll(items);
        }
        maybeNotifyChildrenChanged();
    }

    private void tearDownItem(DrillDownItem item) {
        if (item.mAdapter != null && item.mAdapter.getCurrentItem() == item) {
            item.mAdapter.setCurrentItem(null);
        }
        item.mParent = null;
        item.mAdapter = null;
    }

    /**
     * Clears all children items
     */
    private void clearItems() {
        synchronized (mLock) {
            removeItems(mChildren);
        }
        maybeNotifyChildrenChanged();
    }

    /**
     * Returns the parent item
     *
     * @return
     */
    public DrillDownItem getParent() {
        return mParent;
    }

    /**
     * Returns all of the parents of the item
     *
     * @return
     */
    public List<DrillDownItem> getParents() {
        List<DrillDownItem> parents = new ArrayList<DrillDownItem>();

        DrillDownItem current = getParent();
        while (current != null) {
            parents.add(current);
            current = current.getParent();
        }

        return parents;
    }

    /**
     * Returns the direct children of the item
     *
     * @return
     */
    public List<DrillDownItem> getChildren() {
        synchronized (mLock) {
            return new ArrayList(mChildren);
        }
    }

    /**
     * Returns true when the item has children
     *
     * @return
     */
    public boolean hasChildren() {
        return !mChildren.isEmpty();
    }

    /**
     * Returns true when the item is a root (top-level) item
     *
     * @return
     */
    public boolean isRoot() {
        return mParent == null;
    }

    /**
     * Notify the adapter that menu item data has changed. Use {@link #notifyChildrenChanged()} if changes to
     * children have been added or removed.
     */
    public void notifyDataChanged() {
        setNotifyOnChange(true);
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        } else {
            notifyChildrenChanged();
        }
    }

    /**
     * Notify the adapter that children have been added or removed
     */
    public void notifyChildrenChanged() {
        setNotifyOnChange(true);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void maybeNotifyDataChanged() {
        if (mNotify) {
            notifyDataChanged();
        }
    }

    private void maybeNotifyChildrenChanged() {
        if (mNotify) {
            notifyChildrenChanged();
        }
    }

    /**
     * Sets whether or not changes to children and data should automatically notify the adapter. default=true
     *
     * @param notify
     */
    public void setNotifyOnChange(final boolean notify) {
        mNotify = notify;
    }
}