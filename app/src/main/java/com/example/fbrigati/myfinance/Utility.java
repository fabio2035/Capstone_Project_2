package com.example.fbrigati.myfinance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.fbrigati.myfinance.sync.MFSyncJob;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by FBrigati on 21/05/2017.
 */

public class Utility {

    HashMap<Integer, String> months;

    public static String getPreferredCurSymbol(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_cur_key),
                context.getString(R.string.pref_cur_default));
    }


    static public void setPrefferecSymbol(Context c, String Symbol){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(c.getString(R.string.pref_cur_key),Symbol);
        spe.apply();
    }

    @SuppressWarnings("ResourceType")
    static public @MFSyncJob.CurrencyFetchStatus
    int getCurrencyFetchStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_cur_status_key), 100);
    }

    static public void resetLocationStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_cur_status_key), MFSyncJob.CURRENCYFETCH_STATUS_OK);
        spe.apply();
    }

    static public String getMonth(Context c, int j){
        String[] monthNames = c.getResources().getStringArray(R.array.months);
        Log.v("Utility", "month: " + j);
        if(j==0){
            return monthNames[0];
        }else{
        return monthNames[j-1];}
    }

    static public void setNavigationMonth(Context c, int i){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_month_status), i);
        spe.apply();
    }

    public static int getNavigationMonth(Context c) {
        final Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getInt(c.getString(R.string.pref_month_status),
                month);
    }

    public static int getTransSequence(Context c){

        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int currentPostDate = Integer.parseInt(String.valueOf(year) +
                String.format("%02d", month) +
                String.format("%02d", day));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();

        int lastPostDate = sp.getInt(c.getString(R.string.pref_last_posting_date), 0);
        int currentSequence = sp.getInt(c.getString(R.string.pref_post_seq), 0);

        Log.v("Utility", "LastPostDate: " + lastPostDate + "; CurrentPostDate: " + currentPostDate);

        int sequence = 0;

        if(currentPostDate > lastPostDate){
            //current posting date is more recent so reset posting date and set sequence to zero
            spe.putInt(c.getString(R.string.pref_last_posting_date), currentPostDate);
            spe.apply();
        }else{
            //current posting date equal to last posting date, so add to posting sequence
            currentSequence += 1;
            sequence = currentSequence;
            spe.putInt(c.getString(R.string.pref_post_seq), currentSequence);
            spe.apply();
        }

        return sequence;
    }
}
