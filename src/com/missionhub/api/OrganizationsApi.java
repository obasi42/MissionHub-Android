package com.missionhub.api;

import java.util.List;

import android.content.Context;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;

/**
 * Organizations API Helper
 * 
 * @see missionhub/app/controllers/api/organizations_controller.rb
 */
public class OrganizationsApi {

	/**
	 * Get all organizations of the current user
	 * 
	 * @param context
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("organizations");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Get a single organization
	 * 
	 * @param context
	 * @param organizationId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final long organizationId, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("organizations", String.valueOf(organizationId));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Get listed organizations
	 * 
	 * @param ctx
	 * @param organizationIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final List<Long> organizationIds, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("organizations", ApiHelper.toList(organizationIds));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}
}