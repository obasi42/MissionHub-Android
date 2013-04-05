package com.missionhub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.missionhub.R;
import com.missionhub.application.Session.SessionInvalidTokenEvent;
import com.missionhub.application.Session.SessionInvalidatedEvent;
import com.missionhub.fragment.MainFragment;
import com.missionhub.fragment.MainMenuFragment;
import com.missionhub.fragment.MyContactsFragment;
import com.missionhub.util.IntentHelper;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.slider.SliderView;
import org.holoeverywhere.widget.Toast;

/**
 * The main activity controls the attachment of the main fragments such as My Contacts, All Contacts, Surveys, etc.
 */
public class MainActivity extends BaseAuthenticatedActivity {

    /**
     * the main content fragment
     */
    private MainFragment mFragment;

    /**
     * the main menu fragment
     */
    private MainMenuFragment mMenuFragment;

    @Override
    protected void onPreInit(Holo config, Bundle savedInstanceState) {
        super.onPreInit(config, savedInstanceState);
        config.requireSlider = true;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // setup the slider addon
        final AddonSlider.AddonSliderA slider = getAddonSlider();
        slider.setDragWithActionBar(true);
        slider.disableShadow();
        slider.setTranslateFactor(0f);
        slider.setTouchMode(SliderView.TouchMode.Left);
        slider.setLeftViewWidth((int) getResources().getDimension(R.dimen.main_menu_width));

        // setup the fragments
        if (savedInstanceState != null) {
            mFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);
            mMenuFragment = (MainMenuFragment) getSupportFragmentManager().findFragmentById(R.id.leftView);
        } else {
            mFragment = new MyContactsFragment();
            mMenuFragment = new MainMenuFragment();
            setFragment(mFragment);
            setMenuFragment(mMenuFragment);
        }

        // show the up button to access the menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public AddonSlider.AddonSliderA getAddonSlider() {
        return addon(AddonSlider.class);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getAddonSlider().toggle();
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
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.contentView, fragment).commit();
        mFragment = fragment;
    }

    private void setMenuFragment(final MainMenuFragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.leftView, fragment).commit();
        mMenuFragment = fragment;
    }

    public void openPreferences() {
        final Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!getAddonSlider().isContentShowed()) {
            getAddonSlider().showContentDelayed();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}