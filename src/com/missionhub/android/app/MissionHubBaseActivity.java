package com.missionhub.android.app;

import java.lang.ref.WeakReference;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.missionhub.R;
import com.missionhub.R.layout;
import com.missionhub.android.api.old.model.sql.DaoSession;

public abstract class MissionHubBaseActivity extends SherlockFragmentActivity {

	/** the progress/refresh menu item */
	private WeakReference<MenuItem> progressMenuItem;

	/** the progress bar view */
	private LinearLayout progressBarItem;

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
		setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
	}

	/**
	 * Shortcut to hide the Indeterminate progress bar
	 */
	public void hideProgress() {
		setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
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

	@Override
	public void setSupportProgressBarIndeterminateVisibility(final boolean visible) {
		if (progressMenuItem == null) {
			super.setSupportProgressBarIndeterminateVisibility(visible);
		} else {
			updateProgressAnimation(visible);
		}
	}

	/**
	 * Sets the menu item to use as a indeterminate progress bar
	 * 
	 * @param item
	 */
	public void setSupportProgressBarIndeterminateItem(final MenuItem item) {
		progressMenuItem = new WeakReference<MenuItem>(item);

		if (progressMenuItem != null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
			progressBarItem = (LinearLayout) layoutInflater.inflate(R.layout.widget_refresh_button, null);
		}
	}

	/**
	 * Starts/stops animation on progress change
	 */
	private void updateProgressAnimation(final boolean visible) {
		if (progressMenuItem == null || progressBarItem == null) {
			return;
		}

		final MenuItem item = progressMenuItem.get();
		if (item == null) {
			return;
		}

		if (visible) {
			item.setEnabled(false);
			item.setActionView(progressBarItem);
		} else {
			item.setEnabled(true);
			item.setActionView(null);
		}
	}

	/**
	 * Convenience method for setting the content view for old tablets
	 * 
	 * @param normal
	 * @param oldTablet
	 */
	public void setContentView(final int normal, final int oldTablet) {
		if (getMHApplication().getDisplayMode().isOldTablet()) {
			setContentView(oldTablet);
		} else {
			setContentView(normal);
		}
	}

	public DisplayMode getDisplayMode() {
		return getMHApplication().getDisplayMode();
	}
}