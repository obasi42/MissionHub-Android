package com.missionhub.api;

import com.missionhub.exception.ExceptionHelper;

import org.apache.commons.lang3.StringUtils;

public class ApiHttpException extends ApiException implements ExceptionHelper.ExceptionHelperException {

    private int mCode;
    private String mMessage;

    public ApiHttpException(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    @Override
    public String getDialogTitle() {
        return "Network Error";
    }

    @Override
    public String getDialogMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("The server returned status code: ").append(mCode);
        if (StringUtils.isNotEmpty(mMessage)) {
            builder.append("Message: ").append(mMessage);
        }
        return builder.toString();
    }

    @Override
    public int getDialogIconId() {
        return 0;
    }

}
