package com.missionhub;

import android.database.sqlite.SQLiteDatabase;

import com.missionhub.sql.DaoMaster;
import com.missionhub.sql.DaoMaster.DevOpenHelper;
import com.missionhub.sql.DaoSession;

import de.greenrobot.dao.IdentityScopeType;

import greendroid.app.GDApplication;

public class MissionHubApplication extends GDApplication {

	private SQLiteDatabase db;
	private DaoSession daoSession;
	
    @Override
    public Class<?> getHomeActivityClass() {
        return MissionHubActivity.class;
    }
    
    @Override
    public void onTerminate() {
    	db.close();
    	super.onTerminate();
    }
    
    public SQLiteDatabase getDb() {
    	if (db == null) {
    		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mh-db", null);
            db = helper.getWritableDatabase();
    	}
    	return db;
    }

    public DaoSession getDbSession() {
    	if (daoSession == null) {
    		DaoMaster daoMaster = new DaoMaster(getDb());
            daoSession = daoMaster.newSession(IdentityScopeType.None);
    	}
    	return daoSession;
    }
}