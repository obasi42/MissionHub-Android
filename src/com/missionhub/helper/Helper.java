package com.missionhub.helper;

import java.util.Date;
import java.util.HashMap;

import com.missionhub.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Helper {
	
	public static final String TAG = "HELPER";
	
	public static final HashMap<String, Integer> statusMap = new HashMap<String, Integer>();
	static {
		statusMap.put("attempted_contact", R.string.status_attempted_contact);
		statusMap.put("uncontacted", R.string.status_uncontacted);
		statusMap.put("do_not_contact", R.string.status_do_not_contact);
		statusMap.put("contacted", R.string.status_contacted);
		statusMap.put("completed", R.string.status_completed);
	}
	
	public static Date getDateFromUTCString(String s) {
		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
		s = s.replace("UTC", "");
		
		try {
			return df.parse(s);
		} catch (Exception e) {
			Log.w(TAG, "date parse exception", e);
		}
		return new Date();
	}
	
	public static String formatPhoneNumber(String phoneNumber)
	{
	    String fNum = null;
	    phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
	 
	    if(11 == phoneNumber.length())
	    {
	        fNum = "+" + phoneNumber.substring(0, 1);
	        fNum += " (" + phoneNumber.substring(1, 4) + ")";
	        fNum += " " + phoneNumber.substring(4, 7);
	        fNum += "-" + phoneNumber.substring(7, 11);
	    }
	    else if(10 == phoneNumber.length())
	    {
	        fNum = "(" + phoneNumber.substring(0, 3) + ")";
	        fNum += " " + phoneNumber.substring(3, 6);
	        fNum += "-" + phoneNumber.substring(6, 10);
	    }
	    else if(7 == phoneNumber.length())
	    {
	        fNum = phoneNumber.substring(0, 3);
	        fNum += "-" + phoneNumber.substring(4, 7);
	    }
	    else
	    {
	        return "Invalid Phone Number.";
	    }
	 
	    return fNum;
	}
	
	public static synchronized int getStatusResourceId(String s) {
		return statusMap.get(s);
	}
	
	public static boolean hasPhoneAbility(Context ctx) {
		TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
			return false;

		return true;
	}
	
	public static void openFacebookProfile(final Context ctx, String uid) {
		final String new_uid = uid;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.contact_open_profile)
		       .setCancelable(true)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           @Override
				public void onClick(DialogInterface dialog, int id) {
		        	   try {
		        		   Intent intent = new Intent(Intent.ACTION_VIEW);
		        		   intent.setClassName("com.facebook.katana", "com.facebook.katana.ProfileTabHostActivity");
		        		   intent.putExtra("extra_user_id", Long.parseLong(new_uid));
		        		   ctx.startActivity(intent);
		        	   } catch(Exception e) {
		        		   try {
				       			Intent i = new Intent(Intent.ACTION_VIEW);
				       			i.setData(Uri.parse("http://www.facebook.com/profile.php?id=" + new_uid));
				       			ctx.startActivity(i);
				       		} catch(Exception f) {
				       			Toast.makeText(ctx, R.string.contact_cant_open_profile, Toast.LENGTH_LONG).show();
				       		}
		        	   }
		        	   dialog.dismiss();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           @Override
				public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static void openURL(final Context ctx, String url) {
		final String new_url = url;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(R.string.contact_open_url)
		       .setCancelable(true)
		       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		           @Override
				public void onClick(DialogInterface dialog, int id) {
		        	   try {
		       			Intent i = new Intent(Intent.ACTION_VIEW);
		       			i.setData(Uri.parse(new_url));
		       			ctx.startActivity(i);
		       		} catch(Exception e) {
		       			Toast.makeText(ctx, R.string.contact_cant_open_profile, Toast.LENGTH_LONG).show();
		       		}
		        	   dialog.dismiss();
		           }
		       })
		       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           @Override
				public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static void makePhoneCall(final Context ctx, String phoneNumber) {
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + phoneNumber));
			ctx.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(ctx, R.string.contact_cant_call, Toast.LENGTH_LONG).show();
		}
	}
	
	public static void sendSMS(final Context ctx, String phoneNumber) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("address", phoneNumber);
			intent.setType("vnd.android-dir/mms-sms");
			ctx.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(ctx, R.string.contact_cant_sms, Toast.LENGTH_LONG).show();
		}
	}
	
	public static void sendEmail(final Context ctx, String emailAddress) {
		try {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { emailAddress });
			ctx.startActivity(Intent.createChooser(emailIntent, ctx.getString(R.string.contact_send_email)));
		} catch (Exception e) {
			Toast.makeText(ctx, R.string.contact_cant_email, Toast.LENGTH_LONG).show();
		}
	}
}
