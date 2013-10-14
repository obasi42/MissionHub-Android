package com.missionhub.activity;

import android.content.Intent;
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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.authenticator.AuthenticatorActivity;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.event.DrawerClosedEvent;
import com.missionhub.event.DrawerOpenedEvent;
import com.missionhub.event.OnHostFragmentChangedEvent;
import com.missionhub.event.OnSidebarItemClickedEvent;
import com.missionhub.fragment.HostedFragment;
import com.missionhub.fragment.HostedPeopleListFragment;
import com.missionhub.fragment.HostedSurveysFragment;
import com.missionhub.fragment.SidebarFragment;
import com.missionhub.fragment.dialog.SelectOrganizationDialogFragment;
import com.missionhub.util.IntentHelper;

import org.holoeverywhere.widget.Toast;

import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;

public class HostActivity extends BaseAuthenticatedActivity implements FragmentManager.OnBackStackChangedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private HostedFragment mCurrentFragment;
    private SidebarFragment mSidebarFragment;
    private PullToRefreshAttacher mPullToRefreshHelper;
    private int mLeftDrawerId = R.id.left_drawer;
    private boolean mTabletSidebarStatic = true;
    private long mBackCooldown;
    private Multimap<String, String> mProgress = Multimaps.synchronizedMultimap(HashMultimap.<String, String>create());

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
        mPullToRefreshHelper = PullToRefreshAttacher.get(this);

        Application.registerEventSubscriber(this, ChangeHostFragmentEvent.class, OnSidebarItemClickedEvent.class);
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
                getSupportFragmentManager().beginTransaction().remove(oldFragment).commitAllowingStateLoss();
                try {
                    getSupportFragmentManager().executePendingTransactions();
                } catch (IllegalStateException e) {
                    Log.e("HostActivity", e.getMessage(), e);
                }
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

    @Override
    public void onSessionClosed() {
        Intent intent = new Intent(this, AuthenticatorActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isAtRoot() {
        return getSupportFragmentManager().getBackStackEntryCount() == 0;
    }

    @Override
    public void onBackStackChanged() {
        refreshMenuState();
    }

    private void refreshMenuState() {
        if (isAtRoot() && (!mTabletSidebarStatic || mLeftDrawerId == R.id.left_drawer)) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            closeMenu();
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
            if (!getSupportFragmentManager().popBackStackImmediate()) {
                if (mBackCooldown + 4000 > System.currentTimeMillis()) {
                    finish();
                } else {
                    mBackCooldown = System.currentTimeMillis();
                    Application.showToast(getString(R.string.action_press_to_quit), Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        refreshMenuState();
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

    @SuppressWarnings("unused")
    public void onEventMainThread(OnSidebarItemClickedEvent event) {
        Object item = event.getItem();
        if (item instanceof Integer) {
            switch (((Integer) item).intValue()) {
                case R.id.menu_item_about:
                    Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.menu_item_surveys:
                    onEventMainThread(new ChangeHostFragmentEvent(HostedSurveysFragment.class));
                    break;
                case R.id.menu_item_contacts:
                    onEventMainThread(new ChangeHostFragmentEvent(HostedPeopleListFragment.class));
                    break;
                case R.id.menu_item_help:
                    IntentHelper.openUrl(getString(R.string.main_help_url));
                    break;
                case R.id.menu_item_logout:
                    Application.getSession().close();
                    break;
                case R.id.menu_item_organization:
                    SelectOrganizationDialogFragment.showForResult(getSupportFragmentManager(), 5);
                    break;
            }
            closeMenu();
        }
    }

    public void onEventMainThread(ChangeHostFragmentEvent event) {
        Class<? extends HostedFragment> clss = event.getFragmentClass();

        if (mCurrentFragment != null && ((Object) mCurrentFragment).getClass() == clss && !event.isNewInstance())
            return;

        if (!isAtRoot() && !event.isAddToBackstack()) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        HostedFragment fragment = (HostedFragment) getSupportFragmentManager().findFragmentByTag(event.getFragmentTag());
        if (fragment != null && !event.isNewInstance()) {
            getSupportFragmentManager().popBackStackImmediate(event.getFragmentTag(), 0);
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
            fragment.setOnFragmentChangedCallback(event.getCallback());
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(event.getInAnimation(), event.getOutAnimation(), event.getPopInAnimation(), event.getPopOutAnimation());
            ft.replace(R.id.content_frame_main, fragment, event.getFragmentTag());
            if (event.isAddToBackstack()) {
                ft.addToBackStack(event.getFragmentTag());
            }
            ft.commit();
        }
        mCurrentFragment = fragment;
    }

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshHelper;
    }

    public void setTabletSidebarStatic(boolean tabletSidebarStatic) {
        if (mTabletSidebarStatic == tabletSidebarStatic) return;
        mTabletSidebarStatic = tabletSidebarStatic;
        attachSidebar(true);
        refreshMenuState();
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

    public void _setCurrentFragment(HostedFragment currentFragment) {
        mCurrentFragment = currentFragment;
        Application.postEvent(new OnHostFragmentChangedEvent(mCurrentFragment));
    }

    public void removeProgress(HostedFragment hostedFragment) {
        mProgress.removeAll(getProgressKey(hostedFragment));
        updateProgressVisibility();
        hostedFragment.onProgressStateChanged(false);
        supportInvalidateOptionsMenu();
    }

    public void setProgress(HostedFragment hostedFragment, Set<String> progress) {
        mProgress.removeAll(getProgressKey(hostedFragment));
        if (progress != null && !progress.isEmpty()) {
            mProgress.putAll(getProgressKey(hostedFragment), progress);
        }
        updateProgressVisibility();
        if (mProgress.containsKey(getProgressKey(hostedFragment))) {
            hostedFragment.onProgressStateChanged(true);
        } else {
            hostedFragment.onProgressStateChanged(false);
        }
        supportInvalidateOptionsMenu();
    }

    public void updateProgressVisibility() {
        if (mProgress.isEmpty()) {
            setSupportProgressBarIndeterminateVisibility(false);
        } else {
            setSupportProgressBarIndeterminateVisibility(true);
        }
    }

    private String getProgressKey(HostedFragment fragment) {
        return Integer.toHexString(System.identityHashCode(fragment));
    }
}
