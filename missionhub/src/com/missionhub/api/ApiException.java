package com.missionhub.api;

import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.MissionHubException;

/**
 * All API errors extend or directly use this exception type.
 */
public class ApiException extends MissionHubException {

    private static final long serialVersionUID = 1L;

    private String mCode;

    public ApiException() {
        super();
    }

    public ApiException(final String message) {
        super(message);
    }

    public ApiException(final String message, final String code) {
        super(message);
        mCode = code;
    }

    public ApiException(final Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return mCode;
    }

    public static ApiException wrap(Exception e) {
        if (e instanceof ApiException) {
            return (ApiException) e;
        }
        return new ApiException(e);
    }
}