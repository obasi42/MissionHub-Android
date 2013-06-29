package com.missionhub.model.gson;

import com.missionhub.api.ApiException;

public class GErrors {

    public String[] errors;
    public String code;

    public ApiException getException() {
        final StringBuilder sb = new StringBuilder();
        if (errors != null) {
            for (final String error : errors) {
                sb.append(error);
                sb.append("\n");
            }
        }
        return new ApiException(sb.toString().trim(), code);
    }

}