package com.missionhub.event;

import com.missionhub.application.SessionState;

public class SessionEvent {

    private SessionState mState;
    private Throwable mThrowable;
    private CharSequence mMessage;

    public SessionEvent(SessionState state) {
        this(state, null);
    }

    public SessionEvent(Throwable throwable) {
        mThrowable = throwable;
        mState = SessionState.CLOSED_ERROR;
    }

    public SessionEvent(SessionState state, CharSequence message) {
        mState = state;
        mMessage = message;
    }

    public SessionState getState() {
        return mState;
    }

    public void setState(SessionState state) {
        mState = state;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public void setThrowable(Throwable throwable) {
        mThrowable = throwable;
    }

    public CharSequence getMessage() {
        return mMessage;
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
    }
}
