package com.missionhub.api;

import com.missionhub.network.HttpResponse;

/**
 * Holds the result of an API request.
 */
public class ApiResponse {

	/** the api request object that generated this response */
	public ApiRequest request;

	/** the raw http response from the network client */
	public HttpResponse httpResponse;

	public ApiResponse(final ApiRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		if (httpResponse != null) {
			sb.append("CODE: " + httpResponse.responseCode + "\n");
			sb.append("MESSAGE: " + httpResponse.responseMessage + "\n");
			sb.append("BODY: " + httpResponse.responseBody + "\n");
		}

		return sb.toString();
	}

}