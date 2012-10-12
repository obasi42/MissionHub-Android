package com.missionhub.api;

import com.missionhub.network.HttpClient.Method;
import com.missionhub.network.HttpHeaders;
import com.missionhub.network.HttpParams;

/**
 * Holds the date required to make an api request
 */
public class ApiRequest {

	/** the logging tag */
	public static final String TAG = ApiRequest.class.getSimpleName();

	/** the http method */
	public Method method;

	/** the url */
	public String url;

	/** the http headers */
	public HttpHeaders headers;

	/** the http get/post parameters */
	public HttpParams params;

	/** true if the request requires an access token */
	public boolean authenticated;

	/** the organization id the request was for */
	public long organizationId;

	/** the number of retries attempted */
	public int retries = 0;

	/**
	 * Creates an ApiRequest object.
	 * 
	 * @param method
	 * @param url
	 * @param headers
	 * @param params
	 * @param authenticated
	 * @param organizationId
	 */
	protected ApiRequest(final Method method, final String url, final HttpHeaders headers, final HttpParams params, final boolean authenticated,
			final long organizationId) {
		this.method = method;
		this.url = url;
		this.headers = headers;
		this.params = params;
		this.authenticated = authenticated;
		this.organizationId = organizationId;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(method.toString() + ": ");
		sb.append(url);
		if (headers != null) {
			sb.append("    HEADERS: " + headers.toString());
		}
		if (params != null) {
			sb.append("    PARAMS: " + params.toString());
		}
		return sb.toString();
	}
}