package com.missionhub.event;

import com.missionhub.fragment.SidebarFragment;

public class OnSidebarItemClickedEvent {

    private final SidebarFragment mSidebar;
    private final Object mItem;

    public OnSidebarItemClickedEvent(SidebarFragment sidebar, Object item) {
        mSidebar = sidebar;
        mItem = item;
    }


    public SidebarFragment getSidebar() {
        return mSidebar;
    }

    public Object getItem() {
        return mItem;
    }

}
