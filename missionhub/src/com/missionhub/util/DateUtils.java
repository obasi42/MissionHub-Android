package com.missionhub.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    /**
     * Parses a date in the format yyyy-mm-dd
     *
     * @param ymdString
     * @return
     */
    public static DateTime parseYMD(final String ymdString) {
        final java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        try {
            return new DateTime(df.parse(ymdString), DateTimeZone.UTC);
        } catch (final Exception e) {
            /* ignore */
        }
        return null;
    }

    /**
     * Returns a string yyyy-MM-dd from a DateTime object
     *
     * @param dateTime
     * @return string timestamp in the format yyyy-MM-dd
     */
    public static String toYMD(final DateTime dateTime) {
        return dateTime.toString(DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC().withLocale(Locale.US));
    }

    /**
     * Parses Rails Time.iso8601.to_s
     *
     * @param iso8601String string
     * @return date object or null
     */
    @SuppressWarnings("deprecation")
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

    /**
     * Parse a string yyyy-MM-dd HH:mm:ss to a DateTime object
     *
     * @param timestamp timestamp in the format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static DateTime parseSqlTimestamp(final String timestamp) {
        if (StringUtils.isEmpty(timestamp)) return null;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return new DateTime(df.parse(timestamp), DateTimeZone.UTC);
        } catch (ParseException e) { /* ignore */ }
        return null;
    }

    /**
     * Returns a string timestamp from a DateTime object
     *
     * @param dateTime
     * @return string timestamp in the format yyyy-MM-dd HH:mm:ss
     */
    public static String toSqlTimestamp(DateTime dateTime) {
        return dateTime.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC().withLocale(Locale.US));
    }

    /**
     * Returns the date time object in the system timezone
     *
     * @param dateTime
     * @return
     */
    public static DateTime toLocalTimezone(DateTime dateTime) {
        return dateTime.toDateTime(DateTimeZone.getDefault());
    }

    public static DateTime fixInstantDateTime(int year, int month, int day, int hour, int minute) {
        LocalDate localDate = new LocalDate().withYear(year).withMonthOfYear(month).withDayOfMonth(day);
        LocalTime localTime = new LocalTime().withHourOfDay(hour).withMinuteOfHour(minute);
        DateTime dateTime = localDate.toDateTime(localTime, DateTimeZone.UTC);
        return new DateTime(DateTimeZone.UTC.getMillisKeepLocal(DateTimeZone.getDefault(), dateTime.getMillis()));
    }

    public static String toISO8601(DateTime dateTime) {
        return dateTime.toString(ISODateTimeFormat.dateTime().withZoneUTC());
    }
}
