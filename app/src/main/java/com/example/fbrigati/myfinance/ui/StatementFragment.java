package com.example.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.adapters.StatementAdapter;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Statement;

import com.example.fbrigati.myfinance.R;

import java.util.List;


/**
 * Created by FBrigati on 03/05/2017.
 */

public class StatementFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String LOG_TAG = StatementFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.StatementFragment.MESSAGE";
    private static final int STATEMENT_LOADER = 0;

    static final String STATEMENT_URI = "URI";

    StatementAdapter statementAdapter;

    private Uri statement_uri;



    public StatementFragment(){

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private TextView textSequence;
    private TextView textDate;
    private TextView textDescription;
    private TextView textPayee;
    private TextView textCategory;
    private TextView textAmount;
    private TextView textBalance;
    private ListView statement_details;
    private TextView empty_view;
    private View header_view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Bundle arguments = getArguments();
        if (arguments != null){
            statement_uri = arguments.getParcelable(StatementFragment.STATEMENT_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_statement_main, container, false);

        //transaction sequence
       // textSequence = (TextView) rootView.findViewById(R.id.row_num);

        //transaction date
        textDate = (TextView) rootView.findViewById(R.id.row_date);

        //user transaction notes
        textDescription = (TextView) rootView.findViewById(R.id.row_description);

        //acquirer of transaction
        //textPayee = (TextView) rootView.findViewById(R.id.row_payee);

        //transaction category
        //textCategory = (TextView) rootView.findViewById(R.id.row_cat);

        //transaction amount
        textAmount = (TextView) rootView.findViewById(R.id.row_amt);

        statement_details = (ListView) rootView.findViewById(R.id.item_statement_container);

        statementAdapter = new StatementAdapter(getActivity(), null, 0);

        statement_details.setAdapter(statementAdapter);


        empty_view = (TextView) rootView.findViewById(R.id.empty_statement);

        header_view = (View) rootView.findViewById(R.id.header);

        //account balance
        //textBalance = (TextView) rootView.findViewById(R.id.row_bal);

        //addDummyData();

        return rootView;

    }

    private void addDummyData() {

        ContentValues cv = new ContentValues();

        cv.put(DataContract.StatementEntry.COLUMN_ACCOUNT_NUMBER, "229801925");
        cv.put(DataContract.StatementEntry.COLUMN_DATE, 20170505);
        cv.put(DataContract.StatementEntry.COLUMN_TIME, "1705");
        cv.put(DataContract.StatementEntry.COLUMN_SEQUENCE, 2);
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_ORIGIN, "VB2 SB 100");
        cv.put(DataContract.StatementEntry.COLUMN_DESCRIPTION_USER, "VB2 SB 100");
        cv.put(DataContract.StatementEntry.COLUMN_AMOUNT, 100.20);
        cv.put(DataContract.StatementEntry.COLUMN_TRANSACTION_CODE, 4);
        cv.put(DataContract.StatementEntry.COLUMN_ACQUIRER_ID, "1044510");
        cv.put(DataContract.StatementEntry.COLUMN_CATEGORY_KEY, "N/A");

        getContext().getContentResolver().insert(DataContract.StatementEntry.CONTENT_URI, cv);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(STATEMENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Uri uri;

        switch (id){
            case STATEMENT_LOADER:
                Log.v(LOG_TAG, "statement cursor loader called with uri:" + statement_uri );
                //Todo: make account selection
                //if(statement_uri!=null){
                //uri = DataContract.StatementEntry.buildStatementUri(statement_uri);
                    return new CursorLoader(
                            getActivity(),
                            DataContract.StatementEntry.CONTENT_URI,
                            DataContract.StatementEntry.STATEMENT_COLUMNS,
                            null,
                            null,
                            null);

                }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case STATEMENT_LOADER:

                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    statementAdapter.swapCursor(data);
                    updateEmptyView(1);
                }else updateEmptyView(0);

                break;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Inside swapcursor...");
        switch (loader.getId()){
            case STATEMENT_LOADER:
                if(statementAdapter != null)
                statementAdapter.swapCursor(null);
                break;
        }

    }

    private void updateEmptyView(int flag) {
        if(flag == 1){
            header_view.setVisibility(View.VISIBLE);
            statement_details.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }else if(flag == 0){
            header_view.setVisibility(View.GONE);
            statement_details.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
    }
}
