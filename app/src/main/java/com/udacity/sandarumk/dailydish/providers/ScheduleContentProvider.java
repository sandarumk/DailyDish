package com.udacity.sandarumk.dailydish.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.udacity.sandarumk.dailydish.dao.MealScheduleDAO;
import com.udacity.sandarumk.dailydish.dao.ScheduleRecipeDAO;
import com.udacity.sandarumk.dailydish.util.DataProvider;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScheduleContentProvider extends ContentProvider {

    private static final int SCHEDULE_DIR = 1;
    private static final int SCHEDULE_ID = 2;
    private static final int RECIPE_DIR = 3;
    private static final int RECIPE_ID = 4;
    private static final int SCHEDULE_RECIPE_DIR = 5;
    private static final int SCHEDULE_RECIPE_ID = 6;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ScheduleContract.AUTHORITY, "schedules", SCHEDULE_DIR);
        URI_MATCHER.addURI(ScheduleContract.AUTHORITY, "schedules/#", SCHEDULE_ID);
        URI_MATCHER.addURI(ScheduleContract.AUTHORITY, "recipes", RECIPE_DIR);
        URI_MATCHER.addURI(ScheduleContract.AUTHORITY, "schedules/#", RECIPE_ID);
        URI_MATCHER.addURI(ScheduleContract.AUTHORITY, "schedulerecipes", SCHEDULE_RECIPE_DIR);
        URI_MATCHER.addURI(ScheduleContract.AUTHORITY, "schedulerecipes/#", SCHEDULE_RECIPE_ID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable final String[] selectionArgs, @Nullable String sortOrder) {
        final int code = URI_MATCHER.match(uri);
        if (code == SCHEDULE_DIR || code == SCHEDULE_ID) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            MealScheduleDAO mealScheduleDAO = DataProvider.getDatabase(context).mealScheduleDAO();
            Cursor cursor;
            if (code == SCHEDULE_DIR) {
                if (selection != null && selection.contains(ScheduleContract.Schedules.DATE)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        cursor = mealScheduleDAO.loadForGivenDateRange(sdf.parse(selectionArgs[0]), sdf.parse(selectionArgs[1]));
                    } catch (Exception e) {
                        cursor = mealScheduleDAO.loadAll();
                    }
                } else {
                    cursor = mealScheduleDAO.loadAll();
                }
            } else {
                cursor = mealScheduleDAO.findById(ContentUris.parseId(uri));
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else if (code == SCHEDULE_RECIPE_DIR || code == SCHEDULE_RECIPE_ID) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            final ScheduleRecipeDAO dao = DataProvider.getDatabase(context).scheduleRecipeDAO();
            final Cursor[] cursor = new Cursor[1];
            if (code == SCHEDULE_RECIPE_DIR) {
                if (selection != null && selection.contains(ScheduleContract.Schedules.DATE)) {
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                cursor[0] = dao.loadByMealScheduleId(sdf.parse(selectionArgs[0]), sdf.parse(selectionArgs[1]));
                            } catch (Exception e) {
                                cursor[0] = dao.loadAll();
                            }
                        }
                    });

                    service.shutdown();
                    try {
                        service.awaitTermination(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    cursor[0] = dao.loadAll();
                }
            } else {
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        cursor[0] = dao.loadByMealScheduleId(ContentUris.parseId(uri));
                    }
                });
                service.shutdown();
                try {
                    service.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            cursor[0].setNotificationUri(context.getContentResolver(), uri);
            return cursor[0];
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case SCHEDULE_DIR:
                return ScheduleContract.Schedules.CONTENT_TYPE;
            case SCHEDULE_ID:
                return ScheduleContract.Schedules.CONTENT_ITEM_TYPE;
            case RECIPE_DIR:
                return ScheduleContract.Recipes.CONTENT_TYPE;
            case RECIPE_ID:
                return ScheduleContract.Recipes.CONTENT_ITEM_TYPE;
            case SCHEDULE_RECIPE_DIR:
                return ScheduleContract.ScheduleRecipes.CONTENT_TYPE;
            case SCHEDULE_RECIPE_ID:
                return ScheduleContract.ScheduleRecipes.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //TODO implement
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        //TODO implement
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        //TODO implement
        return 0;
    }
}
