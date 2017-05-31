package com.example.fbrigati.myfinance.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by FBrigati on 25/05/2017.
 */

public class StatementActEditTrxDialog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerFragment.DateSetListenerCustom, TimePickerFragment.TimeSetListenerCustom {

    final static String LOG_TAG = StatementActEditTrxDialog.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.StatementFragEditTrxDialog.MESSAGE";

    public static final int CATEGORY_LOADER = 2;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.fragment_transaction_edit);

        Log.v(LOG_TAG, "Initialising category loader..");
        //Initialize cursor
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

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

        save_btn = (Button) findViewById(R.id.edit_trans_save_btn);

        close_btn = (Button) findViewById(R.id.edit_trans_close_btn);

        //user action to choose date from picker
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerDialog = new DatePickerFragment();
                datePickerDialog.show(getSupportFragmentManager(), "datePicker");
            }
        });

        timePickerBtn = (Button) findViewById(R.id.time_picker_btn);

        //user action to chose time from picker
        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerDialog = new TimePickerFragment();
                timePickerDialog.show(getSupportFragmentManager(), "timePicker");
            }
        });

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
            if(validateInputs()) saveDataToDB();
            }
        });

    }

    private void saveDataToDB() {
        ContentValues cv = new ContentValues();

        Date date;
        Integer dateInt = 0;
        String timeStr = "";
        Integer trxType = 0;


        try {
            date = new java.text.SimpleDateFormat("yyyyMMdd").parse(datePickerBtn.getText().toString());
            dateInt = Integer.parseInt(date.toString());
            date = new java.text.SimpleDateFormat("HHmm").parse(timePickerBtn.getText().toString());
            timeStr = date.toString();
            if(spinner_trxType.getSelectedItem().toString().equals("Debit")) trxType = 5; else trxType = 0;

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error while parsing date", Toast.LENGTH_LONG);
        }


        cv.put(DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER, "229801925");
        cv.put(DataContract.StatementEntry.COLUMN_DATE, dateInt);
        cv.put(DataContract.StatementEntry.COLUMN_TIME, timeStr);
        cv.put(DataContract.StatementEntry.COLUMN_SEQUENCE, 0);
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_ORIGIN, descText.getText().toString());
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_USER, descText.getText().toString());
        cv.put(DataContract.StatementEntry.COLUMN_AMOUNT, Double.valueOf(amountText.getText().toString()));
        cv.put(DataContract.StatementEntry.COLUMN_TRANSACTION_CODE, trxType);
        cv.put(DataContract.StatementEntry.COLUMN_ACQUIRER_ID, "0");
        cv.put(DataContract.StatementEntry.COLUMN_CATEGORY_KEY, spinner_ctg.getSelectedItem().toString());

        getContentResolver().insert(DataContract.CategoryEntry.CONTENT_URI, cv);
    }

    private boolean validateInputs() {
        boolean validate = true;

        //check length of description text
        if(descText.getText().length() < 1 ||
           descText.getText() == "" ||
           descText.getText() == null) validate = false;

        //check that amount is >0
        if(Double.valueOf(amountText.getText().toString()) == 0F ||
           Double.valueOf(amountText.getText().toString()) == 0) validate = false;

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
            case CATEGORY_LOADER:
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

                    do {
                        lables.add(data.getString(DataContract.CategoryEntry.COL_CATEGORY_USER_KEY));
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
        datePickerBtn.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    @Override
    public void showTime(int hour, int minute) {
        timePickerBtn.setText(new StringBuilder().append(hour).append(":")
        .append(minute));
    }
}
