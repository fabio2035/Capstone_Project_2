package com.example.fbrigati.myfinance.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.Utility;
import com.example.fbrigati.myfinance.adapters.CurrenciesAdapter;
import com.example.fbrigati.myfinance.data.DataContract;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class CurrenciesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String LOG_TAG = CurrenciesFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.CurrenciesFragment.MESSAGE";

    private static final int CURRENCIES_LOADER = 1;

    CurrenciesAdapter currencyAdapter;

    static final String CURRENCIES_URI = "CURURI";

    private Uri currencies_uri;

    private TextView textSymbol;
    private TextView textRate;
    private ListView currencyList;
    private TextView empty_view;
    private Spinner spinner_view;

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


        View rootView = inflater.inflate(R.layout.fragment_currencies_main, container, false);

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.cur_symbols, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_view.setAdapter(adapter);

        return rootView;

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
                Log.v(LOG_TAG, "statement cursor loader called with uri:" + currencies_uri );

                return new CursorLoader(
                        getActivity(),
                        DataContract.CurrencyExEntry.CONTENT_URI,
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

                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    currencyAdapter.swapCursor(data);
                    updateEmptyView(1);
                }else updateEmptyView(0);

                break;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Inside swapcursor...");
        switch (loader.getId()){
            case CURRENCIES_LOADER:
                if(currencyAdapter != null)
                    currencyAdapter.swapCursor(null);
                break;
        }
    }

    private void updateEmptyView(int flag) {
        if(flag == 1){
            currencyList.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }else if(flag == 0){
            currencyList.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            parent.getItemAtPosition(position);

            Log.v(LOG_TAG, "Chosen symbol: " + parent.getItemAtPosition(position).toString());

            String Symbol = parent.getItemAtPosition(position).toString();

            SharedPreferences base_cur_settings = getPreferences(0);
            Utility.setPrefferecSymbol(this, Symbol);

        }
    }
}
