package com.missionhub.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.RequestParams;

public class FollowupComments {
	
	public static final String TAG = FollowupComments.class.getSimpleName();
	
	/**
	 * Get a list of followup comments for a person
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, int personId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("followup_comments", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Post a new followup comment
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient post(Context ctx, FollowupComments.Comment comment, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("followup_comments");
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		params.put("json", comment.getJson());
		client.post(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Delete a single followup comment
	 * @param ctx
	 * @param commentId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient delete(Context ctx, int commentId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("followup_comments", String.valueOf(commentId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		params.put("_method", "delete");
		client.post(url, params, responseHandler);
		return client;
	}
	
	/**
	 * Delete a list of followup comments
	 * @param ctx
	 * @param commentIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient delete(Context ctx, List<Integer> commentIds, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("followup_comments", ApiHelper.toList(commentIds));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		params.put("_method", "delete");
		client.post(url, params, responseHandler);
		return client;
	}
	
	/**
	 * A followup Comment
	 */
	public static class Comment {
		
		private int personId;
		private int commenterId;
		private int organizationId;
		private String status;
		private String comment;
		private List<String> rejoicables;
		
		public Comment(int personId, int commenterId, int organizationId, String status, String comment) {
			this(personId, commenterId, organizationId, status, comment, new ArrayList<String>());
		}
		
		public Comment(int personId, int commenterId, int organizationId, String status, String comment, List<String> rejoicables) {
			this.personId = personId;
			this.commenterId = commenterId;
			this.organizationId = organizationId;
			this.status = status;
			this.comment = comment;
			this.rejoicables = rejoicables;
		}
		
		public String getJson() {
			JSONObject jsonComment = new JSONObject();
			try {
				jsonComment.put("organization_id", organizationId);
				jsonComment.put("contact_id", personId);
				jsonComment.put("commenter_id", commenterId);
				jsonComment.put("comment", comment);
				jsonComment.put("status", status);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			}
			
			JSONArray jsonRejoicables = new JSONArray();
			Iterator<String> itr = rejoicables.iterator();
			while(itr.hasNext()) {
				jsonRejoicables.put(itr.next());
			}
			
			JSONObject json = new JSONObject();
			try {
				json.put("followup_comment", jsonComment);
				json.put("rejoicables", jsonRejoicables);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			}
			
			return json.toString();
		}
	}	
}