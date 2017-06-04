package com.example.fbrigati.myfinance.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.fbrigati.myfinance.ui.StatementFragment;

/**
 * Created by FBrigati on 27/04/2017.
 */

public class DataProvider extends ContentProvider {

    final static String LOG_TAG = DataProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDBHelper mOpenHelper;

    static final int STATEMENT = 100;
    static final int STATEMENT_WITH_ID = 101;
    static final int STATEMENT_WITH_ACCTNUMBER = 102;
    static final int STATEMENT_WITH_ACCTNUMBER_DATE = 103;
    static final int CATEGORY = 200;
    static final int CATEGORY_WITH_ACQUIRER = 201;
    static final int BUDGET = 300;
    static final int BUDGET_WITH_MONTH = 301;
    static final int CUREX = 400;
    static final int CUREX_WITH_BASE = 401;

    private static final SQLiteQueryBuilder mStatementQueryBuilder;
    private static final SQLiteQueryBuilder mCurrencyQueryBuilder;



    static{
        mStatementQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //statement LEFT JOIN category ON statement.category = category._id
        mStatementQueryBuilder.setTables(
                DataContract.StatementEntry.TABLE_NAME + " LEFT JOIN " +
                        DataContract.CategoryEntry.TABLE_NAME +
                        " ON " + DataContract.StatementEntry.TABLE_NAME +
                        "." + DataContract.StatementEntry.COLUMN_CATEGORY_KEY +
                        " = " + DataContract.CategoryEntry.TABLE_NAME +
                        "." + DataContract.CategoryEntry.COLUMN_CATEGORY_USER_KEY);
    }

    static{
        mCurrencyQueryBuilder = new SQLiteQueryBuilder();
        mCurrencyQueryBuilder.setTables(DataContract.CurrencyExEntry.TABLE_NAME);}



    //statement._ID = ?
    private static final String sStatementIDSelection =
            DataContract.StatementEntry.TABLE_NAME +
                    "." + DataContract.StatementEntry._ID + " = ?";


        //currencyex.symbol like '%Base'
    private static final String sBaseCurrencySelection =
            DataContract.CurrencyExEntry.TABLE_NAME +
                    "." + DataContract.CurrencyExEntry.COLUMN_SYMBOL + " like ?";


    //statement.account = ? AND date = ?
    private static final String sAcctnumberAndDateSelection =
            DataContract.StatementEntry.TABLE_NAME +
                    "." + DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER + " = ? AND " +
                    DataContract.StatementEntry.COLUMN_DATE + " = ? ";

