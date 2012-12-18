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
import android.util.TypedValue;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.Person;
import com.missionhub.model.gson.GRejoicable;

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
			/* ignore */
		}
		return null;
	}

	/**
	 * Parses Rails Time.iso8601.to_s
	 * 
	 * @param time
	 *            string
	 * @return date object or null
	 */
	public static Date parseISO8601(final String iso8601String) {
		try {
			return ISO8601.parse(iso8601String).getTime();
		} catch (final Exception e) {
			try {
				final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
				return df.parse(iso8601String);
			} catch (final Exception e2) {
				/* ignore */
			}
		}
		return null;

	}

	/**
	 * Parses a date in the format yyyy-mm-dd
	 * 
	 * @param ymdString
	 * @return
	 */
	public static Date parseYMD(final String ymdString) {
		final java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));

		try {
			return df.parse(ymdString);
		} catch (final Exception e) {
			/* ignore */
		}
		return null;
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
	 * Converts a collection of items into a string of comma seperated values
	 * 
	 * @param items
	 * @return
	 */
	public static String toCSV(final Collection<?> items) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<?> itr = items.iterator();
		while (itr.hasNext()) {
			final String item = String.valueOf(itr.next());
			sb.append(item);
			if (itr.hasNext()) {
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
		sherlockActivity.getSupportActionBar().setTitle(R.string.app_name);
	}

	public enum Gender {
		male, female;

		@Override
		public String toString() {
			switch (this) {
			case male:
				return getString(R.string.gender_male);
			case female:
				return getString(R.string.gender_female);
			default:
				return "";
			}
		}

		public String toFilter() {
			switch (this) {
			case male:
				return "m";
			case female:
				return "f";
			default:
				return "";
			}
		}
	}

	public enum Rejoicable {
		spiritual_conversation, prayed_to_receive, gospel_presentation;

		@Override
		public String toString() {
			switch (this) {
			case spiritual_conversation:
				return getString(R.string.rejoice_spiritual_conversation);
			case prayed_to_receive:
				return getString(R.string.rejoice_prayed_to_receive);
			case gospel_presentation:
				return getString(R.string.rejoice_gospel_presentation);
			default:
				return "";
			}
		}
		
		public GRejoicable rejoicable() {
			GRejoicable rejoicable = new GRejoicable();
			rejoicable.what = name();
			return rejoicable;
		}
	}

	public enum FollowupStatus {
		uncontacted, attempted_contact, contacted, completed, do_not_contact;

		@Override
		public String toString() {
			switch (this) {
			case uncontacted:
				return getString(R.string.status_uncontacted);
			case attempted_contact:
				return getString(R.string.status_attempted_contact);
			case contacted:
				return getString(R.string.status_contacted);
			case completed:
				return getString(R.string.status_completed);
			case do_not_contact:
				return getString(R.string.status_do_not_contact);
			default:
				return "";
			}
		}
		
		public static FollowupStatus fromString(String string) {
			if (string.equalsIgnoreCase(getString(R.string.status_uncontacted))){
				return uncontacted;
			} else if (string.equalsIgnoreCase(getString(R.string.status_attempted_contact))) {
				return attempted_contact;
			} else if (string.equalsIgnoreCase(getString(R.string.status_contacted))) {
				return contacted;
			} else if (string.equalsIgnoreCase(getString(R.string.status_completed))) {
				return completed;
			} else if (string.equalsIgnoreCase(getString(R.string.status_do_not_contact))) {
				return do_not_contact;
			}
			return uncontacted;
		}
	}
	
	public enum Role {
		admin, contact, involved, leader, alumni;

		@Override
		public String toString() {
			switch (this) {
			case admin:
				return getString(R.string.role_admin);
			case contact:
				return getString(R.string.role_contact);
			case involved:
				return getString(R.string.role_involved);
			case leader:
				return getString(R.string.role_leader);
			case alumni:
				return getString(R.string.role_alumni);
			default:
				return "";
			}
		}
		
		public long id() {
			switch (this) {
			case admin:
				return 1;
			case contact:
				return 2;
			case involved:
				return 3;
			case leader:
				return 4;
			case alumni:
				return 5;
			default:
				return -1;
			}
		}
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
			return getString(R.string.u_invalid_phone);
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