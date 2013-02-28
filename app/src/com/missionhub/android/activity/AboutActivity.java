package com.missionhub.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.android.R;
import com.missionhub.android.application.Application;
import com.missionhub.android.util.IntentHelper;
import org.holoeverywhere.widget.TextView;

public class AboutActivity extends BaseActivity {

    private TextView mVersion;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVersion = (TextView) findViewById(R.id.version);
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