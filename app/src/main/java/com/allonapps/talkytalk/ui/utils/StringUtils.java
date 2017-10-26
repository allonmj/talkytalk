package com.allonapps.talkytalk.ui.utils;

import android.content.res.Resources;

import com.allonapps.talkytalk.R;

import java.util.Date;

/**
 * Created by michael on 10/25/17.
 */

public class StringUtils {

    private static final long MINUTE_MS = 60000l;
    private static final long HOUR_MS = 60 * MINUTE_MS;
    private static final long DAY_MS = 24 * HOUR_MS;
    private static final long WEEK_MS = 7 * DAY_MS;
    private static final long MONTH_MS = 30 * DAY_MS;
    private static final long YEAR_MS = 365 * DAY_MS;

    public static String ago(Date date, Resources resources) {
        Date currentDate = new Date();
        long currentDateMs = currentDate.getTime();
        long dateMs = date.getTime();
        long diff = currentDateMs - dateMs;

        if (diff < MINUTE_MS) {
            return resources.getString(R.string.moments_ago);
        }
        if (diff < HOUR_MS) {
            int minutes = (int) (diff / MINUTE_MS);
            return resources.getString(R.string.minutes_ago, minutes);
        }
        if (diff < DAY_MS) {
            int hours = (int) (diff / HOUR_MS);
            return resources.getString(R.string.hours_ago, hours);
        }
        if (diff < WEEK_MS) {
            int days = (int) (diff / DAY_MS);
            return resources.getString(R.string.days_ago, days);
        }
        if (diff < MONTH_MS) {
            int weeks = (int) (diff / WEEK_MS);
            return resources.getString(R.string.weeks_ago, weeks);
        }
        if (diff < YEAR_MS) {
            int months = (int) (diff / MONTH_MS);
            return resources.getString(R.string.months_ago, months);
        }

        int years = (int) (diff / YEAR_MS);
        return resources.getString(R.string.years_ago, years);
    }

}