package com.example.fbrigati.myfinance.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by FBrigati on 07/06/2017.
 */

public class SingleWidgetProvider extends AppWidgetProvider {

    final static String LOG_TAG = SingleWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        //perform this loop for every App Widget in this provider
        Log.v(LOG_TAG, "Inside Widget.. number of widgets: " + appWidgetIds.length);
        context.startService(new Intent(context, FMWidgetIntentService.class));
    }

}
