package com.missionhub.event;

public class OnOrganizationChangedEvent {

    private final long mOrganizationId;

    public OnOrganizationChangedEvent(long organizationId) {
        mOrganizationId = organizationId;
    }

    public long getOrganizationId() {
        return mOrganizationId;
    }

}
