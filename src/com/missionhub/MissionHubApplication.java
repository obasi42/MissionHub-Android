package com.missionhub;

import greendroid.app.GDApplication;
import com.cr_wd.android.network.NetworkUtils;

public class MissionHubApplication extends GDApplication {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//this.notifier = new ApplicationNotifier(this);
		
		NetworkUtils.disableConnectionReuseIfNecessary();
		NetworkUtils.enableHttpResponseCache(this);
	}
	
	public String getVersion() {
    	try {
    		return String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
    	} catch (Exception e) {}
    	return null;
    }
	
}
