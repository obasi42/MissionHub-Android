package com.missionhub.api;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.missionhub.api.convert.PersonJsonSql;
import com.missionhub.api.model.json.GMetaPerson;
import com.missionhub.api.model.json.GPerson;

public class People {

	/**
	 * Get a person and add them to the database
	 * 
	 * @param ctx
	 * @param personId
	 * @param tag
	 *            a tag to track the request
	 */
	public static void get(Context ctx, int personId, String tag) {
		get(ctx, personId, new PeopleNotifierResponseHandler(ctx, GMetaPerson.class, tag, "PEOPLE"));
	}

	/**
	 * Get a list of people and adds them to the database
	 * 
	 * @param ctx
	 * @param personIds
	 * @param tag
	 *            a tag to track the request
	 */
	public static void get(Context ctx, List<Integer> personIds, String tag) {
		People.get(ctx, personIds, new PeopleNotifierResponseHandler(ctx, GMetaPerson.class, tag, "PEOPLE"));
	}

	/**
	 * Get the currently logged in person (identified by access_token) and add
	 * them to the database
	 * 
	 * @param ctx
	 * @param tag
	 *            a tag to track the request
	 */
	public static void getMe(Context ctx, String tag) {
		People.getMe(ctx, new PeopleNotifierResponseHandler(ctx, GMetaPerson.class, tag, "PEOPLE"));
	}

	private static class PeopleNotifierResponseHandler extends ApiNotifierResponseHandler {

		public PeopleNotifierResponseHandler(Context ctx, Type t, String tag, String type) {
			super(ctx, t, tag, type);
		}

		@Override
		public void onSuccess(Object gMetaPerson) {
			GMetaPerson people = (GMetaPerson) gMetaPerson;
			for (GPerson person : people.getPeople()) {
				PersonJsonSql.update(ctx, person, tag);
			}
			super.onSuccess(gMetaPerson);
		}
	}

	/**
	 * Get a single person
	 * 
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, int personId, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("people", String.valueOf(personId));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}

	/**
	 * Get a list of people
	 * 
	 * @param ctx
	 * @param personIds
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient get(Context ctx, List<Integer> personIds, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("people", ApiHelper.toList(personIds));
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}

	/**
	 * Get the currently logged in person (identified by access_token)
	 * 
	 * @param ctx
	 * @param responseHandler
	 * @return
	 */
	public static ApiClient getMe(Context ctx, ApiResponseHandler responseHandler) {
		ApiClient client = new ApiClient(ctx);
		String url = ApiHelper.getAbsoluteUrl("people", "me");
		RequestParams params = ApiHelper.getDefaultRequestParams(ctx);
		client.get(url, params, responseHandler);
		return client;
	}
}