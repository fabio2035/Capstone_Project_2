package com.example.fbrigati.myfinance.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;
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
    //static final int STATEMENT_WITH_ID = 101;
    static final int STATEMENT_WITH_ACCTNUMBER = 101;
    static final int STATEMENT_WITH_ACCTNUMBER_DATE = 102;
    static final int CATEGORY = 200;
    static final int CATEGORY_WITH_ACQUIRER = 201;
    static final int BUDGET = 300;
    static final int BUDGET_WITH_YEAR_MONTH = 301;

    private static final SQLiteQueryBuilder mStatementQueryBuilder;

    static{
        mStatementQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //statement INNER JOIN category ON statement.category = category._id
        mStatementQueryBuilder.setTables(
                DataContract.StatementEntry.TABLE_NAME + " INNER JOIN " +
                        DataContract.CategoryEntry.TABLE_NAME +
                        " ON " + DataContract.StatementEntry.TABLE_NAME +
                        "." + DataContract.StatementEntry.COLUMN_CATEGORY_KEY +
                        " = " + DataContract.CategoryEntry.TABLE_NAME +
                        "." + DataContract.CategoryEntry._ID);
    }


    //statement.account = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            DataContract.StatementEntry.TABLE_NAME +
                    "." + DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER + " = ? AND " +
                    DataContract.StatementEntry.COLUMN_DATE + " = ? ";


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
        //matcher.addURI(authority, DataContract.PATH_STATEMENT + "/*", STATEMENT_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_STATEMENT + "/#", STATEMENT_WITH_ACCTNUMBER);
        matcher.addURI(authority, DataContract.PATH_STATEMENT + "/#/*", STATEMENT_WITH_ACCTNUMBER_DATE);

        matcher.addURI(authority, DataContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, DataContract.PATH_CATEGORY + "/*", CATEGORY_WITH_ACQUIRER);

        matcher.addURI(authority, DataContract.PATH_BUDGET, BUDGET);
        matcher.addURI(authority, DataContract.PATH_BUDGET + "/#/*", BUDGET_WITH_YEAR_MONTH);

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
            case BUDGET: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.BudgetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getStatementByAccount(Uri uri, String[] projection, String sortOrder) {

            String acctNumber = DataContract.StatementEntry.getAccountFromUri(uri);
            int date = DataContract.StatementEntry.getDateFromUri(uri);

            return mStatementQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sLocationSettingAndDaySelection,
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
            case BUDGET_WITH_YEAR_MONTH:
                return DataContract.BudgetEntry.CONTENT_ITEM_TYPE;
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
}