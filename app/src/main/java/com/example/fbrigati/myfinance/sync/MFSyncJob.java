package com.example.fbrigati.myfinance.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.fbrigati.myfinance.ui.StatementFragment;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by FBrigati on 14/05/2017.
 */

public class MFSyncJob extends AbstractThreadedSyncAdapter {

    final static String LOG_TAG = MFSyncJob.class.getSimpleName();


    public MFSyncJob(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw CSV response as a string.
        String CSVStr = null;

        String format = ".csv";
        String format_2 = "c4l1";
        String currency = "EURMZN=X";

        try{
            final String BASE_URL = "http://finance.yahoo.com/d/quotes.csv?";
            final String QUERY_PARAM_1 = "e";
            final String QUERY_PARAM_2 = "f";
            final String QUERY_PARAM_3 = "s";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM_1, format)
                    .appendQueryParameter(QUERY_PARAM_2, format_2)
                    .appendQueryParameter(QUERY_PARAM_3, currency)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to Yahoo Fiinance, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                //setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
                return;
            }

            Log.v(LOG_TAG, "Response from server: " + buffer);

            CSVStr = buffer.toString();
            //getDataFromCSV(CSVStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            //setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

            return;
    }
}
}
