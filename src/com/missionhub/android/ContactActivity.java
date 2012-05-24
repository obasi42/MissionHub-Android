package com.missionhub.android;

import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.missionhub.android.app.MissionHubBaseActivity;
import com.missionhub.android.fragment.ContactFragment;

/**
 * The activity used to display a single contact.
 * Creates and attaches a new ContactFragment with the intent extras as the arguments.
 */
public class ContactActivity extends MissionHubBaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final ContactFragment contact = new ContactFragment();
            contact.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, contact).commit();
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
