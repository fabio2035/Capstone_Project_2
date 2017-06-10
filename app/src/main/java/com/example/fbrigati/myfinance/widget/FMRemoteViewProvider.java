package com.example.fbrigati.myfinance.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;

import java.util.Calendar;

/**
 * Created by FBrigati on 08/06/2017.
 */

public class FMRemoteViewProvider extends RemoteViewsService {
    private static final String LOG_TAG = FMRemoteViewProvider.class.getSimpleName();
    private static final int MAX_PROGRESS = 100;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null){
                    data.close();
                }

                Calendar c = Calendar.getInstance();
                int month = c.get(Calendar.MONTH)+1;

                Log.v(LOG_TAG, "Inside onDataSetChanged.. getting cursor");
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        DataContract.BudgetEntry.buildBudgetWidgetUri(month),
                        null,
                        null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        com.example.fbrigati.myfinance.R.layout.widget_item_budget);

                String category = data.getString(3);
                Double spent = data.getDouble(5);
                Double goal = data.getDouble(4);
                Double percentage = spent/goal*100;

                StringBuilder text = new StringBuilder();
                text.append(spent).append(" in ").append(category);

                views.setTextViewText(com.example.fbrigati.myfinance.R.id.widget_amount_spent_category,
                        text.toString());
                views.setProgressBar(com.example.fbrigati.myfinance.R.id.widget_progbar,
                        MAX_PROGRESS, percentage.intValue(), false);

                //Fill intent for detail view...
                final Intent fillIntent = new Intent();

                Uri intentUri = DataContract.BudgetEntry.buildBudgetUri(data.getInt(0));
                fillIntent.setData(intentUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item_budget);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getLong(0);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
