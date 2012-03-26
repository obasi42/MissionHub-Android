package com.missionhub;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.missionhub.api.model.sql.DaoSession;

public class MissionHubBaseActivity extends SherlockFragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/** Called when the activity is destroyed */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Shortcut to the MissionHubApplication
	 * 
	 * @return
	 */
	public MissionHubApplication getMHApplication() {
		return ((MissionHubApplication) getApplicationContext());
	}

	/**
	 * Returns the session
	 * 
	 * @return
	 */
	public Session getSession() {
		return getMHApplication().getSession();
	}

	/**
	 * Shortcut to show the Indeterminate progress bar
	 */
	public void showProgress() {
		setSupportProgressBarIndeterminateVisibility(true);
	}

	/**
	 * Shortcut to hide the Indeterminate progress bar
	 */
	public void hideProgress() {
		setSupportProgressBarIndeterminateVisibility(false);
	}

	/**
	 * Gets the raw sqlite database for the application context
	 * 
	 * @return
	 */
	public synchronized SQLiteDatabase getDb() {
		return getMHApplication().getDb();
	}

	/**
	 * Returns the database session for the application context
	 * 
	 * @return
	 */
	public synchronized DaoSession getDbSession() {
		return getMHApplication().getDbSession();
	}
}