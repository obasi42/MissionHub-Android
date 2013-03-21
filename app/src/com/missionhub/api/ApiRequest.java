package com.missionhub.api;

import com.github.kevinsawicki.http.HttpRequest;
import com.missionhub.api.Api.ApiResponseParser;
import com.missionhub.application.Session;
import com.missionhub.model.gson.GErrors;
import com.missionhub.model.gson.GErrorsDepreciated;
import com.missionhub.util.U;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic api call
 *
 * @param <T> the return type of the call
 */
public class ApiRequest<T> {

    private final ApiOptions mOptions;
    private boolean mParamsBuilt = false;

    private HttpRequest mRequest;
    private String mBody;
    private T mParsedBody;
    private String mMessage;
    private int mCode;

    private boolean mRequestStarted;
    private boolean mRequestFinished;

    protected ApiRequest(final ApiOptions options) {
        if (options == null) throw new RuntimeException("ApiOptions required.");

        mOptions = options;
    }


    private synchronized HttpRequest getRequest() throws ApiException {
        if (mRequest == null) {
            try {
                mRequest = Api.getInstance().createRequest(getUrl(), getHttpMethod(), getHttpHeaders(), getHttpParams(), isAuthenticated());
            } catch (Exception e) {
                throw ApiException.wrap(e);
            }
        }
        return mRequest;
    }

    private synchronized void doRequest() throws ApiException {
        if (mRequestStarted) return;

        mRequestStarted = true;
        mRequestFinished = false;

        try {
            mCode = getRequest().code();
            mMessage = getRequest().message();
            mBody = getRequest().body();

            checkResponse();
            mParsedBody = onParseReponse(this);
        } catch (Exception e) {
            throw ApiException.wrap(e);
        } finally {
            try {
                getRequest().disconnect();
            } catch (Exception e2) {
                /* ignore */
            }
            mRequestFinished = true;
        }
    }

    private synchronized boolean isStarted() {
        return mRequestStarted;
    }

    private synchronized boolean isFinished() {
        return mRequestFinished;
    }

    public synchronized T get() throws ApiException {
        doRequest();

        return mParsedBody;
    }

    public String getBody() throws ApiException {
        doRequest();

        return mBody;
    }

    public int getCode() throws ApiException {
        doRequest();

        return mCode;
    }

    public String getMessage() throws ApiException {
        doRequest();

        return mMessage;
    }

    private void checkResponse() throws ApiException {
        if (isAuthenticated() && getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            Session.getInstance().reportInvalidAccessToken();
            throw new AccessTokenException();
        }

        if (!U.isNullEmpty(getBody())) {
            ApiException exception = null;
            try {
                final GErrors errors = Api.sGson.fromJson(getBody(), GErrors.class);
                if (errors.errors == null) throw new ApiException("The API returned an error with out a message");
                exception = errors.getException();
            } catch (final Exception e) {
                /* ignore */
            }

            if (exception == null) {
                try {
                    final GErrorsDepreciated error = Api.sGson.fromJson(getBody(), GErrorsDepreciated.class);
                    if (error.error != null && error.error.code.equalsIgnoreCase("56")) {
                        Session.getInstance().reportInvalidAccessToken();
                        exception = new AccessTokenException(error);
                    } else {
                        exception = error.getException();
                    }
                } catch (final Exception e) {
                    /* ignore */
                }
            }
            if (exception != null) {
                throw ApiException.wrap(exception);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected T onParseReponse(final ApiRequest response) throws Exception {
        return getReponseParser().parseResponse(response);
    }

    @SuppressWarnings("unchecked")
    protected ApiResponseParser<T> getReponseParser() {
        return (ApiResponseParser<T>) mOptions.responseParser;
    }

    public String getHttpMethod() {
        return mOptions.method;
    }

    public String getUrl() {
        return mOptions.url;
    }

    public Map<String, String> getHttpHeaders() {
        return mOptions.headers;
    }

    public Map<String, String> getHttpParams() {
        if (mParamsBuilt) return mOptions.params;

        if (mOptions.params == null) {
            mOptions.params = new HashMap<String, String>();
        }
        if (mOptions.includes != null) {
            mOptions.params.put("include", U.toCSV(mOptions.includes));
        }
        if (mOptions.since != null) {
            mOptions.params.put("since", String.valueOf(mOptions.since));
        }
        if (mOptions.limit != null) {
            mOptions.params.put("limit", String.valueOf(mOptions.limit));
        }
        if (mOptions.offset != null) {
            mOptions.params.put("offset", String.valueOf(mOptions.offset));
        }

        mParamsBuilt = true;

        return mOptions.params;
    }

    public boolean isAuthenticated() {
        if (mOptions.authenticated == null) {
            return true;
        }
        return mOptions.authenticated;
    }

    public synchronized void reload() throws ApiException {
        try {
            getRequest().disconnect();
        } catch (Exception e) {
            /* ignore */
        }

        mRequest = null;

        mRequestStarted = false;
        mRequestFinished = false;

        mBody = null;
        mParsedBody = null;
        mMessage = null;
        mCode = 0;

        doRequest();
    }

}