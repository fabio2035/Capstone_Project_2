package com.example.fbrigati.myfinance.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.example.fbrigati.myfinance.Utility;

/**
 * Created by FBrigati on 21/07/2017.
 */

public class StatementLoader extends CursorLoader {


    public static StatementLoader newInstance(Context context, int month){

        String[] selectionArgs = {String.format("%02d", month)};
        String selection = "substr(date,5,2) = ?";

        return new StatementLoader(context,
                DataContract.StatementEntry.CONTENT_URI,
                DataContract.StatementEntry.STATEMENT_COLUMNS,
                selection,
                selectionArgs,
                null
                );
    }

    private StatementLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }
}
