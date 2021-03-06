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
    public static final String PATH_CUREX = "currencyex";


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

        //bounded columns for general statment...
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

        public static Uri buildStatsMonthUri(int month) {
            return CONTENT_URI.buildUpon().appendPath("stats")
                    .appendPath(String.valueOf(month)).build();
        }

        public static Uri buildWidgetDataUri() {
            return CONTENT_URI.buildUpon().appendPath("widget")
                    .appendPath("data").build();
        }

        public static int getMonthFromUri(Uri uri) {
            return Integer.parseInt(uri.getLastPathSegment());
        }

        public static String getAccountFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static int getDateFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static int getIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getLastPathSegment());
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

        public static final String[] CATEGORY_COLUMNS = {
                CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID,
                CategoryEntry.COLUMN_ACQUIRER_ID,
                CategoryEntry.COLUMN_CATEGORY_DEFAULT,
                CategoryEntry.COLUMN_CATEGORY_USER_KEY
        };

        //bounded columns...
        public static final int COL_CATEGORY_USER_KEY = 1;
        public static final int COL_CATEGORY_DEFAULT = 2;
        public static final int COL_ACQUIRER_ID = 3;



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

        //bounded columns...
        public static final int COL_MONTH = 1;
        public static final int COL_YEAR = 2;
        public static final int COL_AMOUNT = 3;
        public static final int COL_CATEGORY = 4;
        public static final int COL_SPENT = 5;

        //table columns names...
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_AMOUNT = "amount";


        public static Uri buildBudgetUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildBudgetWidgetUri(int month) {
            return CONTENT_URI.buildUpon().appendPath("widget")
                    .appendPath(String.valueOf(month)).build();
        }

        public static String getBudgetCategory(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static Uri buildBudgetMonth(int month) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(month)).build();
        }

        public static int getBudgetMonth(Uri uri){
            return Integer.parseInt(uri.getLastPathSegment());
        }

    }

    //Definition of currencies table *****
    public static final class CurrencyExEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUREX).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUREX;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUREX;

        // Table name
        public static final String TABLE_NAME = "currencyex";

        public static final String[] CURRENCIES_COLUMNS = {
                CurrencyExEntry.TABLE_NAME + "." + CurrencyExEntry._ID,
                CurrencyExEntry.COLUMN_SYMBOL,
                CurrencyExEntry.COLUMN_RATE,
                CurrencyExEntry.COLUMN_DATE};


        //bounded columns...
        public static final int COL_SYMBOL = 1;
        public static final int COL_RATE = 2;
        public static final int COL_DATE = 3;

        //table columns names...
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_DATE = "date";


        public static Uri buildCurrencyExUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCurrencyUri(String symbol) {
            return CONTENT_URI.buildUpon().appendPath(symbol).build();
        }

        public static String getBaseCurrenyFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }

    }

}
