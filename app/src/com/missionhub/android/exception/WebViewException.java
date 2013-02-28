package com.missionhub.android.exception;

/**
 * Represents the data returned by webview errors
 */
public class WebViewException extends Exception {

    private static final long serialVersionUID = -3473049479894005440L;

    private final int code;
    private final String failingUrl;

    public WebViewException(final int code, final String description, final String failingUrl) {
        super(description);
        this.code = code;
        this.failingUrl = failingUrl;
    }

    public int getCode() {
        return code;
    }

    public String getFailingUrl() {
        return failingUrl;
    }

}