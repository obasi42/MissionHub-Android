package com.missionhub;

import greendroid.app.GDApplication;

public class MissionHubApplication extends GDApplication {

    @Override
    public Class<?> getHomeActivityClass() {
        return MissionHubActivity.class;
    }
    
//    @Override
//    public Intent getMainApplicationIntent() {
//        return new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url)));
//    }

}