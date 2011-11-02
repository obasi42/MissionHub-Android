package com.missionhub.api;

import android.content.Context;

import com.loopj.android.http.RequestParams;

public class Roles {

	/**
	 * Change a person's role
	 * 
	 * @param ctx
	 * @param personId
	 * @param role
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient change(Context ctx, int personId, String role, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("roles", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		params.put("role", role);
		params.put("_method", "put");
		client.post(url, params, responseHandler);
		return client;
	}

}