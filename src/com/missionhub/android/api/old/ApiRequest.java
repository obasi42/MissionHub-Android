package com.missionhub.android.api.old;

public class ApiRequest {

	public final ApiClient client;
	public final int requestId;

	public ApiRequest(final ApiClient client, final int requestId) {
		this.client = client;
		this.requestId = requestId;
	}

	public boolean cancel() {
		return client.cancel(requestId);
	}
}