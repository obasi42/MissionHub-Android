package com.missionhub;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.missionhub.api.Api;
import com.missionhub.api.GContact;
import com.missionhub.api.GContactAll;
import com.missionhub.api.GError;
import com.missionhub.api.GFCTop;
import com.missionhub.api.GPerson;
import com.missionhub.api.MHError;
import com.missionhub.api.User;
import com.missionhub.ui.DisplayError;

public class MissionHubActivity extends Activity {
	
	public static final String TAG = "MissionHubActivity";
	public static final String TAG2 = "API";
	final Handler mHandler = new Handler();
	private ProgressDialog mProgressDialog;
	
	private LinearLayout loggedOut;
	private LinearLayout loggedIn;
	private RelativeLayout logoutBar;
	private TextView txtLogoutbarName;
	
	public final int LOGIN_WINDOW_ACTIVITY = 0;
	
	public static final String PREFS_NAME = "MissionHubPrivate";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		testingApi();
		
		loggedOut = (LinearLayout) findViewById(R.id.loggedout);
		loggedIn = (LinearLayout) findViewById(R.id.loggedin);
		logoutBar = (RelativeLayout) findViewById(R.id.logoutbar);
		txtLogoutbarName = (TextView) findViewById(R.id.txt_logoutbar_name);
		
		if (Config.debug) {
			User.token = getStoredToken();
			User.orgID = "56";
			User.isLoggedIn = true;
			refreshView();
		} else if (!checkToken()) {
			refreshView();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_WINDOW_ACTIVITY && resultCode == RESULT_OK) {
			Log.i(TAG, data.getStringExtra("token"));
			refreshView();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}

