package com.missionhub.event;

import com.missionhub.application.SessionState;

public class SessionEvent {

    private SessionState mState;
    private Exception mException;
    private CharSequence mMessage;

    public SessionEvent(SessionState state) {
        this(state, null);
    }

    public SessionEvent(Exception exception) {
        mException = exception;
        mState = SessionState.CLOSED_ERROR;
    }

    public SessionEvent(SessionState state, CharSequence message) {
        mState = state;
        mMessage = message;
    }

    public SessionState getState() {
        return mState;
    }

    public Exception getException() {
        return mException;
    }

    public CharSequence getMessage() {
        return mMessage;
    }
}
