package com.missionhub.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Build;
import android.util.Log;
import com.loopj.android.http.*;
import com.missionhub.Application;
import com.missionhub.auth.Auth;
import com.missionhub.auth.User;
import com.missionhub.config.Config;

public class Api {
	public static final String TAG = "API";
	
	public static boolean getPeople(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/people/") + String.valueOf(id) + ".json";
		Log.i(TAG, "getPeople URL: " + url);
		RequestParams params = new RequestParams();
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("access_token", Auth.getAccessToken());
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean getPeople(ArrayList<Integer> ids, AsyncHttpResponseHandler responseHandler) {
		if (ids.size() > 0) {
			String url = getAbsoluteUrl("/people/") + buildIds(ids) + ".json";
			RequestParams params = new RequestParams();
			params.put("access_token", Auth.getAccessToken());
			params.put("org_id", String.valueOf(User.getOrganizationID()));
			get(url, params, responseHandler);
			return true;
		}
		return false;
	}
	
	public static boolean getPeople(String id, AsyncHttpResponseHandler responseHandler) {
		if (id.equalsIgnoreCase("me")) {
			String url = getAbsoluteUrl("/people/") + id + ".json";
			RequestParams params = new RequestParams();
			params.put("access_token", Auth.getAccessToken());
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
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params = sortFilterAssign(options,params);
		Log.i(TAG, params.toString());
		
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean getContacts(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/contacts/") + String.valueOf(id) + ".json";
		
		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean getContacts(ArrayList<Integer> ids, AsyncHttpResponseHandler responseHandler) {
		if (ids.size() > 0) {
			String url = getAbsoluteUrl("/contacts/") + buildIds(ids) + ".json";
			
			RequestParams params = new RequestParams();
			params.put("access_token", Auth.getAccessToken());
			params.put("org_id", String.valueOf(User.getOrganizationID()));
			
			get(url, params, responseHandler);
			return true;
		}
		return false;
	}
	
	public static boolean getFollowupComments(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/followup_comments/") + String.valueOf(id) + ".json";
		
		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		
		get(url, params, responseHandler);
		return true;
	}
	
	public static boolean postFollowupComment(int contact_id, int commenter_id, String status, String comment, AsyncHttpResponseHandler responseHandler, ArrayList<String> rejoicables) {
		String url = getAbsoluteUrl("/followup_comments.json");
		JSONObject jsonComment = new JSONObject();
		try {
			jsonComment.put("organization_id", User.getOrganizationID());
			jsonComment.put("contact_id", contact_id);
			jsonComment.put("commenter_id", commenter_id);
			jsonComment.put("comment", comment);
			jsonComment.put("status", status);
		}
		catch(Exception e) {
			Log.i(TAG, e.getMessage());
		}
		
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
		try {
			json.put("followup_comment", jsonComment);
			json.put("rejoicables", jsonRejoicables);
		}
		catch(Exception e) {
			Log.i(TAG, e.getMessage());
		}

		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("json", json.toString());
		
		post(url, params, responseHandler);
		
		return true;
	}
	
	public static boolean changeRole(String role, int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/roles/" + id + ".json");
		
		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("role", role);
		params.put("_method", "put");
		
		post(url, params, responseHandler);
		
		return true;
	}
	
	public static boolean createContactAssignment(int id, int assign_to, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/contact_assignments.json");
		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("assign_to", String.valueOf(assign_to));
		params.put("ids", String.valueOf(id));
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("organization_id", String.valueOf(User.getOrganizationID()));
		
		post(url, params, responseHandler);	
		return true;
	}
	
	public static boolean deleteContactAssignment(int id, AsyncHttpResponseHandler responseHandler) {
		String url = getAbsoluteUrl("/contact_assignments/") + String.valueOf(id) + ".json";
		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("_method", "delete");
		params.put("id", String.valueOf(id));
		
		post(url, params, responseHandler);
		return true;
	}
	
	public static boolean deleteComment(int id, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("access_token", Auth.getAccessToken());
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("_method", "delete");
		
		String url = getAbsoluteUrl("/followup_comments/") + String.valueOf(id) + ".json";
		
		post(url, params, responseHandler);
		return true;
	}
	
	public static String getSurveysUrl() {
		String url = Config.baseUrl + "/surveys";
		RequestParams params = new RequestParams();
		params.put("org_id", String.valueOf(User.getOrganizationID()));
		params.put("access_token", Auth.getAccessToken());
		try {
			params.put("platform", "android");
			params.put("platform_product", Build.PRODUCT);
			params.put("platform_release", android.os.Build.VERSION.RELEASE);
			params.put("app", Application.getVersion());
		} catch (Exception e) {}
		return url + '?' + params.toString();
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
			String key = iterator.next();
			if (key.equalsIgnoreCase("limit")) {
				params.put("limit", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("start")) {
				params.put("start", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("assigned_to_id")) {
				params.put("assigned_to", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("sort")) {
				params.put("sort", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("direction")) {
				params.put("direction", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("filters")) {
				params.put("filters", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("values")) {
				params.put("values", options.get(key));
				continue;
			}
			if (key.equalsIgnoreCase("term")) {
				params.put("term", options.get(key));
				continue;
			}
	   }
		return params;
	}
	
	private static AsyncHttpClient client = new AsyncHttpClient();

	private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		try {
			params.put("platform", "android");
			params.put("platform_product", Build.PRODUCT);
			params.put("platform_release", android.os.Build.VERSION.RELEASE);
			params.put("app", Application.getVersion());
		} catch (Exception e) {}
		Log.i(TAG, "Starting get at url:" + url);
		Log.i(TAG, "with params:" + params.toString());
	    client.get(url, params, responseHandler);
	}
	
	private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		try {
			params.put("platform", "android");
			params.put("platform_product", Build.PRODUCT);
			params.put("platform_release", android.os.Build.VERSION.RELEASE);
			params.put("app", Application.getVersion());
		} catch (Exception e) {}
		Log.i(TAG, "starting post at url:" + url);
		Log.i(TAG, "with params:" + params.toString());
      client.post(url, params, responseHandler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
	      return Config.apiUrl + relativeUrl;
	}
}