    private Cursor getCurrenciesByBaseCurrency(
            Uri uri, String[] projection, String sortOrder) {
        String baseCurrency = DataContract.CurrencyExEntry.getBaseCurrenyFromUri(uri);
        Log.v(LOG_TAG, "baseCurrency: " + baseCurrency);
        return mCurrencyQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sBaseCurrencySelection,
                new String[]{"%" + baseCurrency},
                null,
                null,
                sortOrder
        );
    }


    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DataContract.PATH_STATEMENT, STATEMENT);
        matcher.addURI(authority, DataContract.PATH_STATEMENT + "/*", STATEMENT_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_STATEMENT + "/#", STATEMENT_WITH_ACCTNUMBER);
        matcher.addURI(authority, DataContract.PATH_STATEMENT + "/#/*", STATEMENT_WITH_ACCTNUMBER_DATE);

        matcher.addURI(authority, DataContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, DataContract.PATH_CATEGORY + "/*", CATEGORY_WITH_ACQUIRER);

        matcher.addURI(authority, DataContract.PATH_BUDGET, BUDGET);
        matcher.addURI(authority, DataContract.PATH_BUDGET + "/#", BUDGET_WITH_MONTH);

        matcher.addURI(authority, DataContract.PATH_CUREX, CUREX);
        matcher.addURI(authority, DataContract.PATH_CUREX + "/*", CUREX_WITH_BASE);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new DataDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // statement/*
            case STATEMENT_WITH_ID:
            {
                retCursor = getStatementByID(uri, projection, sortOrder);
                break;
            }
            // statement/#"
            case STATEMENT_WITH_ACCTNUMBER:
            {
                retCursor = getStatementByAccount(uri, projection, sortOrder);
                break;
            }
            // "statement/#/*"
            case STATEMENT_WITH_ACCTNUMBER_DATE: {
                retCursor = getStatementByAccount(uri, projection, sortOrder);
                break;
            }
            // "statement"
            case STATEMENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.StatementEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Category"
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Budget"
            case BUDGET_WITH_MONTH: {
                retCursor = getBudgetWithMonth(uri, projection, sortOrder);
                break;
            }
            // "Currencies"
            case CUREX: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.CurrencyExEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Currencies"
            case CUREX_WITH_BASE: {
                Log.v(LOG_TAG, "inside CUREX_WITH_BASE");
                retCursor = getCurrenciesByBaseCurrency(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getBudgetWithMonth(Uri uri, String[] projection, String sortOrder) {

        //String category = DataContract.BudgetEntry.getBudgetCategory(uri);
        int month = DataContract.BudgetEntry.getBudgetMonth(uri);

        return mOpenHelper.getReadableDatabase().rawQuery(
                "select S._ID, S.month, S.year , S.category, S.amount, T.amount From budget as S inner join " +
                        "(select substr(a.date,5,2)*1 as Month, sum(amount) amount, category from statement as a " +
                        "group by category, substr(a.date,5,2)*1) as T ON " +
                        "S.month = T.month and S.category = T.category " +
                        "where S.month = ? ",new String[] {String.valueOf(month)});

    }

    private Cursor getStatementByID(Uri uri, String[] projection, String sortOrder) {

        String ID = String.valueOf(DataContract.StatementEntry.getIDFromUri(uri));

        Log.v(LOG_TAG, "ID = " +ID);

        return mStatementQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                new String[]{ DataContract.StatementEntry.COLUMN_TRANSACTION_CODE,
                DataContract.StatementEntry.COLUMN_CATEGORY_KEY,
                DataContract.StatementEntry.COLUMN_TRANSACTION_CODE,
                DataContract.StatementEntry.COLUMN_DESCRIPTION_USER,
                DataContract.StatementEntry.COLUMN_DATE,
                DataContract.StatementEntry.COLUMN_TIME,
                DataContract.StatementEntry.COLUMN_AMOUNT,
                DataContract.CategoryEntry.COLUMN_CATEGORY_USER_KEY,
                DataContract.StatementEntry.COLUMN_TRANSACTION_CODE},
                sStatementIDSelection,
                new String[]{ID},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getStatementByAccount(Uri uri, String[] projection, String sortOrder) {

            String acctNumber = DataContract.StatementEntry.getAccountFromUri(uri);
            int date = DataContract.StatementEntry.getDateFromUri(uri);

            return mStatementQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sAcctnumberAndDateSelection,
                    new String[]{acctNumber, Integer.toString(date)},
                    null,
                    null,
                    sortOrder
            );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match){
            case STATEMENT:
                return DataContract.StatementEntry.CONTENT_TYPE;
            case STATEMENT_WITH_ACCTNUMBER:
                return DataContract.StatementEntry.CONTENT_TYPE;
            case STATEMENT_WITH_ACCTNUMBER_DATE:
                return DataContract.StatementEntry.CONTENT_TYPE;
            case CATEGORY:
                return DataContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_WITH_ACQUIRER:
                return DataContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case BUDGET:
                return DataContract.BudgetEntry.CONTENT_TYPE;
            case BUDGET_WITH_MONTH:
                return DataContract.BudgetEntry.CONTENT_ITEM_TYPE;
            case CUREX:
                return DataContract.CurrencyExEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        Log.v(LOG_TAG, "inside provider insert method");

        switch(match){
            case STATEMENT:{
                //normalizeData();
                Log.v(LOG_TAG, "About to execute insert os statement values..");
                long _id = db.insert(DataContract.StatementEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    Log.v(LOG_TAG, "Inserted something, returned value: " + _id);
                    returnUri = DataContract.StatementEntry.buildStatementUri(_id);}
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY:{
                long _id = db.insert(DataContract.CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DataContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BUDGET:{
                long _id = db.insert(DataContract.BudgetEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DataContract.BudgetEntry.buildBudgetUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CUREX:{
                long _id = db.insert(DataContract.CurrencyExEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DataContract.CurrencyExEntry.buildCurrencyExUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case STATEMENT:
                rowsDeleted = db.delete(
                        DataContract.StatementEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(
                        DataContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BUDGET:
                rowsDeleted = db.delete(
                        DataContract.BudgetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CUREX:
                rowsDeleted = db.delete(
                        DataContract.CurrencyExEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            int rowsUpdated;

            switch (match) {
                case STATEMENT:
                    //normalizeDate(values);
                    rowsUpdated = db.update(DataContract.StatementEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                case CATEGORY:
                    rowsUpdated = db.update(DataContract.CategoryEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CUREX:
                db.beginTransaction();
                int returnCount = 0;
                Log.v(LOG_TAG, "About to insert values in currencyExchange..");
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                DataContract.CurrencyExEntry.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
