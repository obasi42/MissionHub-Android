package com.missionhub.api;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.missionhub.api.convert.OrganizationJsonSql;
import com.missionhub.api.model.json.GMetaOrganization;
import com.missionhub.api.model.json.GOrganization;

public class Organizations {

	/**
	 * Get all organizations of the current user
	 * 
	 * @param ctx
	 * @param tag
	 */
	public static void get(Context ctx, String tag) {
		get(ctx, new OrganizationResponseHandler(ctx, GMetaOrganization.class, tag, "ORGANIZATIONS"));
	}

	/**
	 * Get a single organization
	 * 
	 * @param ctx
	 * @param organizationId
	 * @param tag
	 */
	public static void get(Context ctx, int organizationId, String tag) {
		get(ctx, organizationId, new OrganizationResponseHandler(ctx, GMetaOrganization.class, tag, "ORGANIZATIONS"));
	}

	/**
	 * Get listed organizations
	 * 
	 * @param ctx
	 * @param organizationIds
	 * @param tag
	 */
	public static void get(Context ctx, List<Integer> organizationIds, String tag) {
		get(ctx, organizationIds, new OrganizationResponseHandler(ctx, GMetaOrganization.class, tag, "ORGANIZATIONS"));
	}

	private static class OrganizationResponseHandler extends ApiNotifierResponseHandler {
		public OrganizationResponseHandler(Context ctx, Type t, String tag, String type) {
			super(ctx, t, tag, type);
		}

		@Override
		public void onSuccess(Object gMetaOrganization) {
			GMetaOrganization metaOrgs = (GMetaOrganization) gMetaOrganization;
			for (GOrganization org : metaOrgs.getOrganizations()) {
				OrganizationJsonSql.update(ctx, org, tag);
			}
			super.onSuccess(gMetaOrganization);
		}
	}

	/**
	 * Get all organizations of the current user
	 * 
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
	 * 
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
	 * 
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