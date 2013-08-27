package com.missionhub.event;

import com.missionhub.api.ListOptions;

public class OnHostedListOptionsChangedEvent {

    private final ListOptions mOptions;

    public OnHostedListOptionsChangedEvent(ListOptions options) {
        mOptions = options;
    }

    public ListOptions getOptions() {
        return mOptions;
    }

}
