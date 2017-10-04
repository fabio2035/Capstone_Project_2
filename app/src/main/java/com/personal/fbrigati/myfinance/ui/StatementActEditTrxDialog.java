package com.personal.fbrigati.myfinance.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.personal.fbrigati.myfinance.R;
import com.personal.fbrigati.myfinance.Utility;
import com.personal.fbrigati.myfinance.data.DataContract;
import com.personal.fbrigati.myfinance.elements.Statement;
import com.personal.fbrigati.myfinance.sync.MFSyncJob;
import com.personal.fbrigati.myfinance.widget.FMWidgetProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

/**
 * Created by FBrigati on 25/05/2017.
 */

public class StatementActEditTrxDialog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerFragment.DateSetListenerCustom, TimePickerFragment.TimeSetListenerCustom {

    public final static String ID_MESSAGE = "com.personal.fbrigati.myfinance.ui.StatementFragEditTrxDialog.MESSAGE";
    public final static String ID_CAT_LIST = "com.personal.fbrigati.myfinance.ui.StatementFragEditTrxDialog.CAT_LIS";
    public final static String ID_CAT_SEL = "com.personal.fbrigati.myfinance.ui.StatementFragEditTrxDialog.CAT_SEL";
    public static final int CATEGORY_LOADER = 2;
    public static final int STATEMENT_LOADER_ID = 3;

    //Firebase variables
    public static final String ANONYMOUS = "anonymous";
    final static String LOG_TAG = StatementActEditTrxDialog.class.getSimpleName();

