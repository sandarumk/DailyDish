package com.udacity.sandarumk.dailydish.util;

import android.support.v4.util.Pair;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Pair<Date, Date> getWeekStartEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date from = cal.getTime();

        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.add(Calendar.DATE, -1);
        Date to = cal.getTime();
        return new Pair<>(from, to);
    }
}
