package com.missionhub.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class U {

	/** logging tag */
	public static final String TAG = U.class.getSimpleName();

	/**
	 * Checks if a long array contains a value
	 * 
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
	 * 
	 * @param time
	 *            string
	 * @return date object
	 */
	public static Date parseUTC(String s) {
		final java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
		s = s.replace("UTC", "");

		try {
			return df.parse(s);
		} catch (final Exception e) {
			Log.w(TAG, "date parse exception", e);
		}
		return new Date();
	}

	/**
	 * Parses Rails Time.iso8601.to_s
	 * 
	 * @param time
	 *            string
	 * @return date object
	 */
	public static Date parseISO8601(final String iso8601String) {
		try {
			return ISO8601.parse(iso8601String).getTime();
		} catch (final Exception e) {
			try {
				final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				return df.parse(iso8601String);
			} catch (final Exception e2) {
				Log.w(TAG, "Could not parse date", e);
			}
		}
		return new Date();

	}

	/**
	 * Checks any object in a list is null
	 * 
	 * @param objects
	 * @return true if an object is null
	 */
	public static boolean isNull(final Object... objects) {
		for (final Object o : objects) {
			if (o == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if any object in a list is null or empty
	 * 
	 * @param objects
	 * @return true if an object is null or empty
	 */
	public static boolean isNullEmpty(final Object... objects) {
		for (final Object o : objects) {
			if (o == null) {
				return true;
			}
			if (o instanceof CharSequence && ((CharSequence) o).length() <= 0) {
				return true;
			}
			if (o instanceof Collection && ((Collection<?>) o).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if any object in a list is null, empty, or negative
	 * 
	 * @param objects
	 * @return true if any object is null, empty, or negative
	 */
	public static boolean isNullEmptyNegative(final Object... objects) {
		for (final Object o : objects) {
			if (isNullEmpty(o)) {
				return true;
			}
			if (o instanceof Number && ((Number) o).doubleValue() < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the current device is a tablet. e.g. screen width >=
	 * 600dp
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isTablet(final Context context) {
		return isSW720dp(context) || isW1024dp(context);
	}
	
	public static boolean isSW720dp(Context context) {
		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = wm.getDefaultDisplay();

		final DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		
		final int widthdp = (int) (dm.widthPixels / dm.density + 0.5f);
		final int heightdp = (int) (dm.heightPixels / dm.density + 0.5f);
		
		// compare to the smallest
		if (widthdp > heightdp) {
			return heightdp >= 720;
		} else {
			return widthdp >= 720;
		}
	}
	
	public static boolean isW1024dp(Context context) {
		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = wm.getDefaultDisplay();

		final DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		
		final int widthdp = (int) (dm.widthPixels / dm.density + 0.5f);
		final int heightdp = (int) (dm.heightPixels / dm.density + 0.5f);
		
		if (widthdp >= 1024 && heightdp >= 600) {
			return true;
		}
		return false;
	}
	

	/**
	 * Returns true of the current device is a phone. e.g. screen width < 600dp
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPhone(final Context context) {
		return !isTablet(context);
	}

	/**
	 * Determines if the current device is an old tablet Used to select layouts
	 * for old tables like the Kindle Fire, Nook, etc.
	 * 
	 * @return true if screen width dp >= 600 and SDK version < Honeycomb
	 */
	public static boolean isOldTablet(final Context context) {
		if (isTablet(context) && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return true;
		}
		return false;
	}
}