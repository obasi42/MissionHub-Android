package com.missionhub.api;

import com.missionhub.exception.ExceptionHelper;
import com.missionhub.exception.MissionHubException;
import com.missionhub.model.gson.GErrorsDepreciated;

/**
 * All API errors extend or directly use this exception type.
 */
public class ApiException extends MissionHubException implements ExceptionHelper.ExceptionHelperException {

    private static final long serialVersionUID = 1L;

    private String mTitle;
    private String mCode;

    public ApiException() {
        super();
    }

    public ApiException(final String message) {
        super(message);
    }

    public ApiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ApiException(final Throwable cause) {
        super(cause);
    }

    public ApiException(final GErrorsDepreciated error) {
        this(error.error.title, error.error.message, error.error.code);
    }

    public ApiException(final String title, final String message, final String code) {
        super(message);
        mTitle = title;
        mCode = code;
    }

    public String getTitle() {
        return mTitle;
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

    @Override
    public String getDialogTitle() {
        return getTitle();
    }

    @Override
    public String getDialogMessage() {
        if (getCode() != null) {
            return getMessage() + "\ncode: " + getCode();
        } else {
            return getMessage();
        }
    }

    @Override
    public int getDialogIconId() {
        return 0;
    }
}