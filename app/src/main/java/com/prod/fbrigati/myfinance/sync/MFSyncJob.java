package com.prod.fbrigati.myfinance.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.IntDef;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prod.fbrigati.myfinance.R;
import com.prod.fbrigati.myfinance.data.DataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by FBrigati on 14/05/2017.
 */

public class MFSyncJob extends AbstractThreadedSyncAdapter {

    final static String LOG_TAG = MFSyncJob.class.getSimpleName();

    private Context context;

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public static final Uri invalid_currencyFetch_uri = DataContract.CurrencyExEntry.CONTENT_URI.buildUpon().appendPath("invalid").build();

    public static final String ACTION_DATA_UPDATED = "com.prod.fbrigati.myfinance.ACTION_DATA_UPDATED";

    public MFSyncJob(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = getContext();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CURRENCYFETCH_STATUS_OK, CURRENCYFETCH_STATUS_INVALID, CURRENCYFETCH_STATUS_SERVER_INVALID})
    public @interface CurrencyFetchStatus {}

    public static final int CURRENCYFETCH_STATUS_OK = 0;
    public static final int CURRENCYFETCH_STATUS_SERVER_INVALID = 2;
    public static final int CURRENCYFETCH_STATUS_INVALID = 4;

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String base_cur = sp.getString(context.getString(R.string.pref_cur_key), context.getString(R.string.pref_cur_default)).trim();

        String yql_query = "select * from yahoo.finance.xchange where pair in ";

        StringBuilder currencies_group = new StringBuilder();

        String[] symbolArray = context.getResources().getStringArray(R.array.cur_symbols);
        //currencies_group.append("(");

        //Builds the currencies to show on listView
        for(int i=0; i<symbolArray.length; i++){
        if(!symbolArray[i].equals(base_cur))
            {
            //currencies_group.append("\"" +symbolArray[i] + base_cur).append("\"");
            currencies_group.append(symbolArray[i]);
            if(i < symbolArray.length - 1) {
                currencies_group.append(",");
                }
            }
        }
        //currencies_group.append(")");

        String source = "store://datatables.org/alltableswithkeys";
        //String joint = yql_query + currencies_group;
        String joint = currencies_group.toString();

        try {
            //final String BASE_URL = "http://query.yahooapis.com/v1/public/yql?";
            final String BASE_URL = "https://api.fixer.io/latest?";
            final String QUERY_PARAM_1 = "base";
            final String QUERY_PARAM_2 = "symbols";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM_1, base_cur)
                    .appendQueryParameter(QUERY_PARAM_2, joint)
                    .build();

            URL url = new URL(builtUri.toString().replace("%2",","));

            //Log.v(LOG_TAG, "URL: " + url.toString());

            RequestQueue queue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Check response
                            if (!response.isEmpty()) {
                                try {
                                    getDataFromBuffer(context, response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });


            queue.add(stringRequest);

        } catch (IOException e) {
            //Log.v(LOG_TAG, "error: " + e);
            //other generic errors will have to be thrown visually
            setCurrencyFetchStatus(context, MFSyncJob.CURRENCYFETCH_STATUS_INVALID);
            context.getContentResolver().notifyChange(invalid_currencyFetch_uri, null, false);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return;
    }
}

    private void setCurrencyFetchStatus(Context context, @CurrencyFetchStatus int currencyfetchStatusInvalid) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(context.getString(R.string.pref_cur_status_key), currencyfetchStatusInvalid);
        spe.apply();
    }

    private static void getDataFromBuffer(Context context, String buffer) throws JSONException  {


            final String JO_RATE = "rates";
            final String JO_BASE = "base";
            final String JO_DATE = "date";

            ArrayList<ContentValues> currCVs = new ArrayList<>();

            JSONObject json = new JSONObject(buffer);
            JSONObject rateList = json.getJSONObject(JO_RATE);
            //rateList.put(json.get(JO_RATE));
            //JSONArray rateList = json.getJSONArray(JO_RATE);
            //Vector<ContentValues> cVVector = new Vector<ContentValues>(rateList.length());
                Iterator iterator = rateList.keys();
            String basecur = json.get(JO_BASE).toString();
            String date = json.get(JO_DATE).toString();

                while (iterator.hasNext()){

                    ContentValues rateValues = new ContentValues();

                    String key = (String)iterator.next();

                    rateValues.put(DataContract.CurrencyExEntry.COLUMN_SYMBOL, basecur.trim() + "/" + key.trim());
                    rateValues.put(DataContract.CurrencyExEntry.COLUMN_RATE, Double.valueOf(rateList.get(key).toString()));
                    //date was previously in format: mm/dd/yyyy
                    rateValues.put(DataContract.CurrencyExEntry.COLUMN_DATE, date);
                    currCVs.add(rateValues);
                }

            //Add to database
            if(currCVs.size() > 0) {

                context.getContentResolver()
                        .bulkInsert(
                                DataContract.CurrencyExEntry.CONTENT_URI,
                                currCVs.toArray(new ContentValues[currCVs.size()]));
            }
    }



    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MFSyncJob.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
