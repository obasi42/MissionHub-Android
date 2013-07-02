package com.missionhub.model.gson;

import com.missionhub.api.ApiException;
import com.missionhub.api.InvalidFacebookTokenException;
import com.missionhub.api.UserNotFoundException;

public class GErrors {

    public String[] errors;
    public String code;

    public ApiException getException() {
        if (code != null) {
            if (code.equalsIgnoreCase("user_not_found")) {
                return new UserNotFoundException();
            } else if (code.equalsIgnoreCase("invalid_facebook_token")) {
                return new InvalidFacebookTokenException();
            }
        }
        if (code != null || errors != null) {
            final StringBuilder sb = new StringBuilder();
            if (errors != null) {
                for (final String error : errors) {
                    sb.append(error);
                    sb.append("\n");
                }
            }
            return new ApiException(sb.toString().trim(), code);
        }
        return null;
    }

}