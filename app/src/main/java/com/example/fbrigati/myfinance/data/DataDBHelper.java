package com.example.fbrigati.myfinance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fbrigati.myfinance.data.DataContract.BudgetEntry;
import com.example.fbrigati.myfinance.data.DataContract.CategoryEntry;
import com.example.fbrigati.myfinance.data.DataContract.StatementEntry;
import com.example.fbrigati.myfinance.data.DataContract.CurrencyExEntry;
import com.example.fbrigati.myfinance.elements.Budget;
import com.example.fbrigati.myfinance.elements.Category;
import com.example.fbrigati.myfinance.elements.Statement;

/**
 * Created by FBrigati on 26/04/2017.
 */

public class DataDBHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "finance.db";

    public DataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creation of statements table
        final String SQL_CREATE_STATEMENT_TABLE = "CREATE TABLE " + StatementEntry.TABLE_NAME + " (" +
                StatementEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StatementEntry.COLUMN_ACCOUNT_NUMBER + " TEXT NOT NULL, " +
                StatementEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                StatementEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                StatementEntry.COLUMN_SEQUENCE + " INTEGER NOT NULL, " +
                StatementEntry.COLUMN_DESCRIPTION_ORIGIN + " TEXT NOT NULL, " +
                StatementEntry.COLUMN_DESCRIPTION_USER + " TEXT NOT NULL, " +
                StatementEntry.COLUMN_AMOUNT + " REAL NOT NULL DEFAULT 0, " +
                StatementEntry.COLUMN_TRANSACTION_CODE + " INTEGER NOT NULL, " +
                StatementEntry.COLUMN_ACQUIRER_ID + " TEXT NOT NULL, " +
                StatementEntry.COLUMN_CATEGORY_KEY + " TEXT NOT NULL, " +

                // Set up the category column as a foreign key to location table.
                " FOREIGN KEY (" + StatementEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + ") " +
                " );";

        //Creation of category table
        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryEntry.COLUMN_CATEGORY_USER_KEY + " TEXT NOT NULL DEFAULT ' ', " +
                CategoryEntry.COLUMN_CATEGORY_DEFAULT + " TEXT NOT NULL DEFAULT 'NA', " +
                CategoryEntry.COLUMN_ACQUIRER_ID + " TEXT NOT NULL DEFAULT 'NA', " +
                // to assure user does not repeat categories
                " UNIQUE (" + CategoryEntry.COLUMN_CATEGORY_USER_KEY + "));";


        //Creation of budget table
        final String SQL_CREATE_BUDGET_TABLE = "CREATE TABLE " + BudgetEntry.TABLE_NAME + " (" +
                BudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BudgetEntry.COLUMN_MONTH + " INTEGER NOT NULL, " +
                BudgetEntry.COLUMN_YEAR + " INTEGER NOT NULL, " +
                BudgetEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
                BudgetEntry.COLUMN_CATEGORY + " TEXT NOT NULL DEFAULT 'ALL', " +

                // to assure user does not repeat budgets for different categories..
                " UNIQUE (" + BudgetEntry.COLUMN_MONTH + ", " +
                BudgetEntry.COLUMN_YEAR + ", " + BudgetEntry.COLUMN_CATEGORY + "));";

        //Creation of currencyex table
        final String SQL_CREATE_CUREX_TABLE = "CREATE TABLE " + CurrencyExEntry.TABLE_NAME + " (" +
                CurrencyExEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CurrencyExEntry.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                CurrencyExEntry.COLUMN_RATE + " REAL NOT NULL, " +
                CurrencyExEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                "UNIQUE (" + CurrencyExEntry.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_STATEMENT_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_BUDGET_TABLE);
        db.execSQL(SQL_CREATE_CUREX_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + StatementEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BudgetEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CurrencyExEntry.TABLE_NAME);
        onCreate(db);
    }
}
