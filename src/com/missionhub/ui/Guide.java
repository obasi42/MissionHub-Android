package com.missionhub.ui;

import com.missionhub.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

public class Guide {
	
	public static final int CONTACTS_MY_CONTACTS = 0;
	public static final int CONTACTS_UNASSIGNED = 1;
	public static final int CONTACT = 2;
	
	public static final String PREFS_NAME = "MissionHubGuide";
	
	public static void display(Context ctx, int id) {
		if (dontShow(ctx, id)) return;
		
		final Context context = ctx;
		final int guideId = id;
		
		String title = "";
		String message = "";
		switch(guideId) {
			case CONTACTS_MY_CONTACTS:
				title = ctx.getString(R.string.guide_contacts);
				message = ctx.getString(R.string.guide_contacts_msg);
				break;
			case CONTACTS_UNASSIGNED:
				title = ctx.getString(R.string.guide_contacts_unassigned);
				message = ctx.getString(R.string.guide_contacts_unassigned_msg);
				break;
			case CONTACT:
				title = ctx.getString(R.string.guide_contact);
				message = ctx.getString(R.string.guide_contact_msg);
				break;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title)
				.setIcon(R.drawable.ic_dialog_info)
				.setMessage(message)
				.setPositiveButton(R.string.alert_hide, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setNegativeButton(R.string.alert_dont_show, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						setDontShow(context, guideId);
					}
				});
		AlertDialog ad = builder.create();
		ad.show();
	}
	
	public static boolean dontShow(Context ctx, int id) {
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean("guide_"+id, false);
	}
	
	public static boolean setDontShow(Context ctx, int id) {
		Log.i("GUIDE", "Set Never Show: " + id);
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("guide_"+id, true);
		return editor.commit();
	}
	
}