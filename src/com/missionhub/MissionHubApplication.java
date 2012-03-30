package com.missionhub;

import greendroid.app.GDApplication;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cr_wd.android.network.NetworkUtils;
import com.missionhub.api.model.sql.DaoMaster;
import com.missionhub.api.model.sql.DaoMaster.OpenHelper;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.MissionHubOpenHelper;
import com.missionhub.config.Preferences;
import com.missionhub.util.Upgrade;

/**
 * MissionHub's Base Application
 */
public class MissionHubApplication extends GDApplication {

	/** logging tag */
	public static final String TAG = MissionHubActivity.class.getSimpleName();

	/** the mSession */
	private Session mSession;

	/** application context's sqlite database */
	private SQLiteDatabase mDb;

	/** application context's database cache mSession */
	private DaoSession mDaoSession;

	/** database name */
	private static final String DB_NAME = "mh-mDb";

	/**
	 * Called when the application is started
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		// disables http connection reuse in Donut and below, as it was buggy
		NetworkUtils.disableConnectionReuseIfNecessary();

		// enables the http response cache for ICS
		NetworkUtils.enableHttpResponseCache(this);

		// runs upgrade methods
		Upgrade.doUpgrades(this);

		// sets up the mSession
		Session.resumeSession(this);
	}

	/**
	 * Called when the application terminates. Makes sure the database closes
	 * nicely.
	 */
	@Override
	public void onTerminate() {
		try {
			mDb.close();
		} catch (final Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		super.onTerminate();
	}

	/**
	 * Gets the current mSession
	 * 
	 * @return
	 */
	public synchronized Session getSession() {
		return mSession;
	}

	/**
	 * Sets the mSession
	 * 
	 * @param mSession
	 */
	public synchronized void setSession(final Session mSession) {
		this.mSession = mSession;
	}

	/**
	 * Returns the version name of the application
	 * 
	 * @return
	 */
	public synchronized String getVersion() {
		try {
			return String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (final Exception e) {}
		return null;
	}

	/**
	 * Gets the raw sqlite database for the application context
	 * 
	 * @return
	 */
	public synchronized SQLiteDatabase getDb() {
		if (mDb == null) {
			final OpenHelper helper = new MissionHubOpenHelper(getApplicationContext(), DB_NAME, null);
			mDb = helper.getWritableDatabase();
		}
		return mDb;
	}

	/**
	 * Returns the database mSession for the application context
	 * 
	 * @return
	 */
	public synchronized DaoSession getDbSession() {
		if (mDaoSession == null) {
			final DaoMaster daoMaster = new DaoMaster(getDb());
			mDaoSession = daoMaster.newSession();
		}
		return mDaoSession;
	}

	/**
	 * Deletes the mh-mDb
	 * 
	 * @return
	 */
	public boolean deleteDatabase() {
		getDb().close();
		mDaoSession = null;
		mDb = null;
		return deleteDatabase(DB_NAME);
	}

	/**
	 * Resets all app data
	 */
	public synchronized void reset() {
		deleteDatabase();
		Preferences.reset(this);
		setSession(null);
		Session.resumeSession(this);
	}

	public synchronized void logout() {
		getSession().logout();
	}
}