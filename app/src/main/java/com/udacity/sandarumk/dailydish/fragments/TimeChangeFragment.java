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
    private static final String STATE_KEY_FROM = "TimeChangeFragmentfrom";
    private static final String STATE_KEY_TO = "TimeChangeFragmentto";

    protected static final String ARG_FROM = "fromDate";
    protected static final String ARG_TO = "toDate";

    protected Date from;
    protected Date to;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            from = (Date) savedInstanceState.getSerializable(STATE_KEY_FROM);
            to = (Date) savedInstanceState.getSerializable(STATE_KEY_TO);
        } else {
            if (getArguments() != null) {
                from = (Date) getArguments().getSerializable(ARG_FROM);
                to = (Date) getArguments().getSerializable(ARG_TO);
            }
            if (from == null || to == null) {
                Pair<Date, Date> startEnd = DateUtil.geCurrentWeekStartEnd(this.getContext());
                from = startEnd.first;
                to = startEnd.second;
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_KEY_FROM, from);
        outState.putSerializable(STATE_KEY_TO, to);
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
        Pair<Date, Date> startEnd = DateUtil.getWeekStartEnd(this.getContext(), cal.getTime());
        from = startEnd.first;
        to = startEnd.second;
        startLoad();
        if (getActivity() instanceof OnDateChangeListener) {
            ((OnDateChangeListener) getActivity()).onDateChange(from, to);
        }
    }

    protected String getDateDescription() {
        boolean thisWeek = DateUtil.isThisWeek(this.getContext(), from);
        if (thisWeek) {
            return "this week";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
            return sdf.format(from) + " - " + sdf.format(to);
        }

    }

    protected abstract void startLoad();

    public interface OnDateChangeListener {
        void onDateChange(Date from, Date to);
    }
}