	public void clickAbout(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_learn_more)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(R.string.alert_learn_more_msg)
				.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Uri uri = Uri.parse("http://missionhub.com?mobile=0");
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
				})
				.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void clickLogin(View view) {
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, LOGIN_WINDOW_ACTIVITY);
	}

	public void clickContacts(View view) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}
	
	public void clickLogout(View view) {
		User.token = null;
		User.isLoggedIn = false;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("token");
		editor.commit();
		refreshView();
	}
	
	public void clickProfile(View view) {
		
	}	
	
	/**
	 * Refreshes the main view based on user's logged in status
	 */
	public void refreshView() {
		if (User.isLoggedIn) {
			loggedOut.setVisibility(View.GONE);
			loggedIn.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.VISIBLE);
			txtLogoutbarName.setText("My Name");
		} else {
			loggedIn.setVisibility(View.GONE);
			loggedOut.setVisibility(View.VISIBLE);
			logoutBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Returns a users stored access token
	 * @return stored access token
	 */
	public String getStoredToken() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getString("token", null);
	}
	
	/**
	 * Check Stored Access Token
	 * @return true if has stored token
	 */
	private boolean checkToken() {
		User.token = getStoredToken();
		User.isLoggedIn = false;
		if (User.token != null && !User.token.equalsIgnoreCase("")) {
			AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					mProgressDialog = ProgressDialog.show(MissionHubActivity.this, "", MissionHubActivity.this.getString(R.string.alert_logging_in), true);
				}
				@Override
				public void onSuccess(String response) {
					Gson gson = new Gson();
					try{
						GError error = gson.fromJson(response, GError.class);
						onFailure(new MHError(error));
					} catch (Exception out){
						try {
							GPerson[] people = gson.fromJson(response, GPerson[].class);
							if (people.length > 0) {
								User.contact = new GContact();
								User.contact.setPerson(people[0]);
								User.isLoggedIn = true;
								refreshView();
							}
						} catch(Exception e) {
							onFailure(e);
						}
					}
				}
				@Override
				public void onFailure(Throwable e) {
					Log.e(TAG, "Auto Login Failed", e);
					AlertDialog ad = DisplayError.display(MissionHubActivity.this, e);
					ad.setButton(ad.getContext().getString(R.string.alert_retry), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							checkToken();
						}
					});
					ad.show();
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
	
	public void testingApi() {
		User.token = "43941a348dbb0b6c6e88763338baa5bedc08ddaa3c139106c700b8a45e1e8205";
		User.orgID = "1825";
		
		AsyncHttpResponseHandler responseHandler2 = new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				Log.i(TAG2, "Starting");
			}
			
			@Override
			public void onSuccess(String response) {
				Log.i(TAG2, "SUCCESS!");
				Log.i(TAG2, response);
				Gson gson = new Gson();
				
				
//				try {
//					GFCTop[] fcs = gson.fromJson(response, GFCTop[].class);
//					Log.i(TAG2, fcs[0].getFollowup_comment().getRejoicables()[0].getWhat());
//					Log.i(TAG2, fcs[0].getFollowup_comment().getComment().getStatus());
//				} catch(Exception e) {
//					Log.i(TAG2, "CRAP", e);
//				}
				
				try {
					GContactAll contact = gson.fromJson(response, GContactAll.class);
//					Log.i(TAG2, contact.getKeywords()[0].getName());
//					Log.i(TAG2, String.valueOf(contact.getKeywords()[0].getQuestions().length));
//					Log.i(TAG2, contact.getQuestions()[0].getLabel());
					Log.i(TAG2, contact.getPeople()[0].getPerson().getName());
//					Log.i(TAG2, contact.getPeople()[0].getForm()[0].getA());
					Log.i(TAG2, "Coming up next... valid roles");
					User.calculateRoles(contact.getPeople()[0].getPerson());
					Log.i(TAG2, String.valueOf(User.primaryOrgId));
				} catch (Exception e) {
					Log.i(TAG2, "CRAP: ", e);
				}

				
//				try{
//					GPerson[] peeps = gson.fromJson(response, GPerson[].class);
//					Log.i(TAG2, peeps[0].getName());
//					
////					GContact[] contacts = gson.fromJson(response, GContact[].class);
////					Log.i(TAG2, contacts[0].getPerson().getName());
////					Log.i(TAG2, contacts[0].getPerson().getPicture());
////					Log.i(TAG2, contacts[0].getPerson().getStatus());
//					
////					try {
////						Log.i(TAG2, peeps[0].getLocation().getName());
////						Log.i(TAG2, peeps[0].getAssignment().getAssigned_to_person()[0].getName());
////						Log.i(TAG2, peeps[0].getOrganizational_memebership()[0].getName());
////						Log.i(TAG2, peeps[0].getOrganizational_roles()[0].getOrg_id());
////						Log.i(TAG2, peeps[0].getEducation()[1].getConcentration()[0].getName());
////						
////					} catch(Exception e) {
////						Log.i(TAG2, "ARGGG", e);
////					}
//
//					
//				} catch(Exception e) {
//					Log.i(TAG2, "CRAP", e);
//				}

				
			}
			
			@Override
			public void onFailure(Throwable e) {
				Log.i(TAG2, e.toString());
				Log.i(TAG2, "stuff");
			}
			
			@Override
			public void onFinish() {
				Log.i(TAG2, "I'm finished!");
			}
		};
		//Api.getPeople(1282204, responseHandler2);
//		Api.getPeople("me", responseHandler);
//		
//		ArrayList<Integer> ids = new ArrayList<Integer>();
//		ids.add(1282204);
//		ids.add(244771);
//		Api.getPeople(ids, responseHandler);
//		
		HashMap<String,String> options = new HashMap<String,String>();
		options.put("limit","15");
		options.put("start", "0");
		options.put("assigned_to_id", "none");
		options.put("sort", "time");
		options.put("direction", "ASC");
//		Api.getContactsList(options, responseHandler2);
		
		Api.getContacts(1282204, responseHandler2);
		
		
//		ArrayList<String> rejoicables = new ArrayList<String>();
//		rejoicables.add("gospel_presentation");
//		
//		Api.postFollowupComment(1282204, 244771, "completed", "Hi from the Android API", responseHandler, rejoicables);
//	
//		Api.createContactAssignment(244771, 1282204, responseHandler);
		
//		Api.deleteContactAssignment(244771, responseHandler2);
		
//		Api.deleteComment(194, responseHandler);
//		Api.getFollowupComments(1282204, responseHandler2);	
	}
}
