package com.example.fbrigati.myfinance.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by FBrigati on 26/04/2017.
 */

public class DataContract {

    //name of content provider
    public static final String CONTENT_AUTHORITY = "com.example.fbrigati.myfinance";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //possible paths
    public static final String PATH_STATEMENT = "statement";
    public static final String PATH_BUDGET = "budget";
    public static final String PATH_CATEGORY = "category";


    //Definition of satement table *****
    public static final class StatementEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATEMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATEMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATEMENT;

        // Table name
        public static final String TABLE_NAME = "statement";

        public static final String[] STATEMENT_COLUMNS = {
                StatementEntry.TABLE_NAME + "." + StatementEntry._ID,
                StatementEntry.COLUMN_ACCOUNT_NUMBER,
                StatementEntry.COLUMN_DATE,
                StatementEntry.COLUMN_TIME,
                StatementEntry.COLUMN_SEQUENCE,
                StatementEntry.COLUMN_DESCRIPTION_ORIGIN,
                StatementEntry.COLUMN_DESCRIPTION_USER,
                StatementEntry.COLUMN_AMOUNT,
                StatementEntry.COLUMN_TRANSACTION_CODE,
                StatementEntry.COLUMN_ACQUIRER_ID,
                StatementEntry.COLUMN_CATEGORY_KEY
        };

        //bounded columns...
        public static final int COL_ACCOUNT_NUMBER = 1;
        public static final int COL_DATE = 2;
        public static final int COL_TIME = 3;
        public static final int COL_SEQUENCE = 4;
        public static final int COL_DESCRIPTION_ORIGIN = 5;
        public static final int COL_DESCRIPTION_USER = 6;
        public static final int COL_AMOUNT = 7;
        public static final int COL_TRANSACTION_CODE = 8;
        public static final int COL_ACQUIRER_ID = 9;
        public static final int COL_CATEGORY_KEY = 10;

        //table columns names...
        public static final String COLUMN_ACCOUNT_NUMBER = "accountNumber";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_SEQUENCE = "sequence";
        public static final String COLUMN_DESCRIPTION_ORIGIN = "desc_origin";
        public static final String COLUMN_DESCRIPTION_USER = "desc_user";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TRANSACTION_CODE = "trxcode";
        public static final String COLUMN_ACQUIRER_ID = "acquirer_id";
        public static final String COLUMN_CATEGORY_KEY = "category";

        public static Uri buildStatementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getAccountFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static int getDateFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
    }

    //Definition of categroy table *****
    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "category";

        //table columns names...
        public static final String COLUMN_ACQUIRER_ID = "acquirer_id";
        public static final String COLUMN_CATEGORY_DEFAULT = "desc_default";
        public static final String COLUMN_CATEGORY_USER_KEY = "category_id";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    //Definition of budget table *****
    public static final class BudgetEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUDGET).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGET;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUDGET;

        // Table name
        public static final String TABLE_NAME = "budget";

        //table columns names...
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_AMOUNT = "amount";


        public static Uri buildBudgetUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
