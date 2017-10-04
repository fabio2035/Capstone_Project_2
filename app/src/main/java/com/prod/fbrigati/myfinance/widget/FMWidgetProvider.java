package com.prod.fbrigati.myfinance.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;

import com.prod.fbrigati.myfinance.ItemListActivity;
import com.prod.fbrigati.myfinance.R;
import com.prod.fbrigati.myfinance.data.DataDBHelper;
import com.prod.fbrigati.myfinance.sync.MFSyncJob;
import com.prod.fbrigati.myfinance.ui.BudgetActivity;

import static com.prod.fbrigati.myfinance.R.id.widget;

/**
 * Created by FBrigati on 07/06/2017.
 */

public class FMWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_ACTION = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final String LOG_TAG = FMWidgetProvider.class.getSimpleName();
    private Cursor generalData = null;
    private RemoteViews rview;
    private DataDBHelper db;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        //perform loop for every app widget in this provider
        for(int i = 0; i < appWidgetIds.length; ++i){

            rview = new RemoteViews(context.getPackageName(),
                    R.layout.widget_main);

            //Intent to launch main activity
            Intent intent = new Intent(context, ItemListActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rview.setOnClickPendingIntent(widget, pendingIntent);

            rview.setRemoteAdapter(R.id.widget_collection, new Intent(context, FMRemoteViewProvider.class));

            showEmptyviews(setGeneralData(context));

            Intent clickIntentTemplate = new Intent(context, BudgetActivity.class);
            PendingIntent clickPendingIntentTemplate = android.support.v4.app.TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rview.setPendingIntentTemplate(R.id.widget_collection, clickPendingIntentTemplate);

            //Empty view used for when there are no items to display
            rview.setEmptyView(R.id.widget_collection, R.id.empty_widget);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rview);
        }
      //  super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void showEmptyviews(boolean show) {

        //if true then hide views...
        if(show) {
            rview.setViewVisibility(R.id.top_label, View.INVISIBLE);
            rview.setViewVisibility(R.id.widget_today_label, View.INVISIBLE);
            rview.setViewVisibility(R.id.widget_amount_day, View.INVISIBLE);
            rview.setViewVisibility(R.id.widget_week_label, View.INVISIBLE);
            rview.setViewVisibility(R.id.widget_amount_week, View.INVISIBLE);
            rview.setViewVisibility(R.id.widget_month_label, View.INVISIBLE);
            rview.setViewVisibility(R.id.widget_amount_month, View.INVISIBLE);
        }else{
            rview.setViewVisibility(R.id.top_label, View.VISIBLE);
            rview.setViewVisibility(R.id.widget_today_label, View.VISIBLE);
            rview.setViewVisibility(R.id.widget_amount_day, View.VISIBLE);
            rview.setViewVisibility(R.id.widget_week_label, View.VISIBLE);
            rview.setViewVisibility(R.id.widget_amount_week, View.VISIBLE);
            rview.setViewVisibility(R.id.widget_month_label, View.VISIBLE);
            rview.setViewVisibility(R.id.widget_amount_month, View.VISIBLE);
        }
    }



    private boolean setGeneralData(Context ctx) {

        Double[] values = new Double[3];
        Double total = 0.0;

        db = new DataDBHelper(ctx);
        SQLiteDatabase dbh = db.getReadableDatabase();

        //fill view for generalData...
        Cursor generalData = dbh.rawQuery(
                "SELECT ifnull((SELECT sum(amount) amt FROM statement as a " +
                        "WHERE a.date = strftime('%Y','now') || strftime('%m','now') || strftime('%d','now') " +
                        "), 0) " +
                        "UNION ALL " +
                "SELECT ifnull((SELECT sum(amount) amt FROM statement as b " +
                        "WHERE strftime('%W',strftime('%Y-%m-%d', substr(b.date,1,4) || '-' || substr(b.date,5,2) || '-' || substr(b.date,7,2))) " +
                        "= strftime('%W', 'now') " + // 20170609 date('now', 'weekday 0', '-7 days') " +
                        "), 0) "  +
                        "UNION ALL " +
                "SELECT ifnull(" +
                        "(SELECT sum(amount) amt FROM statement as c " +
                        "WHERE substr(c.date,5,2)*1 = cast(strftime('%m','now') as INT)*1 " + ///date('now', '%m')
                        "), 0)",null);

        generalData.moveToPosition(0);


        for(int j=0 ; j<generalData.getCount(); j++) {
            switch (j) {
                case 0:{
                    values[j] = generalData.getDouble(0);
                    rview.setTextViewText(R.id.widget_amount_day,
                            String.valueOf(generalData.getDouble(0)));
                    break;}
                case 1:{
                    values[j] = generalData.getDouble(0);
                    rview.setTextViewText(R.id.widget_amount_week,
                            String.valueOf(generalData.getDouble(0)));
                    break;}
                case 2:{
                    values[j] = generalData.getDouble(0);
                    rview.setTextViewText(R.id.widget_amount_month,
                            String.valueOf(generalData.getDouble(0)));
                    break;}
            }
            generalData.moveToNext();
        }
        generalData.close();

        for (int i=0; i < values.length; i++){
            total = total + values[i];
        }
        //If total of values is bigger than 0, then user has
        //already registered something, show textViews...
        if (total>0.0){
            return false;
        }else{
            return true;
        }
    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (MFSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            rview = new RemoteViews(context.getPackageName(),
                    R.layout.widget_main);
            showEmptyviews(setGeneralData(context));
            ComponentName thisWidget = new ComponentName(context, FMWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_collection);
            appWidgetManager.updateAppWidget(thisWidget, rview);
        }
    }

}
