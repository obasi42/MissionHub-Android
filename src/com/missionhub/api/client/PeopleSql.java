package com.missionhub.api.client;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.ApiResponseHandler;
import com.missionhub.api.json.GMetaPerson;
import com.missionhub.api.json.GPerson;
import com.missionhub.sql.convert.PersonJsonSql;

public class PeopleSql {
	
	/**
	 * Get a single person and adds them to the sql database
	 * @param ctx
	 * @param personId
	 * @param responseHandler
	 * @return
	 */
	public static void get(Context ctx, int personId, String tag) {
		People.get(ctx, personId, new PeopleSqlApiResponseHandler(ctx, tag));
	}
	
	/**
	 * Get a list of people and adds them to the sql database
	 * @param ctx
	 * @param personIds
	 * @param responseHandler
	 * @return
	 */
	public static void get(Context ctx, List<Integer> personIds, String tag) {
		People.get(ctx, personIds, new PeopleSqlApiResponseHandler(ctx, tag));
	}
	
	/**
	 * Get the currently logged in person (identified by access_token) and adds them to the sql database
	 * @param ctx
	 * @param responseHandler
	 * @return
	 */
	public static void getMe(Context ctx, String tag) {
		People.getMe(ctx, new PeopleSqlApiResponseHandler(ctx, tag));
	}
	
	private static class PeopleSqlApiResponseHandler extends ApiResponseHandler {

		Application app;
		Context ctx;
		String tag;
		
		public PeopleSqlApiResponseHandler(Context ctx, String tag) {
			super(GMetaPerson.class);
			this.ctx = ctx;
			app = (Application) ctx.getApplicationContext();
			this.tag = tag;
		}
		
		@Override 
		public void onStart() {
			super.onStart();
			Bundle b = new Bundle();
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(Type.JSON_PEOPLE_ON_START, b);			
		}
		
		@Override
		public void onSuccess(Object gMetaPerson) {		
			GMetaPerson personMeta = (GMetaPerson) gMetaPerson;
			GPerson[] people = personMeta.getPeople();
			try {
				for (GPerson person : people) {
					PersonJsonSql.update(ctx, person, tag);
				}
				Bundle b = new Bundle();
				if (tag != null) b.putString("tag", tag);
				app.getApiNotifier().postMessage(Type.JSON_PEOPLE_ON_SUCCESS, b);
			} catch (Exception e) {
				onFailure(e);
			}
		}
		
		@Override
		public void onFailure(Throwable e) {			
			Bundle b = new Bundle();
			if (tag != null) b.putString("tag", tag);
			b.putSerializable("throwable", e);
			app.getApiNotifier().postMessage(Type.JSON_PEOPLE_ON_FAILURE, b);
		}
		
		@Override
		public void onFinish() {
			Bundle b = new Bundle();
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(Type.JSON_PEOPLE_ON_FINISH, b);
		}
	}
	
}