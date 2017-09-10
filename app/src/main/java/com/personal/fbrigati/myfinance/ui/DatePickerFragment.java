package com.personal.fbrigati.myfinance.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import java.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

/**
 * Created by FBrigati on 28/05/2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    final static String LOG_TAG = DatePickerFragment.class.getSimpleName();

    public interface DateSetListenerCustom{
        void showDate(int year, int month, int day);
    }

    public DatePickerFragment(){

    }


    // Use this instance of the interface to deliver action events
    DateSetListenerCustom mListener;

    // Override the Fragment.onAttach() method to instantiate the DateSetListenerCustom
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DateSetListenerCustom so we can send events to the host
            mListener = (DateSetListenerCustom) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DateSetListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {

        // Do something with the date chosen by the user
        mListener = (DateSetListenerCustom) getActivity();
        Log.v(LOG_TAG, "Variables: " + year + "," + month + "," + day);

        mListener.showDate(year, month+1, day);
        this.dismiss();
    }
}
