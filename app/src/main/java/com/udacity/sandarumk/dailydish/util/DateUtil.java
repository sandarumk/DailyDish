package com.udacity.sandarumk.dailydish.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import android.support.v7.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Pair<Date, Date> geCurrentWeekStartEnd(Context context) {
        return getWeekStartEnd(context, new Date());
    }

    public static Pair<Date, Date> getWeekStartEnd(Context context, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int weekType = Integer.parseInt(prefs.getString("week_type", "0"));

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, weekType);
        Date from = cal.getTime();

        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.add(Calendar.DATE, -1);
        Date to = cal.getTime();
        return new Pair<>(from, to);
    }

    public static Pair<Date, Date> getSingleDayRange(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        Date from = cal.getTime();

        cal.add(Calendar.DATE, 1);
//        cal.add(Calendar.MILLISECOND,-1);
        Date to = cal.getTime();
        return new Pair<>(from, to);
    }

    public static boolean isThisWeek(Context context, Date date) {
        Pair<Date, Date> currentWeekStartEnd = geCurrentWeekStartEnd(context);
        return (currentWeekStartEnd.first.before(date) || currentWeekStartEnd.first.equals(date)) &&
                (currentWeekStartEnd.second.after(date) || currentWeekStartEnd.second.equals(date));
    }
}
