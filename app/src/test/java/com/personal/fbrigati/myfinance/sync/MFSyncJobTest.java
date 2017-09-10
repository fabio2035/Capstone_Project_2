package com.personal.fbrigati.myfinance.sync;

import android.net.Uri;
import android.util.Log;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by FBrigati on 14/05/2017.
 */
public class MFSyncJobTest {

    private static final String TEST_URL = "http://finance.yahoo.com/d/quotes.csv?e=.csv&f=c4l1&s=EURMZN=X";

    final static String LOG_TAG = MFSyncJobTest.class.getSimpleName();


    @org.junit.Test
    public void testGettingCSVData() {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw CSV response as a string.
        String CSVStr = null;

        String format = ".csv";
        String format_2 = "c4l1";
        String currency = "EURMZN=X";

        try {
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

            Assert.fail("Something went wrong!");
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