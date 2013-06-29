package com.missionhub.event;

import org.holoeverywhere.widget.Toast;

public class ToastEvent {
    public String message;
    public int duration = Toast.LENGTH_SHORT;

    public ToastEvent(final String message) {
        this.message = message;
    }

    public ToastEvent(final String message, final int duration) {
        this.message = message;
        this.duration = duration;
    }
}
