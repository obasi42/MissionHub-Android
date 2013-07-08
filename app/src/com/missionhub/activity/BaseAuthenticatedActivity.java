package com.missionhub.activity;

import android.os.Bundle;

import com.missionhub.application.Application;
import com.missionhub.event.SessionEvent;
import com.missionhub.exception.ExceptionHelper;

/**
 * Listens for SessionEvents and responds accordingly.
 */
public abstract class BaseAuthenticatedActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.registerEventSubscriber(this, SessionEvent.class);
    }

    @Override
    public void onDestroy() {
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
    }

    public void onSessionClosed() {

    }

    /**
     * Responds to Session Invalidated Events
     *
     * @param event
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(final SessionEvent event) {
        switch (event.getState()) {
            case CLOSED_ERROR:
                if (event.getException() != null) {
                    ExceptionHelper eh = new ExceptionHelper(this, event.getException());
                    eh.makeToast();
                }
            case CLOSED:
                onSessionClosed();
                finish();
        }
    }
}