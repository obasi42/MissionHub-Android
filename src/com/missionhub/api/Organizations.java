package com.missionhub.api;

import java.util.List;

import android.content.Context;

import com.loopj.android.http.RequestParams;

public class Organizations {
	
	/**
	 * Get all organizations of the current user
	 * @param ctx
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("organizations");
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Get a single organization
	 * @param ctx
	 * @param organizationId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, int organizationId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("organizations", String.valueOf(organizationId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Get listed organizations
	 * @param ctx
	 * @param organizationIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, List<Integer> organizationIds, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("organizations", ApiHelper.toList(organizationIds));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}
}