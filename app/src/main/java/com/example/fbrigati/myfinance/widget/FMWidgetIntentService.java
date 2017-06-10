package com.example.fbrigati.myfinance.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.fbrigati.myfinance.R;

/**
 * Created by FBrigati on 07/06/2017.
 */

public class FMWidgetIntentService extends IntentService {


    private static final String LOG_TAG = FMWidgetIntentService.class.getSimpleName();

    /*
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FMWidgetIntentService() {
        super("FMWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                SingleWidgetProvider.class));

        Log.v(LOG_TAG, "Inside appWidget, items: " + appWidgetIds.length);

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.widget_main);
            //views.setTextViewText(R.id.widget_today_spendings, "250");
            //views.setTextViewText(R.id.widget_month_spendings, "100");

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
