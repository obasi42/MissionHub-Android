package com.missionhub.ui;

import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.missionhub.R;

import org.holoeverywhere.LayoutInflater;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProgressItemHelper {

    final private Set<Object> mTasks = Collections.synchronizedSet(new HashSet<Object>());
    private Animation mAnimation;
    private MenuItem mRefreshItem;
    private ImageView mRefreshView;

    public ProgressItemHelper() {
    }

    public void onCreateOptionsMenu(final Menu menu) {
        mRefreshItem = menu.add(Menu.NONE, R.id.action_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh);
        MenuItemCompat.setShowAsAction(mRefreshItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM | MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
        update();
    }

    public void onDestroyView() {
        mRefreshView = null;
    }

    public void onCreateView(final LayoutInflater inflater) {
        mAnimation = AnimationUtils.loadAnimation(inflater.getContext(), R.anim.clockwise_refresh);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mRefreshView = (ImageView) inflater.inflate(R.layout.refresh_icon, null);
        update();
    }

    private synchronized void update() {
        if (mRefreshItem == null || mRefreshView == null || mAnimation == null) return;

        if (!mTasks.isEmpty()) {
            mRefreshView.startAnimation(mAnimation);
            MenuItemCompat.setActionView(mRefreshItem, mRefreshView);
        } else {
            mRefreshView.clearAnimation();
            MenuItemCompat.setActionView(mRefreshItem, null);
        }
    }

    public synchronized void addProgress(final Object task) {
        mTasks.add(task);
        update();
    }

    public synchronized void removeProgress(final Object task) {
        mTasks.remove(task);
        update();
    }

    public synchronized void clearProgress() {
        mTasks.clear();
        update();
    }

    public synchronized boolean hasProgress(final Object task) {
        return mTasks.contains(task);
    }
}