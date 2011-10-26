package com.missionhub.auth;

import com.missionhub.R;
import com.missionhub.api.ApiClient;
import com.missionhub.api.ApiResponseHandler;
import com.missionhub.api.client.People;
import com.missionhub.api.json.GContact;
import com.missionhub.api.json.GPerson;
import com.missionhub.config.Preferences;
import com.missionhub.helpers.Flurry;
import com.missionhub.sql.convert.PersonJsonSql;
import com.missionhub.ui.DisplayError;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Auth {
	
	/* Logging Tag */
	public static final String TAG = Auth.class.getName();
	
	/* OAuth Access Token */
	private static String accessToken;
	
	/* User's Logged In State */
	private static boolean loggedIn = false;
	
	/* Message Constants */
	public static final int SUCCESS = 0;
	public static final int FAILURE = 1;
	public static final int RETRY = 2;
	
	/**
	 * Saves the auth state to a bundle
	 * 
	 * @param b The bundle
	 */
	public static synchronized void saveState(Bundle b) {
		b.putString("_accessToken", accessToken);
		b.putBoolean("_loggedIn", loggedIn);
	}

	/**
	 * Restores the auth state from a bundle
	 * 
	 * @param b The Bundle
	 */
	public static synchronized void restoreState(Bundle b) {
		accessToken = b.getString("_accessToken");
		loggedIn = b.getBoolean("_loggedIn");
	}
	
	/**
	 * Log User Out
	 * @param ctx Context
	 */
	public static synchronized void logout(Context ctx) {
		User.destroy();
		accessToken = null;
		loggedIn = false;
		Preferences.removeAccessToken(ctx);
		Preferences.removeOrganizationID(ctx);
	}
	
	private static ApiClient client;
	
	/**
	 * Check Stored Access Token
	 * @return true if has stored token
	 */
	public static synchronized boolean checkToken(final Context context, final Handler handler) {		
		setAccessToken(Preferences.getAccessToken(context));
		
		if (getAccessToken() != null) {
			ApiResponseHandler responseHandler = new ApiResponseHandler(GPerson[].class) {
				
				ProgressDialog mProgressDialog;
				
				@Override
				public void onStart() {
					mProgressDialog = ProgressDialog.show(context, "", context.getString(R.string.alert_logging_in), true);
					mProgressDialog.setCancelable(true);
					mProgressDialog.setOnCancelListener(new OnCancelListener(){
						@Override
						public void onCancel(DialogInterface dialog) {
							client.cancel(true);
							client = null;
						}
					});
				}
				
				@Override 
				public void onSuccess(Object jsonObject) {
					if (client == null)
						return;
					
					GPerson[] people = (GPerson[]) jsonObject; 
					try {
						if (people.length > 0) {
							GContact contact = new GContact();
							contact.setPerson(people[0]);
							User.setContact(contact);
							PersonJsonSql.update(context, contact);
							User.setOrganizationID(Preferences.getOrganizationID(context));
							Auth.setLoggedIn(true);
							handler.sendEmptyMessage(Auth.SUCCESS);
						}
					} catch (Exception e) {
						onFailure(e);
					}
					
				}
				@Override
				public void onFailure(Throwable e) {
					Log.e(TAG, "Auto Login Failed", e);
					handler.sendEmptyMessage(Auth.FAILURE);
					AlertDialog ad = DisplayError.display(context, e);
					ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							handler.sendEmptyMessage(Auth.RETRY);
						}
					});
					ad.show();
					Flurry.error(e, "Main.checkToken");
				}
				@Override
				public void onFinish() {
					mProgressDialog.dismiss();
					client = null;
				}
			};
			client = People.getMe(context, responseHandler);
			return true;
		}
		return false;
	}
	
	public static synchronized String getAccessToken() {
		return accessToken;
	}
	
	public static synchronized void setAccessToken(String t) {
		accessToken = t;
	}
	
	public static synchronized boolean isLoggedIn() {
		return loggedIn;
	}
	
	public static synchronized void setLoggedIn(boolean b) {
		loggedIn = b;
	}
}
