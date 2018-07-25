package com.udacity.sandarumk.dailydish.util;

import android.support.annotation.NonNull;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExtendedDateFormat extends SimpleDateFormat {

    public ExtendedDateFormat(@NonNull String pattern) {
        super(pattern);
    }

    @Override
    public StringBuffer format(@NonNull Date date, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
        StringBuffer format = super.format(date, toAppendTo, pos);
        int start = format.indexOf("AD");
        if (start > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DATE);
            String text = "th";
            switch (day) {
                case 1:
                case 21:
                case 31:
                    text = "st";
                    break;
                case 2:
                case 22:
                    text = "nd";
                    break;
                case 3:
                case 23:
                    text = "rd";
                    break;
            }
            format.replace(start, start + 2, text);
        }
        return format;
    }
}
