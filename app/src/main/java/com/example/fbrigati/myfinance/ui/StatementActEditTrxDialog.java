package com.example.fbrigati.myfinance.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;

import org.w3c.dom.Text;

/**
 * Created by FBrigati on 25/05/2017.
 */

public class StatementActEditTrxDialog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerFragment.DateSetListenerCustom {

    final static String LOG_TAG = StatementActEditTrxDialog.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.StatementFragEditTrxDialog.MESSAGE";

    public static final int CATEGORY_LOADER = 2;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView amountText;
    private TextView descText;
    private Button datePickerBtn;
    private Button timePickerBtn;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_transaction_edit);

        //View rootView = inflater.inflate(R.layout.fragment_transaction_edit, container, false);

        amountText = (TextView) findViewById(R.id.amount_id);

        descText = (TextView) findViewById(R.id.description_id);

        datePickerBtn = (Button) findViewById(R.id.date_picker_btn);

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new DatePickerFragment();
                dialog.show(getSupportFragmentManager(), "datePicker");
            }
        });

        timePickerBtn = (Button) findViewById(R.id.time_picker_btn);

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
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Todo:fill the view elements with data from database
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void showDate(int year, int month, int day) {
        datePickerBtn.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    }
