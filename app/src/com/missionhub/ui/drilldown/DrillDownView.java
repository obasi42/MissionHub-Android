package com.missionhub.ui.drilldown;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import com.missionhub.application.Application;
import com.missionhub.ui.widget.LockableViewPager;
import org.holoeverywhere.widget.Toast;

public class DrillDownView extends LockableViewPager {

    public static final String TAG = DrillDownView.class.getSimpleName();

    private OnDrillDownItemClickListener mClickListener;
    private OnDrillDownItemClickListener mNextListener;

    public DrillDownView(Context context) {
        this(context, null);
    }

    public DrillDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public final void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof DrillDownAdapter)) {
            throw new RuntimeException(TAG + ": adapter must be instanceof DrillDownAdapter");
        }
        ((DrillDownAdapter) adapter).setDrillDownView(this);
        adapter.notifyDataSetChanged();
        super.setOnPageChangeListener((OnPageChangeListener) adapter);
        super.setAdapter(adapter);
        ((DrillDownAdapter) adapter).onDrillDownViewCreated(this);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        throw new RuntimeException("Cannot set onPageChangeListener for DrillDownViews");
    }

    @Override
    public DrillDownAdapter getAdapter() {
        return (DrillDownAdapter) super.getAdapter();
    }

    public void onItemClicked(DrillDownAdapter adapter, DrillDownItem item) {
        if (mClickListener != null) {
            mClickListener.onItemClicked(adapter, item);
        } else {
            onNextClicked(adapter, item);
        }
    }

    public void onNextClicked(DrillDownAdapter adapter, DrillDownItem item) {
        if (mNextListener != null) {
            mNextListener.onItemClicked(adapter, item);
        } else {
            adapter.setCurrentItem(item);
        }
    }

    public void setOnDrillDownItemClickListener(OnDrillDownItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnDrillDownItemNextListener(OnDrillDownItemClickListener nextListener) {
        mNextListener = nextListener;
    }

    public static interface OnDrillDownItemClickListener {

        public void onItemClicked(DrillDownAdapter adapter, DrillDownItem item);

    }
}
