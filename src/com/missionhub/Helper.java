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
	
}
