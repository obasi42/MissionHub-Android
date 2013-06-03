package com.missionhub.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import com.actionbarsherlock.app.ActionBar;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.model.Person;
import com.missionhub.model.PhoneNumber;
import com.missionhub.model.gson.GRejoicable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import org.holoeverywhere.app.Activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Set of utility methods
 */
public class U {

    /**
     * logging tag
     */
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
     * @param s string
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
     * @param iso8601String string
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
     * Concatenates a collections of strings with a delimiter.
     * @param delemiter
     * @param ignoreEmpty
     * @param items
     * @return
     */
    public static String concatinate(final CharSequence delimiter, final boolean ignoreEmpty, final CharSequence... items) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < items.length; i++) {
            final CharSequence string = items[i];
            if (ignoreEmpty && U.isNullEmpty(string)) {
                continue;
            }

            sb.append(string);

            if (delimiter != null && i + 1 < items.length) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }

    /**
     * Converts a collection of items into a string of comma seperated values
     *
     * @param items
     * @return
     */
    public static String toCSV(final Collection<?> items) {
        final StringBuilder sb = new StringBuilder();
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
     * Converts density independent pixels to pixels.
     *
     * @param dip
     * @return
     */
    public static float dpToPixel(final float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, Application.getContext().getResources().getDisplayMetrics());
    }

    /**
     * Converts pixels to density independent pixels.
     * @param px
     * @return
     */
    public static float pixelToDp(final float px) {
        return px / (Application.getContext().getResources().getDisplayMetrics().densityDpi / 160f);
    }

    /**
     * resets the action bar to a "clean slate"
     *
     * @param activity
     */
    public static void resetActionBar(final Activity activity) {
        activity.setSupportProgressBarIndeterminateVisibility(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setIcon(R.drawable.ic_launcher);
        activity.getSupportActionBar().setListNavigationCallbacks(null, null);
        activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        activity.getSupportActionBar().setSubtitle(null);
        activity.getSupportActionBar().setTitle(R.string.app_name);
    }

    public enum Gender {
        m, M, male, Male, f, F, female, Female;

        public Gender normalize() {
            switch (this) {
                case m:
                case M:
                case male:
                case Male:
                    return male;
                case f:
                case F:
                case female:
                case Female:
                    return female;
                default:
                    return null;
            }

        }

        @Override
        public String toString() {
            switch (normalize()) {
                case male:
                    return getString(R.string.gender_male);
                case female:
                    return getString(R.string.gender_female);
                default:
                    return "";
            }
        }

        public String toFilter() {
            switch (normalize()) {
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
            final GRejoicable rejoicable = new GRejoicable();
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

        public static FollowupStatus fromString(final String string) {
            if (string.equalsIgnoreCase(getString(R.string.status_uncontacted))) {
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

    public static Phonenumber.PhoneNumber parsePhoneNumber(final String phoneNumber) {
        try {
            return PhoneNumberUtil.getInstance().parseAndKeepRawInput(phoneNumber, "US");
        } catch (NumberParseException e) {
            /* ignore */
        }
        return null;
    }

    public static Phonenumber.PhoneNumber parsePhoneNumber(final PhoneNumber number) {
        if (number != null) {
            return parsePhoneNumber(number.getNumber());
        }
        return null;
    }

    public static boolean hasPhoneAbility(final Context ctx) {
        final TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
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

    public static String getProfilePicture(Person person, int width, int height) {
        String url = person.getPicture();

        if (url == null || url.contains("facebook.com") || url.contains("fbcdn.net")) {
            if (!U.isNullEmpty(person.getFb_uid())) {
                return "http://graph.facebook.com/" + person.getFb_uid() + "/picture?width=" + width + "&height=" + height;
            }
        }

        return url;
    }

    public static DisplayImageOptions getContactImageDisplayOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .showImageForEmptyUri(R.drawable.default_contact)
                .showImageOnFail(R.drawable.default_contact)
                .showStubImage(R.drawable.default_contact)
                .build();
    }
}