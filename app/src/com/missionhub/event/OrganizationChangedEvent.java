package com.missionhub.event;

public class OrganizationChangedEvent {

    private final long mOrganizationId;

    public OrganizationChangedEvent(long organizationId) {
        mOrganizationId = organizationId;
    }

    public long getOrganizationId() {
        return mOrganizationId;
    }

}
