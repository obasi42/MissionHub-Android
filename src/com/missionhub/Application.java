package com.missionhub;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import com.missionhub.config.Preferences;
import com.missionhub.sql.DaoMaster;
import com.missionhub.sql.DaoMaster.DevOpenHelper;
import com.missionhub.sql.DaoSession;

import de.greenrobot.dao.IdentityScopeType;

import greendroid.app.GDApplication;

public class Application extends GDApplication {
	
	public static final int MESSAGE_USER_LOGGED_OUT = 0;
	public static final int MESSAGE_USER_LOGGED_IN = 1;
	public static final int MESSAGE_USER_REFRESH_STARTED = 2;
	public static final int MESSAGE_USER_REFRESH_END = 3;
	public static final int MESSAGE_USER_REFRESH_ERROR = 4; 
	
	/* Application Level Handlers */
	private List<WeakReference<Handler>> handlers = Collections.synchronizedList(new ArrayList<WeakReference<Handler>>());
	
	private SQLiteDatabase db;
	private DaoSession daoSession;
	private ApplicationUser user;
	
    @Override
    public Class<?> getHomeActivityClass() {
        return MissionHubActivity.class;
    }
    
    public void registerHandler(Handler h) {
    	handlers.add(new WeakReference<Handler>(h));
    }
    
    public synchronized void postMessage(int message) {
    	List<WeakReference<Handler>> delete = Collections.synchronizedList(new ArrayList<WeakReference<Handler>>());
    	Iterator<WeakReference<Handler>> itr = handlers.iterator();
    	while(itr.hasNext()) {
    		WeakReference<Handler> wr = itr.next();
    		Handler h = wr.get();
    		if (h != null) {
    			Message m = h.obtainMessage();
    			m.what = message;
    			h.sendMessage(m);
    		} else {
    			delete.add(wr);
    		}
    	}
    	handlers.removeAll(delete);
    	delete.clear();
    }
    
    @Override
    public void onTerminate() {
    	db.close();
    	super.onTerminate();
    }
    
    public SQLiteDatabase getDb() {
    	if (db == null) {
    		DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "mh-db", null);
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
    
    public ApplicationUser getUser() {
    	return getUser(Preferences.getUserID(this));
    }
    
    public ApplicationUser getUser(int personId) {
    	if (user == null) {
    		user = new ApplicationUser(this, personId);
    	}
    	return user;
    }
    
    public String getVersion() {
    	try {
    		return String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
    	} catch (Exception e) {}
    	return null;
    }
}