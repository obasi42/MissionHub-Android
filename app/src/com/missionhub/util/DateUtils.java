package com.missionhub.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by croemmich on 6/28/13.
 */
public class DateUtils {

    /**
     * Parses a date in the format yyyy-mm-dd
     *
     * @param ymdString
     * @return
     */
    public static DateTime parseYMD(final String ymdString) {
        final java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        df.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
        try {
            return new DateTime(df.parse(ymdString), DateTimeZone.UTC);
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
    public static DateTime parseISO8601(final String iso8601String) {
        try {
            return new DateTime(iso8601String);
        } catch (IllegalArgumentException e) {
            // May be it came in formatted as a java.util.Date, so try that
            Date date = new Date(iso8601String);
            try {
                return new DateTime(date);
            } catch (Exception e2) { /* ignore */ }
        }
        return null;
    }

}
