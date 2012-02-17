package com.missionhub.api;

import android.content.Context;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;

/**
 * Contact Assignment API Helper
 * 
 * @see missionhub/app/controllers/api/contact_assignments_controller.rb
 */
public class ContactAssignmentApi {

	/** Constant for UNKNOWN currentLeaderId */
	public static final int UNKNOWN = -98;

	/** Constant for NONE currentLeaderId */
	public static final int NONE = -99;

	/** Type of assignment */
	public static enum Type {
		leader, organization
	}

	/**
	 * Assigns a contact to a leader
	 * 
	 * @param context
	 * @param personId
	 * @param leaderId
	 * @param currentLeaderId
	 *            leader id, ContactAssignmentApi.NONE, or
	 *            ContactAssignmentApi.UNKNOWN
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest assignToLeader(final Context context, final long personId, final int leaderId, final int currentLeaderId, final ApiHandler apiHandler) {
		return create(context, Type.leader, personId, leaderId, currentLeaderId, apiHandler);
	}

	/**
	 * Assigns a contact to an organization, removes all current leader
	 * assignments
	 * 
	 * @param context
	 * @param personId
	 * @param organizationId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest assignToOrganization(final Context context, final long personId, final int organizationId, final ApiHandler apiHandler) {
		return create(context, Type.organization, personId, organizationId, UNKNOWN, apiHandler);
	}

	/**
	 * Unassigns a contact from a leader
	 * 
	 * @param context
	 * @param personId
	 * @param currentLeaderId
	 *            leader id, ContactAssignmentApi.NONE, or
	 *            ContactAssignmentApi.UNKNOWN
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest unassignFromLeader(final Context context, final long personId, final int currentLeaderId, final ApiHandler apiHandler) {
		return create(context, Type.leader, personId, NONE, currentLeaderId, apiHandler);
	}

	/**
	 * Add a contact assignment
	 * 
	 * @param context
	 * @param type
	 * @param personId
	 * @param assignToId
	 * @param currentLeaderId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest create(final Context context, final Type type, final long personId, final int assignToId, final int currentLeaderId, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("contact_assignments");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);

		params.put("type", type);
		params.put("id", personId);
		String assignToIdString = "none";
		if (assignToId != NONE) {
			assignToIdString = String.valueOf(assignToId);
		}
		params.put("assign_to_id", assignToIdString);
		if (currentLeaderId != UNKNOWN) {
			String currentLeaderIdString = "none";
			if (currentLeaderId != NONE) {
				currentLeaderIdString = String.valueOf(currentLeaderId);
			}
			params.put("current_assign_to_id", currentLeaderIdString);
		}

		return new ApiRequest(client, client.post(url, headers, params, apiHandler));
	}

}