package com.missionhub.network;

import ch.boye.httpclientandroidlib.Header;

public class HttpResponse {

    public int statusCode;
    public String statusReason;
    public Header[] headers;

    public String responseBody;
    public byte[] responseBodyRaw;

    public HttpResponse() {
    }

    public boolean isRaw() {
        if (responseBodyRaw != null) {
            return true;
        }
        return false;
    }

}