package com.udacity.sandarumk.dailydish.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.activities.RecipeSelectActivity;
import com.udacity.sandarumk.dailydish.adapters.DayAdapter;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;
import com.udacity.sandarumk.dailydish.util.DataProvider;
import com.udacity.sandarumk.dailydish.util.DateUtil;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThisWeekFragment extends Fragment implements DayAdapter.AddScheduleListener {

    private final int REQUEST_CODE_SELECT_RECIPE = 10101;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String ARG_FROM = "fromDate";
    private static final String ARG_TO = "toDate";

    // TODO: Rename and change types of parameters
    private Date from;
    private Date to;

    private DayWrapper selectedDayWrapper;
    private MealTime selectedMealTime;

    // private OnFragmentInteractionListener mListener;
    public ThisWeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param from starting date
     * @param to   starting date
     * @return A new instance of fragment ThisWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThisWeekFragment newInstance(Date from, Date to) {
        ThisWeekFragment fragment = new ThisWeekFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        args.putSerializable(ARG_FROM, from);
        args.putSerializable(ARG_TO, to);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = (Date) getArguments().getSerializable(ARG_FROM);
            to = (Date) getArguments().getSerializable(ARG_TO);
        }
        if (from == null || to == null) {
            Pair<Date, Date> startEnd = DateUtil.getWeekStartEnd();
            from = startEnd.first;
            to = startEnd.second;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_this_week, container, false);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        startScheduleLoad();
        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        // if (mListener != null) {
        //    mListener.onFragmentInteraction(uri);
        // }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateSchedule(List<DayWrapper> result) {
        mAdapter = new DayAdapter(result, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void startScheduleLoad() {
        new ScheduleLoadTask(this).execute(from, to);
    }

    @Override
    public void onAddSchedule(DayWrapper dayWrapper, MealTime mealTime) {
        selectedDayWrapper = dayWrapper;
        selectedMealTime = mealTime;
        Intent intent = new Intent(this.getContext(), RecipeSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_RECIPE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_RECIPE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Serializable serializableExtra = data.getSerializableExtra(RecipeSelectActivity.EXTRA_SELECTED_RECIPE);
                if (serializableExtra instanceof Recipe) {
                    saveSchedule(selectedDayWrapper, selectedMealTime, (Recipe) serializableExtra);
                }
            }
            selectedMealTime = null;
            selectedDayWrapper = null;
        }
    }

    private void saveSchedule(DayWrapper dayWrapper, MealTime mealTime, Recipe recipe) {
        new ScheduleSaveTask(this).execute(dayWrapper.getDate(), mealTime, recipe);
    }

    static class ScheduleLoadTask extends AsyncTask<Date, Void, List<DayWrapper>> {

        private WeakReference<ThisWeekFragment> fragmentReference;

        public ScheduleLoadTask(ThisWeekFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        @Override
        protected List<DayWrapper> doInBackground(Date... params) {
            Date from = params[0];
            Date to = params[1];
            List<DayWrapper> dayWrappers = DataProvider.loadSchedule(fragmentReference.get().getContext(), from, to);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Map<String, DayWrapper> dateMap = new HashMap<>();
            for (DayWrapper dayWrapper : dayWrappers) {
                dateMap.put(sdf.format(dayWrapper.getDate()), dayWrapper);
            }
            List<DayWrapper> result = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            do {
                DayWrapper dayWrapper = dateMap.get(sdf.format(cal.getTime()));
                if (dayWrapper == null) {
                    result.add(DayWrapper.builder().date(cal.getTime()).schedule(new HashMap<MealTime, List<Recipe>>()).build());
                } else {
                    result.add(dayWrapper);
                }
                cal.add(Calendar.DATE, 1);
            } while (cal.getTimeInMillis() <= to.getTime());

            return result;
        }

        @Override
        protected void onPostExecute(List<DayWrapper> result) {
            super.onPostExecute(result);
            //hide progress
            if (result != null && fragmentReference.get() != null) {
                fragmentReference.get().updateSchedule(result);
            }
        }
    }

    static class ScheduleSaveTask extends AsyncTask<Object, Void, Boolean> {

        private WeakReference<ThisWeekFragment> fragmentReference;

        public ScheduleSaveTask(ThisWeekFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {
                ThisWeekFragment fragment = fragmentReference.get();
                if (fragment != null) {
                    DataProvider.saveSchedule(fragment.getContext(), (Date) objects[0], (MealTime) objects[1], (Recipe) objects[2]);
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //hide progress
            if (result && fragmentReference.get() != null) {
                fragmentReference.get().startScheduleLoad();

            }
        }
    }

}


