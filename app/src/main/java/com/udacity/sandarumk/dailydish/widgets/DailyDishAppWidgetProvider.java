package com.udacity.sandarumk.dailydish.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.udacity.sandarumk.dailydish.activities.MainActivity;

public class DailyDishAppWidgetProvider extends AppWidgetProvider {

    public static String WIDGET_CLICK_ACTION = "WIDGET_CLICK_ACTION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, DailyDishAppWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(), ScheduleWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (WIDGET_CLICK_ACTION.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            context.startActivity(activityIntent);
        }
    }
}
