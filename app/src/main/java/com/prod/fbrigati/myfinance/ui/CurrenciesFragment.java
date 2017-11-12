package com.prod.fbrigati.myfinance.ui;

import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.prod.fbrigati.myfinance.R;
import com.prod.fbrigati.myfinance.Utility;
import com.prod.fbrigati.myfinance.adapters.CurrenciesAdapter;
import com.prod.fbrigati.myfinance.data.DataContract;
import com.prod.fbrigati.myfinance.sync.MFSyncJob;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Arrays;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class CurrenciesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String LOG_TAG = CurrenciesFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.prod.fbrigati.myfinance.ui.CurrenciesFragment.MESSAGE";

    private static final int CURRENCIES_LOADER = 1;

    CurrenciesAdapter currencyAdapter;

    private ContentObserver mObserver;

    static final String CURRENCIES_URI = "CURURI";

    private Uri currencies_uri;

    private TextView textSymbol;
    private TextView textRate;
    private ListView currencyList;
    private TextView empty_view;
    private Spinner spinner_view;
    private TextView textDate;
    private AdView mAdView;
    private Toolbar toolbarView;
    private ProgressBar progress_bar;

    public CurrenciesFragment(){}

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Bundle arguments = getArguments();
        if (arguments != null){
            currencies_uri = arguments.getParcelable(CurrenciesFragment.CURRENCIES_URI);
        }

        //Reset currencyFetch status
        Utility.resetLocationStatus(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_currencies_main, container, false);

        //Date value
        textDate = (TextView) rootView.findViewById(R.id.cur_base_date_value);

        //symbol label
        textSymbol = (TextView) rootView.findViewById(R.id.symbol_value);

        //rate label
        textRate = (TextView) rootView.findViewById(R.id.rate_value);

        //empty view
        empty_view = (TextView) rootView.findViewById(R.id.empty_currencies);

        //Currencies listview
        currencyList = (ListView) rootView.findViewById(R.id.item_currencies_container);

        currencyAdapter = new CurrenciesAdapter(getActivity(), null, 0);

        currencyList.setAdapter(currencyAdapter);

        spinner_view = (Spinner) rootView.findViewById(R.id.symbols_spinner);

        toolbarView = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_currencies_title);

        //progress_bar = (ProgressBar) rootView.findViewById(R.id.progressBar1);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.cur_symbols, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_view.setAdapter(adapter);

        mObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                @MFSyncJob.CurrencyFetchStatus int status = Utility.getCurrencyFetchStatus(getActivity());
                switch(status){
                    case MFSyncJob.CURRENCYFETCH_STATUS_OK:
                    {
                        restarLoader();
                        currencyList.invalidateViews();
                        //currencyAdapter.notifyDataSetChanged();
                        updateEmptyView();
                        break;}
                    case MFSyncJob.CURRENCYFETCH_STATUS_INVALID:{
                        //Toast.makeText(getActivity(), R.string.pref_cur_status_invalid, Toast.LENGTH_LONG).show();
                        Utility.resetLocationStatus(getActivity());
                        restarLoader();
                        currencyList.invalidateViews();
                        //currencyAdapter.notifyDataSetChanged();
                        updateEmptyView();
                        break;}
                }
            }
        };

        getActivity().getContentResolver().registerContentObserver(MFSyncJob.invalid_currencyFetch_uri, false, mObserver);

        spinner_view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Object symbol = parent.getItemAtPosition(pos);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.apply();
                //Log.v(LOG_TAG, "symbol chosen: " + symbol.toString());
                Utility.setPrefferecSymbol(getActivity(), symbol.toString());
                MFSyncJob.syncImmediately(getActivity());
                //restarLoader();
                //currencyAdapter.notifyDataSetChanged();
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        mAdView = (AdView) rootView.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("53F4B94474E00A7E14FD516F7AD2ACDF")  // My Galaxy Nexus test phone
        //        .build();
        mAdView.loadAd(adRequest);

        return rootView;

    }


    private void restarLoader() {
        getLoaderManager().restartLoader(CURRENCIES_LOADER, null, this);
    }

    @Override
    public void onResume(){
        //Reset currencyFetch status
        Utility.resetLocationStatus(getActivity());

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //get last selected position
        if(SP!=null){
            String selected = SP.getString(getContext().getString(R.string.pref_cur_key), getContext().getString(R.string.pref_cur_default));
            getActivity().getContentResolver().registerContentObserver(DataContract.CurrencyExEntry.buildCurrencyUri(selected), false, mObserver);

            String[] symbolArray = getResources().getStringArray(R.array.cur_symbols);
            int pos = Arrays.asList(symbolArray).indexOf(selected.trim());

            spinner_view.setSelection(pos);
        }
        super.onResume();
    }

    @Override
    public void onPause(){
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(CURRENCIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case CURRENCIES_LOADER:
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String symbol = SP.getString(getContext().getString(R.string.pref_cur_key), getContext().getString(R.string.pref_cur_default));
                //Log.v(LOG_TAG, "Base symbol used: " + symbol);
                return new CursorLoader(
                        getActivity(),
                        DataContract.CurrencyExEntry.buildCurrencyUri(symbol),
                        DataContract.CurrencyExEntry.CURRENCIES_COLUMNS,
                        null,
                        null,
                        null);

        }

        return null;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case CURRENCIES_LOADER:
                //Log.v(LOG_TAG, "data in loader: " + data.getCount());
                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    currencyAdapter.swapCursor(data);
                    textDate.setText(data.getString(DataContract.CurrencyExEntry.COL_DATE));}
                updateEmptyView();
                break;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
                    currencyAdapter.swapCursor(null);
        }

    private void updateEmptyView() {
        if(currencyList.getCount() > 0){
            currencyList.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
            //progress_bar.setVisibility(View.VISIBLE);
        }else {
            currencyList.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
            //progress_bar.setVisibility(View.GONE);
        }
    }
}
