package com.missionhub.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.missionhub.R;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

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

	public static String toCSV(final List<?> list) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<?> li = ((List<?>) list).listIterator();
		while (li.hasNext()) {
			final Object subpart = li.next();
			sb.append(subpart);
			if (li.hasNext()) {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	/**
	 * converts dip to px
	 * 
	 * @param dp
	 * @param context
	 * @return
	 */
	public static float dpToPixel(final float dip, final Context context) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
	}

	/**
	 * resets the action bar to a "clean slate"
	 * @param sherlockActivity
	 */
	public static void resetActionBar(SherlockFragmentActivity sherlockActivity) {
		sherlockActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		sherlockActivity.getSupportActionBar().setDisplayShowCustomEnabled(false);
		sherlockActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
		sherlockActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
		sherlockActivity.getSupportActionBar().setDisplayUseLogoEnabled(false);
		sherlockActivity.getSupportActionBar().setHomeButtonEnabled(true);
		sherlockActivity.getSupportActionBar().setIcon(R.drawable.ic_launcher);
		sherlockActivity.getSupportActionBar().setListNavigationCallbacks(null, null);
		sherlockActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		sherlockActivity.getSupportActionBar().setSubtitle(null);
		sherlockActivity.getSupportActionBar().setTitle("MissionHub");
	}
}