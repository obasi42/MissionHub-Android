package com.missionhub.api;

import com.missionhub.R;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.util.ResourceUtils;

public class UserNotFoundException extends ApiException implements ExceptionHelper.ExceptionHelperException {

    @Override
    public String getDialogTitle() {
        return ResourceUtils.getString(R.string.exception_user_not_found_title);
    }

    @Override
    public String getDialogMessage() {
        return ResourceUtils.getString(R.string.exception_user_not_found);
    }

    @Override
    public int getDialogIconId() {
        return 0;
    }

}
