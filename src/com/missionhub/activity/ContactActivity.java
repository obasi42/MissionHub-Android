package com.missionhub.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.fragment.ContactFragment;

public class ContactActivity extends BaseAuthenticatedActivity {

	private long mPersonId;
	private ContactFragment mFragment;

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
		mFragment = (ContactFragment) fm.findFragmentById(R.id.content_frame);

		if (mFragment == null) {
			Log.e("FRAGMENT NULL", "FRAGMENT NULL");
			mFragment = ContactFragment.instantiate(mPersonId);
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

}