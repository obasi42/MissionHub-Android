package com.missionhub.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.xml.datatype.DatatypeFactory;

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
	 * Parses Rails Time.utc.to_s
	 * @param time string
	 * @return date object
	 */
	public static Date parseUTC(String s) {
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
	
	/**
	 * Parses Rails Time.iso8601.to_s
	 * @param time string
	 * @return date object
	 */
	public static Date parseISO8601(String iso8601String) {
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(iso8601String).toGregorianCalendar().getTime();
		} catch (Exception e) {
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
				return df.parse(iso8601String);
			} catch (Exception e2) {
				Log.w(TAG, "SimpleDateFormat parse exception", e2);
			}
			Log.w(TAG, "XMLGregorianCalendar parse exception", e);
		}
		return new Date();
		
	}

}