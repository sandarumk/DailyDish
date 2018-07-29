package com.udacity.sandarumk.dailydish.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.activities.AddRecipeActivity;
import com.udacity.sandarumk.dailydish.activities.RecipeSelectActivity;
import com.udacity.sandarumk.dailydish.adapters.DayAdapter;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;
import com.udacity.sandarumk.dailydish.datawrappers.RecipeWrapper;
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

public class ThisWeekFragment extends TimeChangeFragment implements DayAdapter.ScheduleEventListener {

    private static final int REQUEST_CODE_RECIPE_DETAIL = 10111;
    private static final String STATE_KEY_SELECTED_DAY = "selectedDayWrapper";
    private static final String STATE_KEY_SELECTED_MEAL = "selectedMealTime";
    public static final String ADD_RECIPE_MESSAGE = "Add some recipes to today's meal";
    private final int REQUEST_CODE_SELECT_RECIPE = 10101;

    private RecyclerView mRecyclerView;
    private DayAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab;

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
    public static ThisWeekFragment newInstance(Date from, Date to) {
        ThisWeekFragment fragment = new ThisWeekFragment();
        Bundle args = getDateBundle(from, to);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            selectedDayWrapper = (DayWrapper) savedInstanceState.getSerializable(STATE_KEY_SELECTED_DAY);
            selectedMealTime = MealTime.findMealTime(savedInstanceState.getInt(STATE_KEY_SELECTED_MEAL));
        }
        View view = inflater.inflate(R.layout.fragment_this_week, container, false);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        startLoad();

