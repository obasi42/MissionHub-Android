package com.missionhub.android.activity;

import android.os.Bundle;
import com.missionhub.android.application.Application;
import com.missionhub.android.application.Session.SessionInvalidTokenEvent;
import com.missionhub.android.application.Session.SessionInvalidatedEvent;

/**
 * Listens for SesisonEvents and responds accordingly.
 */
public abstract class BaseAuthenticatedActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.registerEventSubscriber(this, SessionInvalidatedEvent.class, SessionInvalidTokenEvent.class);
    }

    @Override
    public void onDestroy() {
        Application.unregisterEventSubscriber(this);
        super.onDestroy();
    }

    /**
     * Responds to Session Invalidated Events
     *
     * @param event
     */
    public void onEventMainThread(final SessionInvalidatedEvent event) {
        finish();
    }

    /**
     * Responds to Session Invalid Token Events
     *
     * @param event
     */
    public void onEventMainThread(final SessionInvalidTokenEvent event) {
        finish();
    }
}