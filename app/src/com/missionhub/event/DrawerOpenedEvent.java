package com.missionhub.event;

import android.view.View;

/**
 * Event posted when the navigation drawer is opened
 */
public class DrawerOpenedEvent {

    private View mDrawerView;

    public DrawerOpenedEvent(View drawerView) {
        mDrawerView = drawerView;
    }

    public View getDrawerView() {
        return mDrawerView;
    }

}