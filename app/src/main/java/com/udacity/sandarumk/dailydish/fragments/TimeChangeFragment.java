package com.udacity.sandarumk.dailydish.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class TimeChangeFragment extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "FRAG_TAG_DATE_PICKER";

    protected static final String ARG_FROM = "fromDate";
    protected static final String ARG_TO = "toDate";

    protected Date from;
    protected Date to;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = (Date) getArguments().getSerializable(ARG_FROM);
            to = (Date) getArguments().getSerializable(ARG_TO);
        }
        if (from == null || to == null) {
            Pair<Date, Date> startEnd = DateUtil.geCurrentWeekStartEnd();
            from = startEnd.first;
            to = startEnd.second;
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_change_time, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_week:
                changeWeek();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    protected static Bundle getDateBundle(Date from, Date to) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FROM, from);
        args.putSerializable(ARG_TO, to);
        return args;
    }

    private void changeWeek() {

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setFirstDayOfWeek(Calendar.SUNDAY);
//                .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                .setDateRange(minDate, null)
//                .setDoneText("OK")
//                .setCancelText("Cancel")
//                .setThemeDark();
        cdp.show(this.getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        Pair<Date, Date> startEnd = DateUtil.getWeekStartEnd(cal.getTime());
        from = startEnd.first;
        to = startEnd.second;
        startLoad();
    }

    protected String getDateDescription(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        boolean thisWeek = DateUtil.isThisWeek(from);
        if(thisWeek) {
            return "this week";
        }else{
            return sdf.format(from) + " - " + sdf.format(to);
        }

    }

    protected abstract void startLoad();

}
