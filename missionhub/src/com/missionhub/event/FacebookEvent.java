package com.missionhub.event;

import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookEvent {

    private final Session mSession;
    private final SessionState mState;
    private final Exception mException;

    public FacebookEvent(Session session, SessionState state, Exception exception) {
        mSession = session;
        mState = state;
        mException = exception;
    }

    public Session getSession() {
        return mSession;
    }

    public SessionState getState() {
        return mState;
    }

    public Exception getException() {
        return mException;
    }

}
