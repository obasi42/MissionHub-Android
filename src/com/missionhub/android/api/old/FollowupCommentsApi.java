package com.missionhub.android.api.old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.cr_wd.android.network.HttpHeaders;
import com.cr_wd.android.network.HttpParams;
import com.missionhub.android.ui.Rejoicable;

/**
 * Followup Comments API Helper
 * 
 * @see missionhub/app/controlers/api/followup_comments_controller.rb
 */
public class FollowupCommentsApi {

	public static final String TAG = FollowupCommentsApi.class.getSimpleName();

	/**
	 * Get a list of followup comments for a person
	 * 
	 * @param context
	 * @param personId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final long personId, final ApiHandler apiHandler) {
		return get(context, personId, -1, -1, apiHandler);
	}

	/**
	 * Get a list of followup comments for a person
	 * 
	 * @param context
	 * @param personId
	 * @param since
	 * @param until
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest get(final Context context, final long personId, final int since, final int until, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("followup_comments", String.valueOf(personId));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		if (since >= 0) {
			params.put("since", since);
		}
		if (until >= 0) {
			params.put("until", until);
		}
		return new ApiRequest(client, client.get(url, headers, params, apiHandler));
	}

	/**
	 * Post a new followup comment
	 * 
	 * @param context
	 * @param personId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest post(final Context context, final FollowupCommentsApi.Comment comment, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("followup_comments");
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		params.put("json", comment.getJson());
		return new ApiRequest(client, client.post(url, headers, params, apiHandler));
	}

	/**
	 * Delete a single followup comment
	 * 
	 * @param context
	 * @param commentId
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest delete(final Context context, final int commentId, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("followup_comments", String.valueOf(commentId));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		params.put("_method", "delete");
		return new ApiRequest(client, client.post(url, headers, params, apiHandler));
	}

	/**
	 * Delete a list of followup comments
	 * 
	 * @param context
	 * @param commentIds
	 * @param apiHandler
	 * @return
	 */
	public static ApiRequest delete(final Context context, final List<Long> commentIds, final ApiHandler apiHandler) {
		final ApiClient client = new ApiClient();
		final String url = ApiHelper.getAbsoluteUrl("followup_comments", ApiHelper.toList(commentIds));
		final HttpHeaders headers = ApiHelper.getDefaultHeaders(context);
		final HttpParams params = ApiHelper.getDefaultParams(context);
		params.put("_method", "delete");
		return new ApiRequest(client, client.post(url, headers, params, apiHandler));
	}

	/**
	 * A followup Comment
	 */
	public static class Comment {

		private final long personId;
		private final int commenterId;
		private final int organizationId;
		private final String status;
		private final String comment;
		private final List<Rejoicable> rejoicables;

		public Comment(final long personId, final int commenterId, final int organizationId, final String status, final String comment) {
			this(personId, commenterId, organizationId, status, comment, new ArrayList<Rejoicable>());
		}

		public Comment(final long personId, final int commenterId, final int organizationId, final String status, final String comment, final ArrayList<Rejoicable> rejoicables) {
			this.personId = personId;
			this.commenterId = commenterId;
			this.organizationId = organizationId;
			this.status = status;
			this.comment = comment;
			this.rejoicables = rejoicables;
		}

		public String getJson() {
			final JSONObject jsonComment = new JSONObject();
			try {
				jsonComment.put("organization_id", organizationId);
				jsonComment.put("contact_id", personId);
				jsonComment.put("commenter_id", commenterId);
				jsonComment.put("comment", comment);
				jsonComment.put("status", status);
			} catch (final JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			}

			final JSONArray jsonRejoicables = new JSONArray();
			final Iterator<Rejoicable> itr = rejoicables.iterator();
			while (itr.hasNext()) {
				jsonRejoicables.put(itr.next().tag);
			}

			final JSONObject json = new JSONObject();
			try {
				json.put("followup_comment", jsonComment);
				json.put("rejoicables", jsonRejoicables);
			} catch (final JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			}

			return json.toString();
		}
	}
}