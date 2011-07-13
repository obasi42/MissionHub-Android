package com.missionhub.auth;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.GContact;
import com.missionhub.api.GError;
import com.missionhub.api.GPerson;
import com.missionhub.config.Preferences;
import com.missionhub.error.MHException;
import com.missionhub.helpers.Flurry;
import com.missionhub.ui.DisplayError;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Auth {
	
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
	
	/**
	 * Check Stored Access Token
	 * @return true if has stored token
	 */
	public static synchronized boolean checkToken(Context context, Handler handler) {
		final Handler h = handler;
		final Context ctx = context;
		
		setAccessToken(Preferences.getAccessToken(ctx));
		
		if (getAccessToken() != null) {
			AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
				
				ProgressDialog mProgressDialog;
				
				@Override
				public void onStart() {
					mProgressDialog = ProgressDialog.show(ctx, "", ctx.getString(R.string.alert_logging_in), true);
				}
				@Override
				public void onSuccess(String response) {
					Gson gson = new Gson();
					try{
						GError error = gson.fromJson(response, GError.class);
						onFailure(new MHException(error));
					} catch (Exception out){
						try {
							GPerson[] people = gson.fromJson(response, GPerson[].class);
							if (people.length > 0) {
								GContact contact = new GContact();
								contact.setPerson(people[0]);
								User.setContact(contact);
								User.setOrganizationID(Preferences.getOrganizationID(ctx));
								Auth.setLoggedIn(true);
								h.sendEmptyMessage(Auth.SUCCESS);
							}
						} catch(Exception e) {
							onFailure(e);
						}
					}
				}
				@Override
				public void onFailure(Throwable e) {
					Log.e(TAG, "Auto Login Failed", e);
					h.sendEmptyMessage(Auth.FAILURE);
					AlertDialog ad = DisplayError.display(ctx, e);
					ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							h.sendEmptyMessage(Auth.RETRY);
						}
					});
					ad.show();
					Flurry.error(e, "Main.checkToken");
				}
				@Override
				public void onFinish() {
					mProgressDialog.dismiss();
				}
			};
			Api.getPeople("me", responseHandler);
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