    Map<Integer, String> categoryMap;
    private TextView amountText;
    private TextView descText;
    private Button datePickerBtn;
    private Button timePickerBtn;
    private Spinner spinner_ctg;
    private Spinner spinner_trxType;
    private Button save_btn;
    private Button close_btn;
    private ArrayList<String> lables = null;
    private ArrayAdapter<String> categoryAdapter;
    private Uri detailUri;
    private Toolbar toolbarView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_transaction_edit);

        detailUri = getIntent().getData();

        //First time loaded
        lables = new ArrayList<String>();

        spinner_trxType = (Spinner) findViewById(R.id.trxType_spinner_id);

        spinner_trxType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    spinner_ctg.setEnabled(false);
                } else {
                    spinner_ctg.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.trx_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_trxType.setAdapter(adapter);

        amountText = (TextView) findViewById(R.id.amount_id);

        descText = (TextView) findViewById(R.id.description_id);

        spinner_ctg = (Spinner) findViewById(R.id.category_spinner_id);

        // Creating adapter for spinner
        categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        categoryAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_ctg.setAdapter(categoryAdapter);


        if (savedInstanceState != null) {
            //configuration changed...

            if (savedInstanceState.getStringArrayList(ID_CAT_LIST) != null) lables.addAll(savedInstanceState.getStringArrayList(ID_CAT_LIST));
            categoryAdapter.notifyDataSetChanged();
            if (savedInstanceState.getInt(ID_CAT_SEL) != 0) spinner_ctg.setSelection(savedInstanceState.getInt(ID_CAT_SEL));

        }else{

            if (detailUri != null) {
                //First load the category items
                getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
                getSupportLoaderManager().initLoader(STATEMENT_LOADER_ID, null, this);
            } else {
                //if its a new transaction only load category items..
                getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
            }
        }

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
                if (validateInputs()){
                    saveDataToDB();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.toast_check_desc_amt, Toast.LENGTH_LONG).show();
                }
            }
        });

        categoryMap = new HashMap<Integer, String>();

        if (detailUri == null) {
            setCurrentDateTimeButtons();
        }

        toolbarView = (Toolbar) findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_trxedit_title);

    }


    private void setCurrentDateTimeButtons() {
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH) + 1;
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        showDate(year, month, day);
        showTime(hour, minute);
    }

    private void saveDataToDB() {
        ContentValues cv = new ContentValues();

        Integer trxType = 0;

        String dateStr = String.format(Locale.US, "%08d", Integer.parseInt(datePickerBtn.getText().toString().replace("/", "")));
        StringBuilder dateBuild = new StringBuilder().append(dateStr.substring(4)).append(dateStr.substring(2, 4)).append(dateStr.substring(0, 2));

        String timeStr = String.format(Locale.US, "%04d", Integer.parseInt(timePickerBtn.getText().toString().replace(":", "")));

        Integer dateInt = Integer.parseInt(dateBuild.toString());
        Integer timeInt = Integer.parseInt(timeStr);

        if (spinner_trxType.getSelectedItemPosition() == 0){
            trxType = 6;
            cv.put(DataContract.StatementEntry.COLUMN_CATEGORY_KEY, categoryMap.get(spinner_ctg.getSelectedItemPosition()+1));
        }else{
            trxType = 0;
            cv.put(DataContract.StatementEntry.COLUMN_CATEGORY_KEY, "Credit");
        }

        cv.put(DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER, "0529925801");
        cv.put(DataContract.StatementEntry.COLUMN_DATE, dateInt);
        cv.put(DataContract.StatementEntry.COLUMN_TIME, timeStr);
        cv.put(DataContract.StatementEntry.COLUMN_SEQUENCE, Utility.getTransSequence(this));
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_ORIGIN, descText.getText().toString());
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_USER, descText.getText().toString());
        cv.put(DataContract.StatementEntry.COLUMN_AMOUNT, Double.valueOf(amountText.getText().toString()));
        cv.put(DataContract.StatementEntry.COLUMN_TRANSACTION_CODE, trxType);
        cv.put(DataContract.StatementEntry.COLUMN_ACQUIRER_ID, "0");


        Uri uri = getContentResolver().insert(DataContract.StatementEntry.CONTENT_URI, cv);

        int execNum = DataContract.StatementEntry.getIDFromUri(uri);

        if (execNum > 0) {
            Toast.makeText(getApplicationContext(), R.string.toast_savetodbsuccess, Toast.LENGTH_LONG).show();

            //Log.v(LOG_TAG, "SENDING BROADCAST FOR WIDGET UPDATE!!");
            updateWidgets();

            /*
            //if successfully saved to DB we can Save to Firbase aswell...
            Statement statementData = new Statement(DataContract.StatementEntry.getIDFromUri(uri),
                    "0529925801",
                    dateInt,
                    timeStr,
                    0,
                    descText.getText().toString(),
                    descText.getText().toString(),
                    Double.valueOf(amountText.getText().toString()),
                    trxType,
                    "0",
                    spinner_ctg.getSelectedItem().toString());

            mStatementDatabaseReference.push().setValue(statementData); */
        }

        finish();
    }

    public void updateWidgets() {
        Intent dataUpdatedIntent = new Intent(this, FMWidgetProvider.class)//MFSyncJob.ACTION_DATA_UPDATED)
                .setAction(MFSyncJob.ACTION_DATA_UPDATED);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(),
                FMWidgetProvider.class));
        dataUpdatedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids);
        sendBroadcast(dataUpdatedIntent);
    }


    private boolean validateInputs() {
        boolean validate = true;

        //check length of description text
        if (descText.getText().length() < 1 ||
                descText.getText() == "" ||
                descText.getText() == null) {
            validate = false;
            descText.requestFocus();
        }

        //check that amount is >0
        if (!isEmpty(amountText.getText())) {
            if (Double.valueOf(amountText.getText().toString()) == 0F ||
                    Double.valueOf(amountText.getText().toString()) == 0) {
                validate = false;
                amountText.requestFocus();
            }
        } else {
            validate = false;
            amountText.requestFocus();
        }

        return validate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save category selection
        outState.putStringArrayList(ID_CAT_LIST, lables);
        outState.putInt(ID_CAT_SEL, spinner_ctg.getSelectedItemPosition()+1);
        //save category list

        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case StatementFragment.STATEMENT_LOADER:
                //Todo: make account selection

            case CATEGORY_LOADER: {
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

        switch (loader.getId()) {
            case CATEGORY_LOADER: {

                if (data != null && data.moveToFirst() && data.getCount() > 0) {

                    lables.clear();

                    int i = 0;

                    do {
                        i++;
                        categoryMap.put(i, data.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT));
                        lables.add(Utility.getTranslation(this, "cat", data.getString(DataContract.CategoryEntry.COL_CATEGORY_DEFAULT)));
                    } while (data.moveToNext());

                    categoryAdapter.notifyDataSetChanged();

                }
                data.close();
                break;
            }
            case STATEMENT_LOADER_ID: {
                if (data != null && data.moveToFirst() && data.getCount() > 0) {

                    amountText.setText(String.valueOf(data.getDouble(6)));

                    descText.setText(data.getString(3));

                    Integer date = data.getInt(4);

                    String time = data.getString(5);

                    String dateStr = date.toString();

                    StringBuilder dateBuild = new StringBuilder().append(dateStr.substring(6, 8)).append("/").append(dateStr.substring(4, 6)).append("/").append(dateStr.substring(0, 4));

                    StringBuilder timeBuild = new StringBuilder().append(time.substring(0, 2)).append(":").append(time.substring(2, 4));

                    datePickerBtn.setText(dateBuild.toString());

                    timePickerBtn.setText(timeBuild.toString());

                    //Pick category
                    int j = categoryAdapter.getPosition(Utility.getTranslation(this, "cat", data.getString(7)));
                    spinner_ctg.setSelection(j);

                    //Pick trx type
                    if (data.getInt(8) > 5) {
                        spinner_trxType.setSelection(0);
                        spinner_ctg.setEnabled(true);
                    } else {
                        spinner_trxType.setSelection(1);
                        spinner_ctg.setEnabled(false);
                    }
                }
                data.close();
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()) {
            case CATEGORY_LOADER:
                if (categoryAdapter != null)
                    categoryAdapter = null;
                break;
        }

    }


    @Override
    public void showDate(int year, int month, int day) {
        datePickerBtn.setText(new StringBuilder().append(String.format(Locale.US, "%02d", day)).append("/")
                .append(String.format(Locale.US, "%02d", month)).append("/").append(year));
    }


    @Override
    public void showTime(int hour, int minute) {
        timePickerBtn.setText(new StringBuilder().append(String.format(Locale.US, "%02d", hour)).append(":")
                .append(String.format(Locale.US, "%02d", minute)));
    }
}
