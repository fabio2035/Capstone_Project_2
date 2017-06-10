package com.example.fbrigati.myfinance.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.fbrigati.myfinance.MainActivity;
import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.data.DataDBHelper;
import com.example.fbrigati.myfinance.sync.MFSyncJob;
import com.example.fbrigati.myfinance.ui.BudgetActivity;

/**
 * Created by FBrigati on 07/06/2017.
 */

public class FMWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_ACTION = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final String LOG_TAG = FMWidgetProvider.class.getSimpleName();
    private Cursor generalData = null;
    private RemoteViews rview;
    private DataDBHelper db;


    //@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        //perform loop for every app widget in this provider
        for(int i = 0; i < appWidgetIds.length; ++i){

            rview = new RemoteViews(context.getPackageName(),
                    R.layout.widget_main);

            Log.v(LOG_TAG, "Inside onUpdate, with item number: " + i);
            //Intent to launch main activity
            Intent intent = new Intent(context, MainActivity.class);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            //intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rview.setOnClickPendingIntent(R.id.widget, pendingIntent);

            rview.setRemoteAdapter(R.id.widget_collection, new Intent(context, FMRemoteViewProvider.class));


            setGeneralData(context);


            Intent clickIntentTemplate = new Intent(context, BudgetActivity.class);
            PendingIntent clickPendingIntentTemplate = android.support.v4.app.TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rview.setPendingIntentTemplate(R.id.widget_collection, clickPendingIntentTemplate);

            //Empty view used for when there are no items to display
            rview.setEmptyView(R.id.widget_collection, R.id.empty_widget);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rview);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private void setGeneralData(Context ctx) {

        db = new DataDBHelper(ctx);
        SQLiteDatabase dbh = db.getReadableDatabase();

        //fill view for generalData...
        Log.v(LOG_TAG, "Inside onDataSetChanged.. getting generalData cursor");
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

        Log.v(LOG_TAG, "number of rows in cursor: " + generalData.getCount());

        for(int j=0 ; j<generalData.getCount(); j++) {
            switch (j) {
                case 0:{
                    Log.v(LOG_TAG, "setting for today amuont: " + j + "," + generalData.getDouble(0));
                    rview.setTextViewText(R.id.widget_amount_day,
                            String.valueOf(generalData.getDouble(0)));
                    break;}
                case 1:{
                    Log.v(LOG_TAG, "setting for week amuont: "  + j + "," + generalData.getDouble(0));
                    rview.setTextViewText(R.id.widget_amount_week,
                            String.valueOf(generalData.getDouble(0)));
                    break;}
                case 2:{
                    Log.v(LOG_TAG, "setting for month amuont: "  + j + "," + generalData.getDouble(0));
                    rview.setTextViewText(R.id.widget_amount_month,
                            String.valueOf(generalData.getDouble(0)));
                    break;}
            }
            generalData.moveToNext();
        }
    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        Log.v(LOG_TAG, "Intent caught: " + intent.getAction().toString());
        if (MFSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Log.v(LOG_TAG, "Inside onReceive.. supposedly updating data...");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_collection);
        }
    }

}
