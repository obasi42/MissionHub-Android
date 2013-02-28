package com.missionhub.android.util;

public abstract class SSEFEvent {

    public static enum Status {
        STARTED, SUCCESS, ERROR, FINISHED
    }

    public Status status;
    public Object data;

    public SSEFEvent(final Status status) {
        this(status, null);
    }

    public SSEFEvent(final Status status, final Object data) {
        this.status = status;
        this.data = data;
    }

}