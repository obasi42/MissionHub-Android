package com.missionhub.api;

import android.content.Context;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;

/**
 * Roles API Helper
 * 
 * @see missionhub/app/controllers/api/roles_controller.rb
 */
public class RolesApi {

	/**
	 * update a person's role
	 * 
	 * @param ctx
	 * @param personId
	 * @param organizationId
	 * @param role
	 * @param responseHandler
	 * @return
	 */
	public static ApiRequest update(final Context context, final long personId, final int organizationId, final String role, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("roles", String.valueOf(personId));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		params.put("role", role);
		params.put("org_id", organizationId);
		params.put("_method", "put");
		return new ApiRequest(client, client.post(url, headers, params, apiHandler));
	}

}