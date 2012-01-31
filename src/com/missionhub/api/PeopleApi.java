package com.missionhub.api;

import java.util.List;

import android.content.Context;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;

/**
 * People Api Helper
 * 
 * @see missionhub/app/controllers/api/people_controller.rb
 */
public class PeopleApi {

	/**
	 * Get a single person
	 * 
	 * @param context
	 * @param personId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final int personId, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("people", String.valueOf(personId));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Get a list of people
	 * 
	 * @param context
	 * @param personIds
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final List<Integer> personIds, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("people", ApiHelper.toList(personIds));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Get the currently logged in person (identified by access_token)
	 * 
	 * @param context
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest getMe(final Context context, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("people", "me");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Get a list of leaders for the current organization
	 * 
	 * @param context
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest getLeaders(final Context context, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("people", "leaders");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Get a list of leaders for the given organization
	 * 
	 * @param context
	 * @param organizationId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest getLeaders(final Context context, final int organizationId, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("people", "leaders");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		params.put("org_id", organizationId);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}
}