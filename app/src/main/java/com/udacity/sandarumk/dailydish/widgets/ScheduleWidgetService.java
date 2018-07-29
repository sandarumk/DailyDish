package com.udacity.sandarumk.dailydish.widgets;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.RemoteViews;

import com.udacity.sandarumk.dailydish.R;
import com.udacity.sandarumk.dailydish.datamodel.MealTime;
import com.udacity.sandarumk.dailydish.datamodel.Recipe;
import com.udacity.sandarumk.dailydish.datawrappers.DayWrapper;
import com.udacity.sandarumk.dailydish.providers.ScheduleContentProvider;
import com.udacity.sandarumk.dailydish.util.DataProvider;
import com.udacity.sandarumk.dailydish.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScheduleWidgetService extends Service {

    public static final String NO_SCHEDULES = "No schedules";
    public static final String MORE = "More..";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        ComponentName thisWidget = new ComponentName(getApplicationContext(), ScheduleContentProvider.class);
        int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

        final Pair<Date, Date> dayRange = DateUtil.getSingleDayRange(new Date());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int widgetId : allWidgetIds) {

            final List<DayWrapper>[] dayWrappersArr = new ArrayList[]{new ArrayList<>()};
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    dayWrappersArr[0] = DataProvider.loadSchedule(ScheduleWidgetService.this.getApplicationContext(), dayRange.first, dayRange.first);
                }
            });
            service.shutdown();
            try {
                service.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<DayWrapper> dayWrappers = dayWrappersArr[0];

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);

            if (dayWrappers.isEmpty() || dayWrappers.get(0).getSchedule() == null) {
                noSchedule(remoteViews, R.id.text_widget_b_1, R.id.text_widget_b_2, R.id.text_widget_b_3, R.id.text_widget_b_more);
                noSchedule(remoteViews, R.id.text_widget_l_1, R.id.text_widget_l_2, R.id.text_widget_l_3, R.id.text_widget_l_more);
                noSchedule(remoteViews, R.id.text_widget_d_1, R.id.text_widget_d_2, R.id.text_widget_d_3, R.id.text_widget_d_more);

            } else {
                DayWrapper dayWrapper = dayWrappers.get(0);

                List<Recipe> breakfast = dayWrapper.getSchedule().get(MealTime.BREAKFAST);
                List<Recipe> lunch = dayWrapper.getSchedule().get(MealTime.LUNCH);
                List<Recipe> dinner = dayWrapper.getSchedule().get(MealTime.DINNER);

                if (breakfast == null || breakfast.isEmpty()) {
                    noSchedule(remoteViews, R.id.text_widget_b_1, R.id.text_widget_b_2, R.id.text_widget_b_3, R.id.text_widget_b_more);
                } else {
                    setSchedule(remoteViews, breakfast, R.id.text_widget_b_1, R.id.text_widget_b_2, R.id.text_widget_b_3, R.id.text_widget_b_more);
                }
                if (lunch == null || lunch.isEmpty()) {
                    noSchedule(remoteViews, R.id.text_widget_l_1, R.id.text_widget_l_2, R.id.text_widget_l_3, R.id.text_widget_l_more);
                } else {
                    setSchedule(remoteViews, lunch, R.id.text_widget_l_1, R.id.text_widget_l_2, R.id.text_widget_l_3, R.id.text_widget_l_more);
                }
                if (dinner == null || dinner.isEmpty()) {
                    noSchedule(remoteViews, R.id.text_widget_d_1, R.id.text_widget_d_2, R.id.text_widget_d_3, R.id.text_widget_d_more);
                } else {
                    setSchedule(remoteViews, dinner, R.id.text_widget_d_1, R.id.text_widget_d_2, R.id.text_widget_d_3, R.id.text_widget_d_more);
                }
            }

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), DailyDishAppWidgetProvider.class);
            clickIntent.setAction(DailyDishAppWidgetProvider.WIDGET_CLICK_ACTION);
//            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
//            clickIntent.putExtra(AddRecipeActivity.INTENT_EXTRA_RECIPE_ID, Long.parseLong(mName));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_layout_text, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
            //

        }
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    private void setSchedule(RemoteViews remoteViews, List<Recipe> breakfast, int text_widget_b_1, int text_widget_b_2, int text_widget_b_3, int text_widget_b_more) {
        if (breakfast.size() > 3) {
            showMore(remoteViews, text_widget_b_more, true);
        } else {
            showMore(remoteViews, text_widget_b_more, false);
        }
        setText(remoteViews, text_widget_b_1, null);
        setText(remoteViews, text_widget_b_2, null);
        setText(remoteViews, text_widget_b_3, null);
        for (int i = 0; i < breakfast.size(); i++) {
            int view = 0;
            switch (i) {
                case 0:
                    view = text_widget_b_1;
                    break;
                case 1:
                    view = text_widget_b_2;
                    break;
                case 2:
                    view = text_widget_b_3;
                    break;

            }
            if (view != 0) {
                setText(remoteViews, view, breakfast.get(i).getRecipeName());
            }
        }
    }

    private void showMore(RemoteViews remoteViews, int text_widget_b_more, boolean show) {
        remoteViews.setTextViewText(text_widget_b_more, MORE);
        if (show) {
            remoteViews.setViewVisibility(text_widget_b_more, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(text_widget_b_more, View.GONE);
        }
    }

    private void setText(RemoteViews remoteViews, int text_widget_b_more, String text) {
        if (text != null) {
            remoteViews.setTextViewText(text_widget_b_more, text);
            remoteViews.setViewVisibility(text_widget_b_more, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(text_widget_b_more, View.GONE);
        }
    }


    private void noSchedule(RemoteViews remoteViews, int text_widget_b_1, int text_widget_b_2, int text_widget_b_3, int text_widget_b_more) {
        remoteViews.setViewVisibility(text_widget_b_1, View.GONE);
        remoteViews.setViewVisibility(text_widget_b_2, View.GONE);
        remoteViews.setViewVisibility(text_widget_b_3, View.GONE);
        remoteViews.setViewVisibility(text_widget_b_more, View.VISIBLE);
        remoteViews.setTextViewText(text_widget_b_more, NO_SCHEDULES);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
