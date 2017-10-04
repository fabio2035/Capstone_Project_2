package com.personal.fbrigati.myfinance.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import java.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Created by FBrigati on 27/05/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    final static String LOG_TAG = TimePickerFragment.class.getSimpleName();

    public interface TimeSetListenerCustom{
        void showTime(int hour, int minute);
    }

    public TimePickerFragment(){

    }

    TimeSetListenerCustom mListener;

    // Override the Fragment.onAttach() method to instantiate the TimeSetListenerCustom
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the TimeSetListenerCustom so we can send events to the host
            mListener = (TimePickerFragment.TimeSetListenerCustom) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DateSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        mListener = (TimePickerFragment.TimeSetListenerCustom) getActivity();

        mListener.showTime(hourOfDay, minute);
        this.dismiss();
    }
}
