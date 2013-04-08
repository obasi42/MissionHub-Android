package com.missionhub.ui.drilldown;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.missionhub.R;
import org.holoeverywhere.widget.ListAdapterWrapper;

import java.lang.ref.WeakReference;
import java.util.*;

public class DrillDownAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    public static final String TAG = DrillDownAdapter.class.getSimpleName();

    private Context mContext;
    private WeakReference<DrillDownView> mDrillDownView;
    private List<DrillDownItem> mRootItems = new ArrayList<DrillDownItem>();

    private List<View> mViews = new ArrayList<View>();
    private WeakReference<View> mCachedRootView;
    private WeakHashMap<DrillDownItem, WeakReference<View>> mCachedViews = new WeakHashMap<DrillDownItem, WeakReference<View>>();

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
                mCachedRootView = null;
                mCachedViews.clear();
            }
            rebuildAdapter();
        }
    }

    protected void setDrillDownView(DrillDownView view) {
        mDrillDownView = new WeakReference<DrillDownView>(view);
    }

    protected DrillDownView getDrillDownView() {
        if (mDrillDownView == null) return null;
        return mDrillDownView.get();
    }

    private void rebuildAdapter() {
        synchronized (mLock) {
            mViews.clear();

            // set up the root view
            mViews.add(setupPageForItem(null));

            // determine the current views
            List<View> mReverseViews = new ArrayList<View>();
            DrillDownItem current = mCurrentItem;
            while (current != null) {
                mReverseViews.add(setupPageForItem(current));
                current = current.getParent();
            }
            Collections.reverse(mReverseViews);
            mViews.addAll(mReverseViews);
        }
    }

    private View setupPageForItem(DrillDownItem item) {
        View page = getPageView(item);
        ListView list = getListView(page);
        DrillDownListAdapter adapter = getListAdapter(list);

        if (adapter == null) {
            if (item == null) {
                adapter = createListAdapter(mRootItems);
            } else {
                adapter = createListAdapter(item.getChildren());
            }
        } else {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            if (item == null) {
                adapter.addItems(mRootItems);
            } else {
                adapter.addItems(item.getChildren());
            }
            adapter.notifyDataSetChanged();
        }

        list.setAdapter(adapter);

        return page;
    }

    public void setCurrentItem(DrillDownItem item) {
        if (item == mCurrentItem) return;

        synchronized (mLock) {
            mCurrentItem = item;
            rebuildAdapter();

            if (getDrillDownView() != null) {
                if (item == null) {
                    getDrillDownView().setCurrentItem(0);
                } else {
                    WeakReference<View> view = mCachedViews.get(item);
                    if (view != null && view.get() != null) {
                        int index = mViews.indexOf(view.get());
                        if (index >= 0) {
                            getDrillDownView().setCurrentItem(index);
                        } else {
                            getDrillDownView().setCurrentItem(0);
                        }
                    }
                }
            }
        }
    }

    public void onItemClicked(DrillDownItem item) {
        if (mDrillDownView != null && mDrillDownView.get() != null) {
            mDrillDownView.get().onItemClicked(this, item);
        }
    }

    public void onNextClicked(DrillDownItem item) {
        if (mDrillDownView != null && mDrillDownView.get() != null) {
            mDrillDownView.get().onNextClicked(this, item);
        }
    }

    public DrillDownItem getCurrentItem() {
        synchronized (mLock) {
            return mCurrentItem;
        }
    }

    private View getPageView(DrillDownItem item) {
        View view = null;
        synchronized (mLock) {
            if (item == null) {
                if (mCachedRootView != null) {
                    view = mCachedRootView.get();
                }
                if (view == null) {
                    view = createPageView(null);
                    setupPageView(view);
                    mCachedRootView = new WeakReference<View>(view);
                }
            } else {
                WeakReference<View> weakView = mCachedViews.get(item);
                if (weakView != null) {
                    view = weakView.get();
                }
                if (view == null) {
                    view = createPageView(item);
                    setupPageView(view);
                    mCachedViews.put(item, new WeakReference<View>(view));
                }
            }
        }
        return view;
    }

    public ListView getListView(View view) {
        ListView list = (ListView) view.findViewById(android.R.id.list);

        if (list == null) {
            throw new RuntimeException(TAG + ": page view must contain a ListView with the id android:id/list");
        }

        return list;
    }

    public DrillDownListAdapter getListAdapter(ListView list) {
        final ListAdapter adapter = list.getAdapter();
        if (adapter instanceof ListAdapterWrapper) {
            return (DrillDownListAdapter) ((ListAdapterWrapper) adapter).getWrappedAdapter();
        }
        return (DrillDownListAdapter) adapter;
    }

    public View createPageView(DrillDownItem item) {
        return getLayoutInflater().inflate(R.layout.widget_drill_down, null);
    }

    public void setupPageView(View view) {
        final ListView list = getListView(view);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked((DrillDownItem) adapterView.getItemAtPosition(i));
            }
        });
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

    public DrillDownListAdapter createListAdapter(Collection<DrillDownItem> initialItems) {
        return new DrillDownListAdapter(this, initialItems);
    }

    public LayoutInflater getLayoutInflater() {
        return (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (mPageChanged && state == ViewPager.SCROLL_STATE_IDLE) {
            synchronized (mLock) {
                int i = getCount() - 1;
                while (i > mPageSelected) {
                    mViews.remove(i);
                    i--;
                }
            }
            mPageChanged = false;
            super.notifyDataSetChanged();
        }
    }

    public void setItemChecked(DrillDownItem item, boolean checked) {
        item.setChecked(checked);


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

    public boolean pageBackward(boolean smoothScroll) {
        return getDrillDownView() != null && getDrillDownView().pageBackward(smoothScroll);
    }
}