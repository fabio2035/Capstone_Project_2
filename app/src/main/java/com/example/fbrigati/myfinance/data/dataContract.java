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
        public static final String COLUMN_CATEGORY_USER = "category";

        public static Uri buildStatementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
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
        public static final String COLUMN_DESCRIPTION_DEFAULT = "desc_default";
        public static final String COLUMN_DESCRIPTION_USER = "desc_user";

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
