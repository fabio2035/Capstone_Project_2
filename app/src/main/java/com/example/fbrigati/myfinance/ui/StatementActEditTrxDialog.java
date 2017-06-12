package com.example.fbrigati.myfinance.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Statement;
import com.example.fbrigati.myfinance.sync.MFSyncJob;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.security.AccessController.getContext;

/**
 * Created by FBrigati on 25/05/2017.
 */

public class StatementActEditTrxDialog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerFragment.DateSetListenerCustom, TimePickerFragment.TimeSetListenerCustom {

    final static String LOG_TAG = StatementActEditTrxDialog.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.StatementFragEditTrxDialog.MESSAGE";

    public static final int CATEGORY_LOADER = 2;
    public static final int STATEMENT_LOADER_ID = 3;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView amountText;
    private TextView descText;
    private Button datePickerBtn;
    private Button timePickerBtn;
    private Spinner spinner_ctg;
    private Spinner spinner_trxType;
    private Button save_btn;
    private Button close_btn;

    private List<String> lables = null;
    private ArrayAdapter<String> categoryAdapter;

    Map<String, Integer> categoryMap;

    private Uri detailUri;

    //Firebase variables
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mStatementDatabaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.fragment_transaction_edit);

        if(savedInstanceState == null){
            detailUri = getIntent().getData();
            if(detailUri != null){
            Log.v(LOG_TAG, "Uri from Intent: " + detailUri);
            Log.v(LOG_TAG, "Initialising new data loader..");
                //First load the category items
            getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
            getSupportLoaderManager().initLoader(STATEMENT_LOADER_ID, null, this);}
            else{
                //if its a new transaction only load category items..
                getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);}
        }

        //Initialize cursor



        lables = new ArrayList<String>();
        //View rootView = inflater.inflate(R.layout.fragment_transaction_edit, container, false);

        amountText = (TextView) findViewById(R.id.amount_id);

        descText = (TextView) findViewById(R.id.description_id);

        spinner_ctg = (Spinner) findViewById(R.id.category_spinner_id);

        spinner_trxType = (Spinner) findViewById(R.id.trxType_spinner_id);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.trx_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_trxType.setAdapter(adapter);

        datePickerBtn = (Button) findViewById(R.id.date_picker_btn);
        timePickerBtn = (Button) findViewById(R.id.time_picker_btn);

        //user action to choose date from picker
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerDialog = new DatePickerFragment();
                datePickerDialog.show(getSupportFragmentManager(), "datePicker");
            }
        });

        //user action to chose time from picker
        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerDialog = new TimePickerFragment();
                timePickerDialog.show(getSupportFragmentManager(), "timePicker");
            }
        });

        save_btn = (Button) findViewById(R.id.edit_trans_save_btn);

        close_btn = (Button) findViewById(R.id.edit_trans_close_btn);

        //user action to close current dialog
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(validateInputs()) saveDataToDB(); else Toast.makeText(getApplicationContext(), R.string.toast_check_desc_amt, Toast.LENGTH_LONG).show();
            }
        });

        categoryMap = new HashMap<String,Integer>();

        if (detailUri == null){
            setCurrentDateTimeButtons();
        }

        //Firebase stuff...
        //initialize object variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsername = ANONYMOUS;
        mStatementDatabaseReference = mFirebaseDatabase.getReference().child("statement");
    }


    private void setCurrentDateTimeButtons() {
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH) + 1;
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        showDate(year, month,day);
        showTime(hour, minute);
    }

    private void saveDataToDB() {
        ContentValues cv = new ContentValues();

        Integer trxType = 0;

        String dateStr = String.format("%08d", Integer.parseInt(datePickerBtn.getText().toString().replace("/", "")));
        StringBuilder dateBuild = new StringBuilder().append(dateStr.substring(4)).append(dateStr.substring(2,4)).append(dateStr.substring(0,2));
        Log.v(LOG_TAG, "DateStr: " + dateBuild); //31052017
        String timeStr = String.format("%04d", Integer.parseInt(timePickerBtn.getText().toString().replace(":","")));

        Integer dateInt = Integer.parseInt(dateBuild.toString());
        Integer timeInt = Integer.parseInt(timeStr);

        if(spinner_trxType.getSelectedItem().equals("Debit")) trxType = 6; else trxType =0;

        Log.v(LOG_TAG, "Date: " + dateStr + "," + dateInt + ", Time: " +
                timeStr + "," + timeInt + ", Transaction code: " + trxType
                + ", Category key: " + spinner_ctg.getSelectedItem().toString()
                + "");

        cv.put(DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER, "0529925801");
        cv.put(DataContract.StatementEntry.COLUMN_DATE, dateInt);
        cv.put(DataContract.StatementEntry.COLUMN_TIME, timeStr);
        cv.put(DataContract.StatementEntry.COLUMN_SEQUENCE, 0);
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_ORIGIN, descText.getText().toString());
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_USER, descText.getText().toString());
        cv.put(DataContract.StatementEntry.COLUMN_AMOUNT, Double.valueOf(amountText.getText().toString()));
        cv.put(DataContract.StatementEntry.COLUMN_TRANSACTION_CODE, trxType);
        cv.put(DataContract.StatementEntry.COLUMN_ACQUIRER_ID, "0");
        cv.put(DataContract.StatementEntry.COLUMN_CATEGORY_KEY, spinner_ctg.getSelectedItem().toString());

        Uri uri = getContentResolver().insert(DataContract.StatementEntry.CONTENT_URI, cv);

        int execNum = DataContract.StatementEntry.getIDFromUri(uri);

        if(execNum > 0){
        Toast.makeText(getApplicationContext(), R.string.toast_savetodbsuccess, Toast.LENGTH_LONG).show();

        updateWidgets();

        //if successfully saved to DB we can Save to Firbase aswell...
        Statement statementData = new Statement(DataContract.StatementEntry.getIDFromUri(uri),
                "0529925801", dateInt, timeStr,0, descText.getText().toString(), descText.getText().toString(),
                Double.valueOf(amountText.getText().toString()),trxType,"0",spinner_ctg.getSelectedItem().toString());
        mStatementDatabaseReference.push().setValue(statementData);}

        finish();
    }

    public void updateWidgets() {
        Intent dataUpdatedIntent = new Intent(MFSyncJob.ACTION_DATA_UPDATED)
                .setPackage(this.getClass().getPackage().getName());
        sendBroadcast(dataUpdatedIntent);
    }



    private boolean validateInputs() {
        boolean validate = true;

        //check length of description text
        if(descText.getText().length() < 1 ||
           descText.getText() == "" ||
           descText.getText() == null) {
            validate = false;
            descText.requestFocus();
        }

        //check that amount is >0
        if(Double.valueOf(amountText.getText().toString()) == 0F ||
           Double.valueOf(amountText.getText().toString()) == 0)
        {validate = false;
         amountText.requestFocus();}

        return validate;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case StatementFragment.STATEMENT_LOADER:
                Log.v(LOG_TAG, "statement cursor loader called");
                //Todo: make account selection
                //if(statement_uri!=null){
                //uri = DataContract.StatementEntry.buildStatementUri(statement_uri);
                /*return new CursorLoader(
                        getActivity(),
                        DataContract.StatementEntry.CONTENT_URI,
                        DataContract.StatementEntry.STATEMENT_COLUMNS,
                        null,
                        null,
                        null); */
            case CATEGORY_LOADER:{
                Log.v(LOG_TAG, "Category loader called");
                //Todo: make category selection
                return new CursorLoader(
                        getApplicationContext(),
                        DataContract.CategoryEntry.CONTENT_URI,
                        DataContract.CategoryEntry.CATEGORY_COLUMNS,
                        null,
                        null,
                        null);
            }
            case STATEMENT_LOADER_ID: {
                Log.v(LOG_TAG, "Category loader_id called");
                //Todo: make category selection
                return new CursorLoader(
                        getApplicationContext(),
                        detailUri,
                        DataContract.StatementEntry.STATEMENT_COLUMNS,
                        null,
                        null,
                        null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Todo:fill the view elements with data from database
        Log.v(LOG_TAG, "inside onLoadFinished method..");
        switch (loader.getId()) {
            case CATEGORY_LOADER:

                if (data != null && data.moveToFirst() && data.getCount() > 0) {

                    Log.v(LOG_TAG, "data crusor with: " + data.getString(DataContract.CategoryEntry.COL_CATEGORY_USER_KEY));

                    int i = 0;

                    do {
                        Log.v(LOG_TAG, "inserting key: " + data.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT)
                                + " | value: " + i);
                        i++;
                        categoryMap.put(data.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT), i);
                        lables.add(data.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT));
                    } while (data.moveToNext());

                    // Creating adapter for spinner
                    categoryAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, lables);

                    // Drop down layout style - list view with radio button
                    categoryAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner_ctg.setAdapter(categoryAdapter);
                }
                break;
            case STATEMENT_LOADER_ID:
                if (data != null && data.moveToFirst() && data.getCount() > 0) {

                    Log.v(LOG_TAG, "Amount: " + String.valueOf(data.getDouble(6)));

                    Log.v(LOG_TAG, "| description: " + data.getString(3));
                    Log.v(LOG_TAG, "| date: " + data.getInt(4));
                    Log.v(LOG_TAG, "| time: " + data.getString(5));
                    Log.v(LOG_TAG, "| Category: " + data.getString(7));

                    amountText.setText(String.valueOf(data.getDouble(6)));

                    descText.setText(data.getString(3));

                    Integer date = data.getInt(4);

                    String time = data.getString(5);

                    String dateStr = date.toString();

                    String category = data.getString(7).trim();

                    StringBuilder dateBuild = new StringBuilder().append(dateStr.substring(6,8)).append("/").append(dateStr.substring(4,6)).append("/").append(dateStr.substring(0,4));

                    StringBuilder timeBuild = new StringBuilder().append(time.substring(0,2)).append(":").append(time.substring(2,4));

                    datePickerBtn.setText(dateBuild.toString());

                    timePickerBtn.setText(timeBuild.toString());

                    //Pick category
                    for(int i=0; i<spinner_ctg.getCount(); i++){
                        Log.v(LOG_TAG, "Item position: " + spinner_ctg.getItemAtPosition(i).toString());
                        if(spinner_ctg.getItemAtPosition(i).toString().equals(categoryMap.get(category))){
                        spinner_ctg.setSelection(i);}
                    }

                    //Pick trx type
                    if(data.getInt(8)>5){
                        spinner_trxType.setSelection(0);
                    }else{
                        spinner_trxType.setSelection(1);
                    }


                }

                }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Inside swapcursor...");
        switch (loader.getId()){
            case CATEGORY_LOADER:
                if(categoryAdapter != null)
                    categoryAdapter.clear();
                break;
        }
    }


    @Override
    public void showDate(int year, int month, int day) {
        datePickerBtn.setText(new StringBuilder().append(String.format("%02d",day)).append("/")
                .append(String.format("%02d",month)).append("/").append(year));
    }


    @Override
    public void showTime(int hour, int minute) {
        timePickerBtn.setText(new StringBuilder().append(String.format("%02d",hour)).append(":")
        .append(String.format("%02d",minute)));
    }
}
