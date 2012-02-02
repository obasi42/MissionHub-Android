package com.missionhub;

import greendroid.app.GDApplication;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cr_wd.android.network.NetworkUtils;
import com.missionhub.api.model.sql.DaoMaster;
import com.missionhub.api.model.sql.DaoMaster.OpenHelper;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.MissionHubOpenHelper;

/**
 * MissionHub's Base Application
 */
public class MissionHubApplication extends GDApplication {
	
	/** logging tag */
	public static final String TAG = MissionHubActivity.class.getSimpleName(); 
	
	/** the session */
	private Session session;
	
	/** application context's sqlite database */
	private SQLiteDatabase db;
	
	/** application context's database cache session */
	private DaoSession daoSession;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// disables http connection reuse in Donut and below, as it was buggy
		NetworkUtils.disableConnectionReuseIfNecessary();
		
		// enables the http response cache for ICS
		NetworkUtils.enableHttpResponseCache(this);
	}
	
    @Override
    public void onTerminate() {
    	try {
    		db.close();
    	} catch (Exception e) {
    		Log.w(TAG, e.getMessage(), e);
    	}
    	super.onTerminate();
    }
	
	/**
	 * Gets the current session
	 * @return
	 */
	public synchronized Session getSession() {
		if (session == null) {
			session = new Session(this);
		}
		return session;
	}
	
	/**
	 * Returns the version code of the application
	 * @return
	 */
	public synchronized String getVersion() {
    	try {
    		return String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
    	} catch (Exception e) {}
    	return null;
    }
	
	/**
	 * Gets the raw sqlite database for the application context
	 * @return
	 */
    public synchronized SQLiteDatabase getDb() {
    	if (db == null) {
    		OpenHelper helper = new MissionHubOpenHelper(getApplicationContext(), "mh-db", null);
            db = helper.getWritableDatabase();
    	}
    	return db;
    }

    /**
     * Returns the database session for the application context
     * @return
     */
    public synchronized DaoSession getDbSession() {
    	if (daoSession == null) {
    		DaoMaster daoMaster = new DaoMaster(getDb());
            daoSession = daoMaster.newSession();
    	}
    	return daoSession;
    }
	
}
