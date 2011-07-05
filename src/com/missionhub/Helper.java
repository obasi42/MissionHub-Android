package com.missionhub;

import java.util.Date;

import android.util.Log;

public class Helper {
	public static final String TAG = "HELPER";
	
	public static Date getDateFromUTCString(String s) {
		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
		java.util.Date date = null;
		s = s.replace("UTC", "");
		
		try {
			date = df.parse(s);
		} catch (Exception e) {
			Log.i(TAG, "date parse exception", e);
		}
		return date;
	}
	
}
