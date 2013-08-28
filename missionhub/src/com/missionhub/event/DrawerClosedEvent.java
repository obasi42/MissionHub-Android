package com.missionhub.event;

import android.view.View;

/**
 * Event posted when the navigation drawer is closed
 */
public class DrawerClosedEvent {

    private View mDrawerView;

    public DrawerClosedEvent(View drawerView) {
        mDrawerView = drawerView;
    }

    public View getDrawerView() {
        return mDrawerView;
    }

}