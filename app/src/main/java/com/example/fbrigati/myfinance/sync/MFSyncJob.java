package com.example.fbrigati.myfinance.sync;

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
import android.support.annotation.IntDef;
import android.util.Log;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.data.DataContract;

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

    public static final String ACTION_DATA_UPDATED = "com.example.fbrigati.myfinance.ACTION_DATA_UPDATED";

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

        Log.v(LOG_TAG, "checking cur preference: " + base_cur);

        String yql_query = "select * from yahoo.finance.xchange where pair in ";

        StringBuilder currencies_group = new StringBuilder();

        String[] symbolArray = context.getResources().getStringArray(R.array.cur_symbols);
        currencies_group.append("(");

        //Builds the currencies to show on listView
        for(int i=0; i<symbolArray.length; i++){
        if(!symbolArray[i].equals(base_cur))
            {
            currencies_group.append("\"" +symbolArray[i] + base_cur).append("\"");
            if(i < symbolArray.length - 1) {
                currencies_group.append(",");
                }
            }
        }
        currencies_group.append(")");


        Log.v(LOG_TAG, "defaultCur: " + currencies_group);
        String source = "store://datatables.org/alltableswithkeys";
        String joint = yql_query + currencies_group;

        Log.v(LOG_TAG, "Inside onPerformSync.. trying to fetch csv");

        try{
            final String BASE_URL = "http://query.yahooapis.com/v1/public/yql?";
            final String QUERY_PARAM_1 = "q";
            final String QUERY_PARAM_2 = "env";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM_1, joint)
                    .appendQueryParameter(QUERY_PARAM_2, source)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Connecting with URL: " + url.toString());

            // Create the request to Yahoo Fiinance, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

//            Log.v(LOG_TAG, "Buffered: " + reader.toString());
            String line;
            while ((line = reader.readLine()) != null) {
                Log.v(LOG_TAG, "Current csv Line: " + line);
                buffer.append(line + "\n");
            }

            getDataFromBuffer(buffer.toString());

            Log.v(LOG_TAG, "Buffered: " + buffer.toString());

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            setCurrencyFetchStatus(context, MFSyncJob.CURRENCYFETCH_STATUS_INVALID);
            context.getContentResolver().notifyChange(invalid_currencyFetch_uri, null, false);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            //setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
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

    private void getDataFromBuffer(String buffer) {

        context = getContext();

        try{
            String symbol;
            Double rate;
            String date;
            ArrayList<ContentValues> currCVs = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new StringReader(buffer)));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("rate");
            Log.v(LOG_TAG, "root element :" + doc.getDocumentElement().getNodeName());
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                    ContentValues currCV = new ContentValues();

                    Element eElement = (Element) nNode;
                    Log.v(LOG_TAG, "element id :" + eElement.getAttribute("id"));
                    Log.v(LOG_TAG, "------------");
                    Log.v(LOG_TAG, "Name: " + eElement.getElementsByTagName("Name").item(0).getTextContent());
                    symbol = eElement.getElementsByTagName("Name").item(0).getTextContent();
                    currCV.put(DataContract.CurrencyExEntry.COLUMN_SYMBOL, symbol);
                    Log.v(LOG_TAG, "Rate: " + eElement.getElementsByTagName("Rate").item(0).getTextContent());
                    rate = Double.parseDouble(eElement.getElementsByTagName("Rate").item(0).getTextContent());
                    currCV.put(DataContract.CurrencyExEntry.COLUMN_RATE, rate);
                    Log.v(LOG_TAG, "Date: " + eElement.getElementsByTagName("Date").item(0).getTextContent());
                    date = eElement.getElementsByTagName("Date").item(0).getTextContent();
                    currCV.put(DataContract.CurrencyExEntry.COLUMN_DATE, date);

                    currCVs.add(currCV);
                    }
                }

            context.getContentResolver()
                    .bulkInsert(
                            DataContract.CurrencyExEntry.CONTENT_URI,
                            currCVs.toArray(new ContentValues[currCVs.size()]));
    } catch (Exception e){
            Log.v(LOG_TAG, "there was an error: " + e.toString());
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
