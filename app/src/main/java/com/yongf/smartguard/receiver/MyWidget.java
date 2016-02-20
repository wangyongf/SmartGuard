package com.yongf.smartguard.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.yongf.smartguard.R;
import com.yongf.smartguard.service.UpdateWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);

        System.out.println("MyWidget.onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
        System.out.println("MyWidget.onUpdate");
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
        System.out.println("MyWidget.onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(intent);
        intent = null;
        System.out.println("MyWidget.onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

