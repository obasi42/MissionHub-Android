package com.missionhub.api;

import com.missionhub.R;
import com.missionhub.util.ResourceUtils;

public class UserNotFoundException extends ApiException {

    @Override
    public String getDialogTitle() {
        return ResourceUtils.getString(R.string.exception_user_not_found_title);
    }

    @Override
    public String getDialogMessage() {
        return ResourceUtils.getString(R.string.exception_user_not_found);
    }

}
