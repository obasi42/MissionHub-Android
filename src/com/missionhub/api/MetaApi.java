package com.missionhub.api;

import android.content.Context;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;

/**
 * Meta Api Helper
 * 
 * @see missionhub/app/controllers/api/meta_controller.rb
 */
public class MetaApi {

	/**
	 * Get the meta for the logged in user
	 * 
	 * @param context
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("meta");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

}