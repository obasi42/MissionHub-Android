package com.missionhub.api.client;

import java.util.List;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.missionhub.api.ApiClient;
import com.missionhub.api.ApiHelper;
import com.missionhub.api.ApiResponseHandler;

public class People {
	
	/**
	 * Get a single person
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, int personId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteJsonUrl("people", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams();
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Get a list of people
	 * @param ctx
	 * @param personIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, List<Integer> personIds, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteJsonUrl("people", ApiHelper.toList(personIds));
		RequestParams params = ApiHelper.getDefaultRequestParams();
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Get the currently logged in person (identified by access_token)
	 * @param ctx
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient getMe(Context ctx, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteJsonUrl("people", "me");
		RequestParams params = ApiHelper.getDefaultRequestParams();
		client.get(url, params, responseHandler);
		return client;
	}
}