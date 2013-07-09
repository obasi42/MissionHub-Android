package com.missionhub.ui.drilldown;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;

import com.missionhub.R;
import com.missionhub.application.Application;

import org.holoeverywhere.widget.ListAdapterWrapper;

import java.lang.ref.WeakReference;
import java.util.*;

public class DrillDownAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    public static final String TAG = DrillDownAdapter.class.getSimpleName();

    private Context mContext;
    private WeakReference<DrillDownView> mDrillDownView;
    private List<DrillDownItem> mRootItems = new ArrayList<DrillDownItem>();
    private List<View> mViews = new ArrayList<View>();

    private boolean mEnablePools = true;
    private List<View> mViewPool = new ArrayList<View>();
    private List<DrillDownListAdapter> mAdapterPool = new ArrayList<DrillDownListAdapter>();

    private boolean mNotify = true;
    private final Object mLock = new Object();

    private boolean mPageChanged;
    private int mPageSelected;

    private DrillDownItem mCurrentItem;

    public DrillDownAdapter(Context context) {
        setContext(context);
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        if (context != mContext) {
            mContext = context;
            synchronized (mLock) {
                mViews.clear();
                mViewPool.clear();
            }
            rebuildAdapter();
        }
    }

    protected void setDrillDownView(DrillDownView view) {
        mDrillDownView = new WeakReference<DrillDownView>(view);
    }

    protected void onDrillDownViewCreated(DrillDownView view) {
        view.setCurrentItem(mPageSelected, false);
    }

    protected DrillDownView getDrillDownView() {
        if (mDrillDownView == null) return null;
        return mDrillDownView.get();
    }

    private void rebuildAdapter() {
        synchronized (mLock) {
            mViews.clear();

            // set up the root view
            mViews.add(setupPage(0, null));

            // determine the current items
            List<DrillDownItem> reverseItems = new ArrayList<DrillDownItem>();
            DrillDownItem current = mCurrentItem;
            while (current != null) {
                reverseItems.add(current);
                current = current.getParent();
            }
            Collections.reverse(reverseItems);

            for (int i = 0; i < reverseItems.size(); i++) {
                mViews.add(setupPage(i + 1, reverseItems.get(i)));
            }
        }
    }

    private View setupPage(int depth, DrillDownItem item) {
        View page = getOrCreatePageView(depth, item);
        ListView list = getListView(page);
        DrillDownListAdapter adapter = getOrCreateListAdapter(depth);

        adapter.setNotifyOnChange(false);
        adapter.clear();
        if (item == null) {
            adapter.addItems(mRootItems);
        } else {
            adapter.addItems(item.getChildren());
        }
        list.setAdapter(adapter);

        return page;
    }

    public void setCurrentItem(DrillDownItem item) {
        if (item == mCurrentItem) return;

        synchronized (mLock) {
            mCurrentItem = item;
            maybeNotify();
        }
    }

    private void onItemClicked(DrillDownItem item) {
        if (mDrillDownView != null && mDrillDownView.get() != null) {
            mDrillDownView.get().onItemClicked(this, item);
        }
    }

    private void onNextClicked(DrillDownItem item) {
        if (mDrillDownView != null && mDrillDownView.get() != null) {
            mDrillDownView.get().onNextClicked(this, item);
        }
    }

    private boolean onItemLongClicked(DrillDownItem item) {
        return mDrillDownView != null && mDrillDownView.get() != null && mDrillDownView.get().onItemLongClicked(this, item);
    }

    public DrillDownItem getCurrentItem() {
        synchronized (mLock) {
            return mCurrentItem;
        }
    }

    public View createPageView(int depth, DrillDownItem item, LayoutInflater inflater) {
        return inflater.inflate(R.layout.widget_drill_down, null);
    }

    public void setupPageView(View view) {
        final ListView list = getListView(view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked((DrillDownItem) parent.getItemAtPosition(position));
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return onItemLongClicked((DrillDownItem) parent.getItemAtPosition(position));
            }
        });
    }

    public ListView getListView(View view) {
        ListView list = (ListView) view.findViewById(android.R.id.list);
        if (list == null) {
            throw new RuntimeException(TAG + ": page view must contain a ListView with the id android:id/list");
        }
        return list;
    }

    private View getOrCreatePageView(int depth, DrillDownItem item) {
        View view = null;
        synchronized (mLock) {
            if (mEnablePools && mViewPool.size() > depth) {
                view = mViewPool.get(depth);
            }
            if (view == null) {
                view = createPageView(depth, item, getLayoutInflater());
                setupPageView(view);
                if (mEnablePools) {
                    if (mViewPool.size() <= depth) {
                        mViewPool.add(view);
                    } else {
                        mViewPool.set(depth, view);
                    }
                }
            }
        }
        return view;
    }

    private DrillDownListAdapter getOrCreateListAdapter(int depth) {
        DrillDownListAdapter adapter = null;
        synchronized (mLock) {
            if (mEnablePools && mAdapterPool.size() > depth) {
                adapter = mAdapterPool.get(depth);
            }
            if (adapter == null) {
                adapter = createListAdapter();
                if (mEnablePools) {
                    if (mAdapterPool.size() <= depth) {
                        mAdapterPool.add(adapter);
                    } else {
                        mAdapterPool.set(depth, adapter);
                    }
                }
            }
        }
        return adapter;
    }


    public View createItemView(final DrillDownItem item, View convertView) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.item_drill_down, null);
            holder = new ViewHolder();
            holder.mText1 = (TextView) view.findViewById(android.R.id.text1);
            holder.mDivider = view.findViewById(R.id.divider);
            holder.mNext = view.findViewById(R.id.next);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mText1.setText(item.getText());

        if (item.hasChildren()) {
            if (holder.mDivider != null) {
                holder.mDivider.setVisibility(View.VISIBLE);
            }
            if (holder.mNext != null) {
                holder.mNext.setVisibility(View.VISIBLE);
                holder.mNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNextClicked(item);
                    }
                });
            }
        } else {
            if (holder.mDivider != null) {
                holder.mDivider.setVisibility(View.GONE);
            }
            if (holder.mNext != null) {
                holder.mNext.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private static class ViewHolder {
        public TextView mText1;
        public View mDivider;
        public View mNext;
    }

    public DrillDownListAdapter createListAdapter() {
        return new DrillDownListAdapter(this);
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    public void setEnablePools(boolean enabled) {
        mEnablePools = enabled;
        if (!enabled) {
            clearPools();
        }
    }

    public boolean isPoolsEnabled() {
        return mEnablePools;
    }

    public void clearPools() {
        synchronized (mLock) {
            mViewPool.clear();
            mAdapterPool.clear();
        }
    }

    public void clear() {
        synchronized (mLock) {
            mRootItems.clear();
        }
        maybeNotify();
    }

    public void setNotifyOnChange(boolean notify) {
        mNotify = notify;
    }

    /**
     * Adds a root item to the adapter
     *
     * @param item
     */
    public void addRootItem(DrillDownItem item) {
        synchronized (mLock) {
            item.setAdapter(this);
            mRootItems.add(item);
        }
        maybeNotify();
    }

    /**
     * Adds multiple root items to the adapter
     *
     * @param items
     */
    public void addRootItems(Collection<DrillDownItem> items) {
        synchronized (mLock) {
            for (DrillDownItem item : items) {
                item.setAdapter(this);
            }
            mRootItems.addAll(items);
        }
        maybeNotify();
    }

    /**
     * Removes a root item from the adapter
     *
     * @param item
     */
    public void removeRootItem(DrillDownItem item) {
        synchronized (mLock) {
            item.setAdapter(null);
            mRootItems.remove(item);
        }
        maybeNotify();
    }

    /**
     * Removes multiple root items from the adapter
     *
     * @param items
     */
    public void removeRootItems(Collection<DrillDownItem> items) {
        synchronized (mLock) {
            for (DrillDownItem item : items) {
                item.setAdapter(null);
            }
            mRootItems.removeAll(items);
        }
        maybeNotify();
    }

    private void maybeNotify() {
        if (mNotify) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mNotify = true;
        rebuildAdapter();
        super.notifyDataSetChanged();
        if (getDrillDownView() != null) {
            getDrillDownView().pageToLast(true);
        }
    }

    @Override
    public int getCount() {
        synchronized (mLock) {
            return mViews.size();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        synchronized (mLock) {
            final View view = mViews.get(position);
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
            container.addView(view, 0);
            return view;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object item) {
        synchronized (mLock) {
            final int position = mViews.indexOf(item);
            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPageSelected(int page) {
        synchronized (mLock) {
            if (mPageSelected != page) {
                if (page == 0) {
                    mCurrentItem = null;
                }
                mPageSelected = page;
                mPageChanged = true;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        synchronized (mLock) {
            if (mPageChanged && state == ViewPager.SCROLL_STATE_IDLE) {
                int i = getCount() - 1;
                while (i > mPageSelected) {
                    mViews.remove(i);
                    i--;
                }
                mPageChanged = false;
                super.notifyDataSetChanged();
            }
        }
    }

    public int getPageSelected() {
        return mPageSelected;
    }

}