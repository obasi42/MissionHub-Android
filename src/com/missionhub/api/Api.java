package com.missionhub.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import com.loopj.android.http.*;
import com.missionhub.Config;
import com.missionhub.LoginActivity;

public class Api {
	public static final String TAG = "API";
	
	public static boolean getPeople(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/people/") + String.valueOf(id) + ".json";
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean getPeople(ArrayList<Integer> ids, AsyncHttpResponseHandler responseHandler) {
		if (ids.size() > 0) {
			String url = getAbsoluteUrl("/people/") + buildIds(ids) + ".json";
			RequestParams params = new RequestParams();
			params.put("access_token", LoginActivity.token);
			get(url, params, responseHandler);
			return true;
		}
		return false;
	}
	
	public static boolean getPeople(String id, AsyncHttpResponseHandler responseHandler) {
		if (id.equalsIgnoreCase("me")) {
			String url = getAbsoluteUrl("/people/") + id + ".json";
			
			RequestParams params = new RequestParams();
			params.put("access_token", LoginActivity.token);
			get(url, params, responseHandler);
			return true;
		}
		return false;
	}
	
	public static boolean getContactsList(HashMap<String,String> options, AsyncHttpResponseHandler responseHandler) {
		String url = "";
		if (options.containsKey("term")) {
			url = getAbsoluteUrl("/contacts/search.json");
		} else {
			url = getAbsoluteUrl("/contacts.json");
		}

		
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		params = sortFilterAssign(options,params);
		
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean getContacts(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/contacts/") + String.valueOf(id) + ".json";
		
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean getContacts(ArrayList<Integer> ids, AsyncHttpResponseHandler responseHandler) {
		if (ids.size() > 0) {
			String url = getAbsoluteUrl("/contacts/") + buildIds(ids) + ".json";
			
			RequestParams params = new RequestParams();
			params.put("access_token", LoginActivity.token);
			
			get(url, params, responseHandler);
			return true;
		}
		return false;
	}
	
	public static boolean getFollowupComment(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/followup_comments/") + String.valueOf(id) + ".json";
		
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean postFollowupComment(int contact_id, int commenter_id, String status, String comment, AsyncHttpResponseHandler responseHandler, ArrayList<String> rejoicables) {
		String url = getAbsoluteUrl("/followup_comments.json");
		JSONObject jsonComment = new JSONObject();
		jsonComment.put("orgization_id", ORGID);
		jsonComment.put("contact_id", contact_id);
		jsonComment.put("commenter_id", commenter_id);
		jsonComment.put("comment", comment);
		jsonComment.put("status", status);
		
		JSONObject json = new JSONObject();
		JSONArray jsonRejoicables = new JSONArray();
		if (rejoicables.contains("spiritual_conversation")) {
			jsonRejoicables.put("spiritual_conversation");
		}
		if (rejoicables.contains("gospel_presentation")) {
			jsonRejoicables.put("gospel_presentation");			
		}
		if (rejoicables.contains("prayed_to_receive")) {
			jsonRejoicables.put("prayed_to_receive");
		}
		json.put("followup_comment", jsonComment);
		json.put("rejoicables", jsonRejoicables);
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		params.put("json", json.toString());
		Log.i(TAG, json.toString());
		
		post(url, params, responseHandler);
		
		return true;
	}
	
	public static boolean createContactAssignment(int id, int assign_to, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/contact_assignments.json");
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		params.put("assign_to", String.valueOf(assign_to));
		params.put("ids", String.valueOf(id));
		params.put("org_id", ORGID);
		params.put("organization_id", ORGID);
		
		post(url, params, responseHandler);	
		return true;
	}
	
	public static boolean deleteContactAssignment(int id, int assign_to, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/contact_assignments/") + String.valueOf(id) + ".json";
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		params.put("org_id", ORGID);
		params.put("_method", "delete");
		
		post(url, params, responseHandler);
		return true;
	}
	
	public static boolean deleteComment(int id, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("access_token", LoginActivity.token);
		params.put("org_id", ORGID);
		params.put("_method", "delete");
		
		String url = getAbsoluteUrl("/followup_comments/") + String.valueOf(id) + ".json";
		
		post(url, params, responseHandler);
	}
	
	private static String buildIds(ArrayList<Integer> ids) {
		String listOfIds = "";
		Iterator<Integer> itr = ids.iterator();
		while (itr.hasNext()) {
			String element = String.valueOf(itr.next());
			listOfIds += element;
			if (itr.hasNext()) {
				listOfIds += ",";
			}
		}
		return listOfIds;
	}
	
	private static RequestParams sortFilterAssign(HashMap<String,String> options, RequestParams params) {
		Iterator<String> iterator = options.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			System.out.println("Error " + key + " means " + options.get(key));
			if (key.equalsIgnoreCase("limit")) {
				params.put("limit", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("start")) {
				params.put("start", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("assigned_to_id")) {
				params.put("assigned_to", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("sort")) {
				params.put("sort", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("direction")) {
				params.put("direction", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("filters")) {
				params.put("filters", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("values")) {
				params.put("values", options.get(key));
				break;
			}
			if (key.equalsIgnoreCase("term")) {
				params.put("term", options.get(key));
				break;
			}
	   }
		return params;
	}
	
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
	      return Config.apiUrl + relativeUrl;
	}
}
