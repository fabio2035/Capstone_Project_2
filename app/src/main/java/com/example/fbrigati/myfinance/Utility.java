package com.example.fbrigati.myfinance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.fbrigati.myfinance.sync.MFSyncJob;

/**
 * Created by FBrigati on 21/05/2017.
 */

public class Utility {

    public static String getPreferredCurSymbol(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_cur_key),
                context.getString(R.string.pref_cur_default));
    }


    static public void setPrefferecSymbol(Context c, String Symbol){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(c.getString(R.string.pref_cur_key),Symbol);
        spe.commit();
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

   }
