package com.missionhub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.fragment.HostedProfileFragment;
import com.missionhub.model.Person;

public class ProfileActivity extends BaseAuthenticatedActivity {

    private long mPersonId;
    private HostedProfileFragment mFragment;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.content_frame);

        mPersonId = getIntent().getExtras().getLong("personId", -1);

        if (savedInstanceState != null) {
            mPersonId = savedInstanceState.getLong("mPersonId");
        }

        final FragmentManager fm = getSupportFragmentManager();
        mFragment = (HostedProfileFragment) fm.findFragmentById(R.id.content_frame);

        if (mFragment == null) {
            Log.e("FRAGMENT NULL", "FRAGMENT NULL");
            mFragment = HostedProfileFragment.instantiate(mPersonId);
            fm.beginTransaction().add(R.id.content_frame, mFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("mPersonId", mPersonId);
    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPersonId = savedInstanceState.getLong("mPersonId");
    }

    public static void start(final Context context, final long personId) {
        final Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("personId", personId);
        context.startActivity(intent);
    }

    public static void start(final Context context, final Person p) {
        start(context, p.getId());
    }

}