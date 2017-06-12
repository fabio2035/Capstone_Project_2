package com.example.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.adapters.StatementAdapter;
import com.example.fbrigati.myfinance.data.DataContract;

import com.example.fbrigati.myfinance.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by FBrigati on 03/05/2017.
 */

public class StatementFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String LOG_TAG = StatementFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.StatementFragment.MESSAGE";
    public static final int STATEMENT_LOADER = 0;

    static final String STATEMENT_URI = "URI";

    StatementAdapter statementAdapter;

    private Uri statement_uri;

    private AdView mAdView;


    Intent intent;

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
    private ViewGroup headerView;
    private Toolbar toolbarView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Bundle arguments = getArguments();
        if (arguments != null){
            statement_uri = arguments.getParcelable(StatementFragment.STATEMENT_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_statement_main_bkp, container, false);

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

        statement_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showTransactionEditDialog(id);
            }
        });

        headerView = (ViewGroup) inflater.inflate(R.layout.item_statement_header, statement_details, false);

        statement_details.addHeaderView(headerView);

        statementAdapter = new StatementAdapter(getActivity(), null, 0);

        statement_details.setAdapter(statementAdapter);

        empty_view = (TextView) rootView.findViewById(R.id.empty_statement);

        toolbarView = (Toolbar) rootView.findViewById(R.id.toolbar);


        toolbarView.setTitle(R.string.toolbar_statement_title);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionAddDialog(0L);
            }
        });

        mAdView = (AdView) rootView.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("53F4B94474E00A7E14FD516F7AD2ACDF")  // My Galaxy Nexus test phone
                .build();
        mAdView.loadAd(adRequest);


        //addDummyData();

        return rootView;

    }

    private void showTransactionEditDialog(Long id) {
        intent = new Intent(getActivity(), StatementActEditTrxDialog.class);
        Log.v(LOG_TAG, "Uri for editing Statement entry: " + DataContract.StatementEntry.buildStatementUri(id));
        intent.setData(DataContract.StatementEntry.buildStatementUri(id));
        getActivity().startActivity(intent);
    }

    private void showTransactionAddDialog(Long id) {

        intent = new Intent(getActivity(), StatementActEditTrxDialog.class);

        switch(id.toString()) {
            case "0": {
                intent.putExtra(StatementActEditTrxDialog.ID_MESSAGE, 0);
                getActivity().startActivity(intent);
                break;
            }
            default:{
                intent.putExtra(StatementActEditTrxDialog.ID_MESSAGE, id);
                getActivity().startActivity(intent);
                break;
            }
        }
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
//            header_view.setVisibility(View.VISIBLE);
            statement_details.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }else if(flag == 0){
  //          header_view.setVisibility(View.GONE);
            statement_details.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
    }
}