        AdView adView = view.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C98DB9D9C73047CAD050890357647175").build();
        adView.loadAd(adRequest);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCook();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_KEY_SELECTED_DAY, selectedDayWrapper);
        if(selectedMealTime != null) {
            outState.putInt(STATE_KEY_SELECTED_MEAL, selectedMealTime.getMealTime());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(this.getContext()).setCurrentScreen(this.getActivity(), "MealSchedule", ThisWeekFragment.class.getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateSchedule(List<DayWrapper> result) {
        if (mAdapter == null) {
            mAdapter = new DayAdapter(result, this);
            mRecyclerView.setAdapter(mAdapter);

            Pair<Date, Date> todayRange = DateUtil.getSingleDayRange(new Date());
            for (int i = 0; i < result.size(); i++) {
                DayWrapper dayWrapper = result.get(i);
                if (dayWrapper.getDate().equals(todayRange.first)) {
                    mRecyclerView.scrollToPosition(i);
                    break;
                }
            }
        } else {
            mAdapter.setDataset(result);
            mAdapter.notifyDataSetChanged();
        }
        DayWrapper todaysData = getTodaysData();
        if (todaysData != null && todaysData.getSchedule() != null && !todaysData.getSchedule().isEmpty()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private DayWrapper getTodaysData() {
        List<DayWrapper> result = mAdapter.getDataset();
        Pair<Date, Date> todayRange = DateUtil.getSingleDayRange(new Date());
        for (int i = 0; i < result.size(); i++) {
            DayWrapper dayWrapper = result.get(i);
            if (dayWrapper.getDate().equals(todayRange.first)) {
                return dayWrapper;
            }
        }
        return null;
    }

    private void startCook() {
        DayWrapper todaysData = getTodaysData();
        if (todaysData != null && todaysData.getSchedule() != null && !todaysData.getSchedule().isEmpty()) {
            Map<MealTime, List<Recipe>> schedule = todaysData.getSchedule();
            Map<Long, Recipe> recipeMap = new HashMap<>();
            for (Map.Entry<MealTime, List<Recipe>> entry : schedule.entrySet()) {
                if (entry.getValue() != null) {
                    for (Recipe recipe : entry.getValue()) {
                        recipeMap.put(recipe.getRecipeId(), recipe);
                    }
                }
            }

            if (recipeMap.size() == 0) {
                Toast.makeText(this.getContext(), ADD_RECIPE_MESSAGE, Toast.LENGTH_LONG).show();
            } else if (recipeMap.size() == 1) {
                startShowingRecipeDetail(new ArrayList<>(recipeMap.values()).get(0).getRecipeId());
            } else {
                showRecipeSelectionDialog(new ArrayList<>(recipeMap.values()));
            }
        } else {
            Toast.makeText(this.getContext(), ADD_RECIPE_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    private void showRecipeSelectionDialog(final List<Recipe> recipes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Select recipe to start");

        // add a list
        String[] recipeNames = new String[recipes.size()];
        for (int i = 0; i < recipes.size(); i++) {
            recipeNames[i] = recipes.get(i).getRecipeName();
        }
        builder.setItems(recipeNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startShowingRecipeDetail(recipes.get(which).getRecipeId());
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onAddSchedule(int position, DayWrapper dayWrapper, MealTime mealTime) {
        selectedDayWrapper = dayWrapper;
        selectedMealTime = mealTime;
        Intent intent = new Intent(this.getContext(), RecipeSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_RECIPE);
    }

    @Override
    public void onDeleteSchedule(int position, DayWrapper dayWrapper, MealTime mealTime, Recipe recipe) {
        new ScheduleDeleteTask(this).execute(dayWrapper.getDate(), mealTime, recipe);
    }

    @Override
    public void onSelectSchedule(int position, DayWrapper dayWrapper, MealTime mealTime, Recipe recipe) {
        startShowingRecipeDetail(recipe.getRecipeId());
    }

    private void startShowingRecipeDetail(long recipeId) {
        new RecipeDetailLoadTask(this).execute(recipeId);
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

    @Override
    protected void startLoad() {
        String title = "Meals for " + getDateDescription();
        getActivity().setTitle(title);

        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
        FirebaseAnalytics.getInstance(this.getContext()).logEvent("view_schedule", params);

        new ScheduleLoadTask(this).execute(from, to);
    }

    private void saveSchedule(DayWrapper dayWrapper, MealTime mealTime, Recipe recipe) {
        new ScheduleSaveTask(this).execute(dayWrapper.getDate(), mealTime, recipe);
    }

    private void startRecipeDetailActivity(RecipeWrapper recipeWrapper) {
        Intent intent = new Intent(getContext(), AddRecipeActivity.class);
        if (recipeWrapper != null) {
            intent.putExtra(AddRecipeActivity.INTENT_EXTRA_RECIPE, recipeWrapper);
        }
        startActivityForResult(intent, REQUEST_CODE_RECIPE_DETAIL);
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
                fragmentReference.get().startLoad();

            }
        }
    }

    static class ScheduleDeleteTask extends AsyncTask<Object, Void, Boolean> {

        private WeakReference<ThisWeekFragment> fragmentReference;

        public ScheduleDeleteTask(ThisWeekFragment fragment) {
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
                    DataProvider.deleteSchedule(fragment.getContext(), (Date) objects[0], (MealTime) objects[1], (Recipe) objects[2]);
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
                fragmentReference.get().startLoad();

            }
        }
    }

    static class RecipeDetailLoadTask extends AsyncTask<Long, Void, RecipeWrapper> {

        private WeakReference<ThisWeekFragment> fragmentReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progress
        }

        public RecipeDetailLoadTask(ThisWeekFragment fragment) {
            fragmentReference = new WeakReference<>(fragment);
        }

        @Override
        protected RecipeWrapper doInBackground(Long... params) {
            return DataProvider.loadRecipe(fragmentReference.get().getContext(), params[0]);
        }

        @Override
        protected void onPostExecute(RecipeWrapper result) {
            super.onPostExecute(result);
            //hide progress
            if (result != null && fragmentReference.get() != null) {
                fragmentReference.get().startRecipeDetailActivity(result);
            }
        }
    }

}


