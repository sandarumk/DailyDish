package com.udacity.sandarumk.dailydish.providers;

import android.content.ContentResolver;
import android.net.Uri;

public final class ScheduleContract {

    public static final String AUTHORITY = "com.udacity.sandarumk.dailydish";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Schedules implements CommonColumns {
        public static final String SCHEDULE_ID = "schedule_id";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ScheduleContract.CONTENT_URI, "schedules");
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.udacity.sandarumk.dailydish_schedules";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.udacity.sandarumk.dailydish_schedules";
        public static final String[] PROJECTION_ALL = {SCHEDULE_ID, DATE, MEAL_TIME, RECIPE_ID};
        public static final String SORT_ORDER_DEFAULT = DATE + " ASC";
    }

    public static final class Recipes {
        public static final String RECIPE_ID = "recipe_id";
        public static final String NAME = "name";
        public static final String STEPS = "steps";
        public static final String NOTES = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ScheduleContract.CONTENT_URI, "recipes");
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.udacity.sandarumk.dailydish_recipes";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.udacity.sandarumk.dailydish_recipes";
        public static final String[] PROJECTION_ALL = {RECIPE_ID, NAME, STEPS, NOTES};
        public static final String SORT_ORDER_DEFAULT = NAME + " ASC";
    }

    public static final class ScheduleRecipes implements CommonColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ScheduleContract.CONTENT_URI, "schedulerecipes");
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.udacity.sandarumk.dailydish_schedulerecipes";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.udacity.sandarumk.dailydish_schedulerecipes";
        public static final String[] PROJECTION_ALL = {Schedules.SCHEDULE_ID, DATE, MEAL_TIME, RECIPE_ID, Recipes.NAME};
        public static final String SORT_ORDER_DEFAULT = DATE + " ASC";
    }

    public static interface CommonColumns {
        public static final String RECIPE_ID = "recipe_id";
        public static final String DATE = "date";
        public static final String MEAL_TIME = "meal_time";
    }
}
