package com.missionhub.android.network;

import java.io.IOException;

public class HttpException extends IOException {

    private static final long serialVersionUID = -4679211317264497570L;

    private final int mCode;

    protected HttpException(final int code, final String status) {
        super(status);
        mCode = code;
    }

    /**
     * Returns the http error code
     *
     * @return
     */
    public int getCode() {
        return mCode;
    }
}