/*
 * Copyright (c) 2011 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * 
 * Modifications 2012 C. Roemmich
 * Use android strings.
 */
package com.missionhub.util;

import com.missionhub.R;
import com.missionhub.application.Application;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Time ago class that converts long millisecond and {@link Date} objects to time ago/from now {@link String} objects.
 * <p/>
 * This class uses the messages from {@link Messages} by default but those can be changed after creation through one of
 * the setter methods for a specified time string.
 */
public class TimeAgo {

    private String prefixAgo = null;

    private String prefixFromNow = null;

    private String suffixAgo = Application.getContext().getString(R.string.time_ago_ago);

    private String suffixFromNow = Application.getContext().getString(R.string.time_ago_from_now);

    private String seconds = Application.getContext().getString(R.string.time_ago_seconds);

    private String minute = Application.getContext().getString(R.string.time_ago_minute);

    private String minutes = Application.getContext().getString(R.string.time_ago_minutes);

    private String hour = Application.getContext().getString(R.string.time_ago_hour);

    private String hours = Application.getContext().getString(R.string.time_ago_hours);

    private String day = Application.getContext().getString(R.string.time_ago_day);

    private String days = Application.getContext().getString(R.string.time_ago_days);

    private String month = Application.getContext().getString(R.string.time_ago_month);

    private String months = Application.getContext().getString(R.string.time_ago_months);

    private String year = Application.getContext().getString(R.string.time_ago_year);

    private String years = Application.getContext().getString(R.string.time_ago_years);

    /**
     * Get time until specified date
     *
     * @param date
     * @return time string
     */
    public String timeUntil(final Date date) {
        return timeUntil(date.getTime());
    }

    /**
     * Get time ago that date occurred
     *
     * @param date
     * @return time string
     */
    public String timeAgo(final Date date) {
        return timeAgo(date.getTime());
    }

    /**
     * Get time until specified milliseconds date
     *
     * @param millis
     * @return time string
     */
    public String timeUntil(final long millis) {
        return time(millis - System.currentTimeMillis(), true);
    }

    /**
     * Get time ago that milliseconds date occurred
     *
     * @param millis
     * @return time string
     */
    public String timeAgo(final long millis) {
        return time(System.currentTimeMillis() - millis, false);
    }

    /**
     * Get time string for milliseconds distance
     *
     * @param distanceMillis
     * @param allowFuture
     * @return time string
     */
    public String time(long distanceMillis, final boolean allowFuture) {
        final String prefix;
        final String suffix;
        if (allowFuture && distanceMillis < 0) {
            distanceMillis = Math.abs(distanceMillis);
            prefix = prefixFromNow;
            suffix = suffixFromNow;
        } else {
            prefix = prefixAgo;
            suffix = suffixAgo;
        }

        final double seconds = distanceMillis / 1000;
        final double minutes = seconds / 60;
        final double hours = minutes / 60;
        final double days = hours / 24;
        final double years = days / 365;

        final String time;
        if (seconds < 45) time = this.seconds;
        else if (seconds < 90) time = this.minute;
        else if (minutes < 45) time = MessageFormat.format(this.minutes, Math.round(minutes));
        else if (minutes < 90) time = this.hour;
        else if (hours < 24) time = MessageFormat.format(this.hours, Math.round(hours));
        else if (hours < 48) time = this.day;
        else if (days < 30) time = MessageFormat.format(this.days, Math.floor(days));
        else if (days < 60) time = this.month;
        else if (days < 365) time = MessageFormat.format(this.months, Math.floor(days / 30));
        else if (years < 2) time = this.year;
        else
            time = MessageFormat.format(this.years, Math.floor(years));

        return join(prefix, time, suffix);
    }

    /**
     * Join time string with prefix and suffix. The prefix and suffix are only joined with the time if they are non-null
     * and non-empty
     *
     * @param prefix
     * @param time
     * @param suffix
     * @return non-null joined string
     */
    public String join(final String prefix, final String time, final String suffix) {
        final StringBuilder joined = new StringBuilder();
        if (prefix != null && prefix.length() > 0) joined.append(prefix).append(' ');
        joined.append(time);
        if (suffix != null && suffix.length() > 0) joined.append(' ').append(suffix);
        return joined.toString();
    }

