package com.missionhub.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.Person;

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
		final java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US);
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
				final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
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
	public static float dpToPixel(final float dip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, Application.getContext().getResources().getDisplayMetrics());
	}

	public static float pixelToDp(final float px) {
		return px / (Application.getContext().getResources().getDisplayMetrics().densityDpi / 160f);
	}

	/**
	 * resets the action bar to a "clean slate"
	 * 
	 * @param sherlockActivity
	 */
	public static void resetActionBar(final SherlockFragmentActivity sherlockActivity) {
		sherlockActivity.setSupportProgressBarIndeterminateVisibility(false);
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

	public static List<String> getStatuses() {
		final List<String> s = new ArrayList<String>();
		s.add("uncontacted");
		s.add("attempted_contact");
		s.add("contacted");
		s.add("completed");
		s.add("do_not_contact");
		return s;
	}

	public static CharSequence translateStatus(final String status) {
		if (status.equalsIgnoreCase("uncontacted")) {
			return getString(R.string.status_uncontacted);
		} else if (status.equalsIgnoreCase("attempted_contact")) {
			return getString(R.string.status_attempted_contact);
		} else if (status.equalsIgnoreCase("contacted")) {
			return getString(R.string.status_contacted);
		} else if (status.equalsIgnoreCase("completed")) {
			return getString(R.string.status_completed);
		} else if (status.equalsIgnoreCase("do_not_contact")) {
			return getString(R.string.status_do_not_contact);
		}
		return "";
	}

	private static String getString(final int resource) {
		return Application.getContext().getString(resource);
	}

	public static String formatPhoneNumber(String phoneNumber) {
		String fNum = null;
		phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

		if (11 == phoneNumber.length()) {
			fNum = "+" + phoneNumber.substring(0, 1);
			fNum += " (" + phoneNumber.substring(1, 4) + ")";
			fNum += " " + phoneNumber.substring(4, 7);
			fNum += "-" + phoneNumber.substring(7, 11);
		} else if (10 == phoneNumber.length()) {
			fNum = "(" + phoneNumber.substring(0, 3) + ")";
			fNum += " " + phoneNumber.substring(3, 6);
			fNum += "-" + phoneNumber.substring(6, 10);
		} else if (7 == phoneNumber.length()) {
			fNum = phoneNumber.substring(0, 3);
			fNum += "-" + phoneNumber.substring(4, 7);
		} else {
			return "Invalid Phone Number.";
		}

		return fNum;
	}

	public static boolean hasPhoneAbility(final Context ctx) {
		final TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) return false;

		return true;
	}

	public static boolean superGetRetainInstance(final Fragment fragment) {
		Fragment parentFragment = fragment.getParentFragment();
		while (parentFragment != null) {
			if (parentFragment.getRetainInstance()) {
				return true;
			}
			parentFragment = parentFragment.getParentFragment();
		}

		return fragment.getRetainInstance();
	}

	public static ArrayList<Person> sortPeople(final Collection<Person> people, final boolean asc) {
		final TreeMultimap<String, Person> sorted = TreeMultimap.create(Ordering.natural(), Ordering.usingToString());

		for (final Person person : people) {
			String name = "";
			if (!U.isNullEmpty(person.getName())) {
				name = person.getName();
			}

			sorted.put(name, person);
		}

		return new ArrayList<Person>(sorted.values());
	}
}