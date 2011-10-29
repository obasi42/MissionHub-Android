package com.missionhub.api;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.missionhub.Application;

public class ContactAssignment {

	/**
	 * Add a contact assignment
	 * @param ctx
	 * @param personId
	 * @param assignToPersonId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient create(Context ctx, int personId, int assignToPersonId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("contact_assignments");
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		params.put("assign_to", String.valueOf(assignToPersonId));
		params.put("ids", String.valueOf(personId));
		params.put("organization_id", String.valueOf(((Application) ctx.getApplicationContext()).getUser().getOrganizationID()));
		client.post(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Delete a contact assignment
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient delete(Context ctx, int personId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("contact_assignments", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		params.put("_method", "delete");
		params.put("id", String.valueOf(personId));
		client.post(url, params, responseHandler);
		return client;
	}
}