    /**
     * @return prefixAgo
     */
    public String getPrefixAgo() {
        return prefixAgo;
    }

    /**
     * @param prefixAgo prefixAgo value
     * @return this instance
     */
    public TimeAgo setPrefixAgo(final String prefixAgo) {
        this.prefixAgo = prefixAgo;
        return this;
    }

    /**
     * @return prefixFromNow
     */
    public String getPrefixFromNow() {
        return prefixFromNow;
    }

    /**
     * @param prefixFromNow prefixFromNow value
     * @return this instance
     */
    public TimeAgo setPrefixFromNow(final String prefixFromNow) {
        this.prefixFromNow = prefixFromNow;
        return this;
    }

    /**
     * @return suffixAgo
     */
    public String getSuffixAgo() {
        return suffixAgo;
    }

    /**
     * @param suffixAgo suffixAgo value
     * @return this instance
     */
    public TimeAgo setSuffixAgo(final String suffixAgo) {
        this.suffixAgo = suffixAgo;
        return this;
    }

    /**
     * @return suffixFromNow
     */
    public String getSuffixFromNow() {
        return suffixFromNow;
    }

    /**
     * @param suffixFromNow suffixFromNow value
     * @return this instance
     */
    public TimeAgo setSuffixFromNow(final String suffixFromNow) {
        this.suffixFromNow = suffixFromNow;
        return this;
    }

    /**
     * @return seconds
     */
    public String getSeconds() {
        return seconds;
    }

    /**
     * @param seconds seconds value
     * @return this instance
     */
    public TimeAgo setSeconds(final String seconds) {
        this.seconds = seconds;
        return this;
    }

    /**
     * @return minute
     */
    public String getMinute() {
        return minute;
    }

    /**
     * @param minute minute value
     * @return this instance
     */
    public TimeAgo setMinute(final String minute) {
        this.minute = minute;
        return this;
    }

    /**
     * @return minutes
     */
    public String getMinutes() {
        return minutes;
    }

    /**
     * @param minutes minutes value
     * @return this instance
     */
    public TimeAgo setMinutes(final String minutes) {
        this.minutes = minutes;
        return this;
    }

    /**
     * @return hour
     */
    public String getHour() {
        return hour;
    }

    /**
     * @param hour hour value
     * @return this instance
     */
    public TimeAgo setHour(final String hour) {
        this.hour = hour;
        return this;
    }

    /**
     * @return hours
     */
    public String getHours() {
        return hours;
    }

    /**
     * @param hours hours value
     * @return this instance
     */
    public TimeAgo setHours(final String hours) {
        this.hours = hours;
        return this;
    }

    /**
     * @return day
     */
    public String getDay() {
        return day;
    }

    /**
     * @param day day value
     * @return this instance
     */
    public TimeAgo setDay(final String day) {
        this.day = day;
        return this;
    }

    /**
     * @return days
     */
    public String getDays() {
        return days;
    }

    /**
     * @param days days value
     * @return this instance
     */
    public TimeAgo setDays(final String days) {
        this.days = days;
        return this;
    }

    /**
     * @return month
     */
    public String getMonth() {
        return month;
    }

    /**
     * @param month month value
     * @return this instance
     */
    public TimeAgo setMonth(final String month) {
        this.month = month;
        return this;
    }

    /**
     * @return months
     */
    public String getMonths() {
        return months;
    }

    /**
     * @param months months value
     * @return this instance
     */
    public TimeAgo setMonths(final String months) {
        this.months = months;
        return this;
    }

    /**
     * @return year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year year value
     * @return this instance
     */
    public TimeAgo setYear(final String year) {
        this.year = year;
        return this;
    }

    /**
     * @return years
     */
    public String getYears() {
        return years;
    }

    /**
     * @param years years value
     * @return this instance
     */
    public TimeAgo setYears(final String years) {
        this.years = years;
        return this;
    }
}