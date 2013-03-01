package com.missionhub.android.model.gson;

import com.missionhub.android.api.ApiException;

public class GErrorsDepreciated {

    public InnerError error;

    public static class InnerError {
        public String message;
        public String code;
        public String title;
    }

    public ApiException getException() {
        return new ApiException(this);
    }

}