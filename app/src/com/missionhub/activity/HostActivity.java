package com.missionhub.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.event.DrawerClosedEvent;
import com.missionhub.event.DrawerOpenedEvent;
import com.missionhub.event.OnHostFragmentChangedEvent;
import com.missionhub.fragment.HostedFragment;
import com.missionhub.fragment.HostedPeopleListFragment;
import com.missionhub.fragment.SidebarFragment;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class HostActivity extends BaseAuthenticatedActivity implements FragmentManager.OnBackStackChangedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private HostedFragment mCurrentFragment;
    private SidebarFragment mSidebarFragment;
    private PullToRefreshAttacher mPullToRefreshHelper;

    private int mLeftDrawerId = R.id.left_drawer;
    private boolean mTabletSidebarStatic = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_host);

        // restore state
        if (savedInstanceState != null) {
            mTabletSidebarStatic = savedInstanceState.getBoolean("mTabletSidebarStatic", true);
        }

        // set up the navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.menu_open, R.string.menu_close) {
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                Application.postEvent(new DrawerClosedEvent(drawerView));
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                Application.postEvent(new DrawerOpenedEvent(drawerView));
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        onBackStackChanged();

        // setup the initial fragments
        if (savedInstanceState != null) {
            mCurrentFragment = (HostedFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_main);
            attachSidebar(true);
        } else {
            onEventMainThread(new ChangeHostFragmentEvent(HostedPeopleListFragment.class));
            attachSidebar(false);
        }

        // create the global pull to refresh helper
        mPullToRefreshHelper = new PullToRefreshAttacher(this);

        Application.registerEventSubscriber(this, ChangeHostFragmentEvent.class);
    }

    public void attachSidebar(boolean created) {
        View leftFrame = findViewById(R.id.content_frame_left_drawer);
        if (leftFrame == null || !mTabletSidebarStatic) {
            mLeftDrawerId = R.id.left_drawer;
        } else {
            mLeftDrawerId = R.id.content_frame_left_drawer;
        }
        if (created) {
            if (mSidebarFragment == null) {
                mSidebarFragment = (SidebarFragment) getSupportFragmentManager().findFragmentById(R.id.left_drawer);
            }
            if (mSidebarFragment == null) {
                mSidebarFragment = (SidebarFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_left_drawer);
            }
            // check if the fragment container has changed, fragments can't be moved :(
            if (mSidebarFragment.getContainerId() != mLeftDrawerId) {
                SidebarFragment oldFragment = mSidebarFragment;
                mSidebarFragment = new SidebarFragment();
                mSidebarFragment.clone(oldFragment);
                getSupportFragmentManager().beginTransaction().remove(oldFragment).commit();
                getSupportFragmentManager().executePendingTransactions();
            }
        } else {
            mSidebarFragment = new SidebarFragment();
        }
        if (mSidebarFragment.getContainerId() == 0) {
            getSupportFragmentManager().beginTransaction().replace(mLeftDrawerId, mSidebarFragment).commit();
        }

        if (leftFrame != null && mLeftDrawerId == R.id.left_drawer) {
            leftFrame.setVisibility(View.GONE);
        } else if (leftFrame != null) {
            leftFrame.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
    }

    public boolean isAtRoot() {
        return getSupportFragmentManager().getBackStackEntryCount() == 0;
    }

    @Override
    public void onBackStackChanged() {
        if (isAtRoot() && (!mTabletSidebarStatic || mLeftDrawerId == R.id.left_drawer)) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        if (isAtRoot() && mTabletSidebarStatic && mLeftDrawerId != R.id.left_drawer) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isAtRoot()) {
                    toggleMenu();
                } else {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isMenuOpen()) {
            closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void toggleMenu() {
        if (isMenuOpen()) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    public void openMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void closeMenu() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public boolean isMenuOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    public HostedFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public SidebarFragment getSidebarFragment() {
        return mSidebarFragment;
    }

    public void onEventMainThread(ChangeHostFragmentEvent event) {
        Class<? extends HostedFragment> clss = event.getFragmentClass();

        if (mCurrentFragment != null && ((Object) mCurrentFragment).getClass() == clss && !event.isNewInstance())
            return;

        HostedFragment fragment = (HostedFragment) getSupportFragmentManager().findFragmentByTag(event.getFragmentTag());
        if (fragment != null && !event.isNewInstance()) {
            getSupportFragmentManager().popBackStack(event.getFragmentTag(), 0);
        } else {
            if (fragment == null || event.isNewInstance()) {
                try {
                    fragment = event.getFragmentClass().newInstance();
                    if (event.getArguments() != null) {
                        fragment.setArguments(event.getArguments());
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    throw new RuntimeException(e);
                    // ignore
                }
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(event.getInAnimation(), event.getOutAnimation(), event.getPopInAnimation(), event.getPopOutAnimation());
            ft.replace(R.id.content_frame_main, fragment, event.getFragmentTag());
            if (event.isAddToBackstack()) {
                ft.addToBackStack(event.getFragmentTag());
            }
            ft.commit();
        }
        if (event.getCallback() != null) {
            event.getCallback().onFragmentChanged(fragment);
        }

        mCurrentFragment = fragment;
        Application.postEvent(new OnHostFragmentChangedEvent(fragment));
    }

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshHelper;
    }

    public void setTabletSidebarStatic(boolean tabletSidebarStatic) {
        if (mTabletSidebarStatic == tabletSidebarStatic) return;

        attachSidebar(true);

        onBackStackChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mTabletSidebarStatic", mTabletSidebarStatic);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mTabletSidebarStatic = savedInstanceState.getBoolean("mTabletSidebarStatic");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
