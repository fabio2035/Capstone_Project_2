package com.example.fbrigati.myfinance.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by FBrigati on 21/07/2017.
 */

public class BudgetLoader extends CursorLoader {


    public static BudgetLoader newInstance(Context context, int month){

        return new BudgetLoader(context,
                DataContract.BudgetEntry.buildBudgetMonth(month),
                null,
                null,
                null,
                null);
    }

    private BudgetLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }
}
