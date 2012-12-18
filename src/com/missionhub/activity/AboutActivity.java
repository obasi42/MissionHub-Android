package com.missionhub.activity;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.util.IntentHelper;

@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

	@InjectView(R.id.version) private TextView mVersion;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mVersion.setText(Application.getVersionName());
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void clickFeedback(final View view) {
		IntentHelper.sendEmail(getString(R.string.about_feedback_email));
	}

	public void clickLicenses(final View view) {
		final Intent intent = new Intent(this, LicensesActivity.class);
		startActivity(intent);
	}

	public void clickPrivacy(final View view) {
		IntentHelper.openUrl(getString(R.string.about_privacy_url));
	}

	public void clickTerms(final View view) {
		IntentHelper.openUrl(getString(R.string.about_terms_url));
	}
}