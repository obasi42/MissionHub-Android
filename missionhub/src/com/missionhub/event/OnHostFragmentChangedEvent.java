package com.missionhub.event;

import com.missionhub.fragment.HostedFragment;

public class OnHostFragmentChangedEvent {

    public HostedFragment mFragment;

    public OnHostFragmentChangedEvent(HostedFragment fragment) {
        mFragment = fragment;
    }

}
