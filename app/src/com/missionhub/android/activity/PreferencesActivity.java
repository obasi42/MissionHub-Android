package com.missionhub.android.activity;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.android.R;
import com.missionhub.android.fragment.PreferencesFragment;

public class PreferencesActivity extends BaseAuthenticatedActivity {

    private PreferencesFragment mFragment;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_frame);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mFragment = (PreferencesFragment) getSupportFragmentManager().findFragmentById(R.layout.content_frame);
        } else {
            mFragment = new PreferencesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mFragment).commit();
        }
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}