package com.prod.fbrigati.myfinance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.prod.fbrigati.myfinance.sync.MFSyncJob;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by FBrigati on 21/05/2017.
 */

public class Utility {

    HashMap<Integer, String> months;


    public static final String[] categoriesArray = {"Transportation",
                                                "Leisure",
                                                "Food",
                                                "Education",
                                                "HealthCare",
                                                "Groceries",
                                                "Rent"};

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
        //Log.v("Utility", "month: " + j);
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

    static public void setNavigationYear(Context c, int i){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_year_status), i);
        spe.apply();
    }

    public static int getNavigationMonth(Context c) {
        final Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getInt(c.getString(R.string.pref_month_status),
                month);
    }

    public static int getNavigationYear(Context c) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getInt(c.getString(R.string.pref_year_status),
                year);
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

        //Log.v("Utility", "LastPostDate: " + lastPostDate + "; CurrentPostDate: " + currentPostDate);

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

    static public void setStatsNavYear(Context c, int i){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_stats_year_status), i);
        spe.apply();
    }

    static public void setStatsPieTrimester(Context c, int i){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_stats_trimester_status), i);
        spe.apply();
    }

    static public void setStatsCategory(Context c, String cat){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(c.getString(R.string.pref_stats_category_status), cat);
        spe.apply();
    }

    public static int getStatsTrimester(Context c) {
        final Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int quarter = 1;

        if(month >=0 && month <=2){
            //is first quarter
        }else if (month >=3 && month <=5){
            quarter =2;
        }else if (month >=6 && month <=8){
            quarter =3;
        }else if (month >=9 && month <=11){
            quarter =4;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getInt(c.getString(R.string.pref_stats_trimester_status),
                quarter);
    }

    public static String getStatsCategory(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getString(c.getString(R.string.pref_stats_category_status),
                "All");
    }

    public static int getStatsNavYear(Context c) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getInt(c.getString(R.string.pref_stats_year_status),
                year);
    }

    public static boolean getInstructionStat(Context c, int ID){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        switch (ID){
            case 1: return prefs.getBoolean(c.getString(R.string.pref_instruct_show_statement),
                    true);
            case 2: return prefs.getBoolean(c.getString(R.string.pref_instruct_show_budget),
                    true);
            case 3: return prefs.getBoolean(c.getString(R.string.pref_instruct_show_stats),
                    true);
            case 4: return prefs.getBoolean(c.getString(R.string.pref_instruct_show_currencies),
                    true);
            default: return true;
        }
    }

    public static void setInstructionStat(Context c, int ID, Boolean stat){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();

        switch (ID){
            case 1: spe.putBoolean(c.getString(R.string.pref_instruct_show_statement),
                    stat);
                    break;
            case 2: spe.putBoolean(c.getString(R.string.pref_instruct_show_budget),
                    stat);
                    break;
            case 3: spe.putBoolean(c.getString(R.string.pref_instruct_show_stats),
                    stat);
                    break;
            case 4: spe.putBoolean(c.getString(R.string.pref_instruct_show_currencies),
                    stat);
                    break;
        }
        spe.apply();
    }

    public static String getDayOfWeek(Context c, int value) {
        String[] dayofweek = c.getResources().getStringArray(R.array.dow);
        return dayofweek[value-1];
    }

    public static String getTranslation(Context c, String script, String wordRef){

        String translation = "";

        String [] categories = c.getResources().getStringArray(R.array.categories);

        if(wordRef!=null){
        switch (script){
            case "cat" : {
                //Log.v("translation", "wordRef :" + wordRef);
                translation = categories[Arrays.asList(categoriesArray).indexOf(wordRef)];

            }
        }
        }

        return translation;
    }

}
