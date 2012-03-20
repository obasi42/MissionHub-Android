package com.missionhub.util;

import java.util.Arrays;
import java.util.Date;

import android.util.Log;

public class U {
	
	/** logging tag */
	public static final String TAG = U.class.getSimpleName();

	/**
	 * Checks if a long array contains a value
	 * @param values
	 * @param value
	 * @return
	 */
	public static boolean contains(final long[] values, final long value) {
		Arrays.sort(values);
		return Arrays.binarySearch(values, value) >= 0;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
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

}