package com.missionhub.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.missionhub.R;
import com.missionhub.application.Session.SessionInvalidTokenEvent;
import com.missionhub.application.Session.SessionInvalidatedEvent;
import com.missionhub.fragment.MainFragment;
import com.missionhub.fragment.MainMenuFragment;
import com.missionhub.fragment.MyContactsFragment;
import com.missionhub.fragment.dialog.SelectOrganizationDialogFragment;
import com.missionhub.util.IntentHelper;
import org.holoeverywhere.widget.Toast;

/**
 * The main activity controls the attachment of the main fragments such as My Contacts, All Contacts, Surveys, etc.
 */
public class MainActivity extends BaseAuthenticatedActivity {

    /**
     * The main drawer layout
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The drawer toggle state
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * the main content fragment
     */
    private MainFragment mFragment;

    /**
     * the main menu fragment
     */
    private MainMenuFragment mMenuFragment;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
                R.string.menu_open, R.string.menu_close) {

            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // setup the fragments
        if (savedInstanceState != null) {
            mFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            mMenuFragment = (MainMenuFragment) getSupportFragmentManager().findFragmentById(R.id.left_drawer);
        } else {
            mFragment = new MyContactsFragment();
            mMenuFragment = new MainMenuFragment();
            setFragment(mFragment);
            setMenuFragment(mMenuFragment);
        }

        // show the up button to access the menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleDrawer(Gravity.LEFT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Switches the main fragment
     *
     * @return true if the fragment was switched
     */
    public boolean switchFragment(final MainFragment fragment) {
        if (fragment == null) return false;
        if (fragment == mFragment) return false;
        setFragment(fragment);
        return true;
    }

    /**
     * Switches the main fragment to an instance of the given class
     *
     * @param clss the type of fragment to create
     * @return true if the fragment was switched
     */
    public boolean switchFragment(final Class<? extends MainFragment> clss) {
        if (mFragment != null && ((Object) mFragment).getClass() == clss) return false;
        try {
            return switchFragment(clss.newInstance());
        } catch (final Exception e) { /* ignore */}
        return false;
    }

    private void setFragment(final MainFragment fragment) {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_frame, fragment).commit();
        mFragment = fragment;
    }

    private void setMenuFragment(final MainMenuFragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.left_drawer, fragment).commit();
        mMenuFragment = fragment;
    }

    public void openPreferences() {
        final Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    public void openOrganization() {
        SelectOrganizationDialogFragment.showForResult(getSupportFragmentManager(), null);
    }

    /**
     * Opens the About MissionHub Activity
     */
    public void openAbout() {
        final Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openHelp() {
        IntentHelper.openUrl(getString(R.string.main_help_url));
    }

    /**
     * Responds to Session Invalidated Events
     *
     * @param event
     */
    @Override
    public void onEventMainThread(final SessionInvalidatedEvent event) {
        Toast.makeText(this, getString(R.string.main_logged_out), Toast.LENGTH_LONG).show();
        startInitActivity();
    }

    /**
     * Responds to Session Invalid Token Events
     *
     * @param event
     */
    @Override
    public void onEventMainThread(final SessionInvalidTokenEvent event) {
        Toast.makeText(this, getString(R.string.main_invalid_token), Toast.LENGTH_LONG).show();
        startInitActivity();
    }

    /**
     * Finishes the current activity and starts the init activity.
     */
    private void startInitActivity() {
        final Intent intent = new Intent(getApplicationContext(), InitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void toggleDrawer(int gravity) {
        if (mDrawerLayout.isDrawerOpen(gravity)){
            mDrawerLayout.closeDrawer(gravity);
        } else {
            mDrawerLayout.openDrawer(gravity);
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
}