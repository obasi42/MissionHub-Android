package com.missionhub.helpers;

import java.util.Date;
import java.util.HashMap;

import com.missionhub.R;

import android.util.Log;

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
	    phoneNumber.replaceAll("[^0-9]", "");
	 
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
}