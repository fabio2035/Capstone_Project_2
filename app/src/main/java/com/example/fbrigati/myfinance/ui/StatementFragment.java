package com.example.fbrigati.myfinance.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbrigati.myfinance.Utility;
import com.example.fbrigati.myfinance.adapters.StatementAdapter;
import com.example.fbrigati.myfinance.data.DataContract;

import com.example.fbrigati.myfinance.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;


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
    private ImageButton bakBtn;
    private ImageButton fwdBtn;
    private TextView monthLabel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Bundle arguments = getArguments();
        if (arguments != null){
            statement_uri = arguments.getParcelable(StatementFragment.STATEMENT_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_statement_main_bkp, container, false);

        //transaction date
        textDate = (TextView) rootView.findViewById(R.id.row_date);

        //user transaction notes
        textDescription = (TextView) rootView.findViewById(R.id.row_description);

        //transaction amount
        textAmount = (TextView) rootView.findViewById(R.id.row_amt);

        statement_details = (ListView) rootView.findViewById(R.id.item_statement_container);

        statement_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showTransactionEditDialog(id);
            }
        });

        statement_details.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                final Long identifier = id;

                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
                alert.setTitle(R.string.dialog_delete_record_title);
                alert.setMessage(R.string.dialog_delete_record_message);
                alert.setPositiveButton(R.string.dialog_delete_record_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecord(identifier);
                        //do your work here
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton(R.string.dialog_delete_record_no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });

        headerView = (ViewGroup) inflater.inflate(R.layout.item_statement_header, statement_details, false);

        statement_details.addHeaderView(headerView);

        statementAdapter = new StatementAdapter(getActivity(), null, 0);

        statement_details.setAdapter(statementAdapter);

        empty_view = (TextView) rootView.findViewById(R.id.empty_statement);

        toolbarView = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_statement_title);

        bakBtn = (ImageButton) rootView.findViewById(R.id.bkbtn);

        monthLabel = (TextView) rootView.findViewById(R.id.monthlbl);

        bakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateMonth(0);
            }
        });

        fwdBtn = (ImageButton) rootView.findViewById(R.id.fwdbtn);

        fwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateMonth(1);
            }
        });



        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionAddDialog(0L);
            }
        });

        /*mAdView = (AdView) rootView.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("53F4B94474E00A7E14FD516F7AD2ACDF")  // My Galaxy Nexus test phone
                .build();
        mAdView.loadAd(adRequest); */

        return rootView;

    }

    private void deleteRecord(Long id) {
        Log.v(LOG_TAG, "id is: " + id);

        int i = getActivity().getContentResolver().delete(
                DataContract.StatementEntry.CONTENT_URI,
                DataContract.StatementEntry._ID + " = ?",
                new String[] {String.valueOf(id)});

        if(i>0){
            Toast.makeText(getContext(), R.string.toast_deleterecord_success, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getContext(), R.string.toast_deleterecord_nosuccess, Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onResume(){
        super.onResume();
        navigateMonth(2);
    }


    private void navigateMonth(int i) {

        Context ctx = getContext();

        int currentSetMonth = Utility.getNavigationMonth(ctx);
        Log.v(LOG_TAG, "currentSetMonth:" + currentSetMonth);
        //move month forward or backwards depending on passed parameter
        if(i==0 && currentSetMonth >= 2){
            //move a month bak
            Log.v(LOG_TAG, "subtracting month..");
            Utility.setNavigationMonth(ctx, currentSetMonth-1);
            monthLabel.setText(Utility.getMonth(ctx,Utility.getNavigationMonth(ctx)));
            getLoaderManager().restartLoader(STATEMENT_LOADER, null, this);
        }else if(i==1 && currentSetMonth <= 11){
            //move a month bak
            Log.v(LOG_TAG, "adding month..");
            Utility.setNavigationMonth(getContext(), currentSetMonth+1);
            monthLabel.setText(Utility.getMonth(ctx,Utility.getNavigationMonth(ctx)));
            getLoaderManager().restartLoader(STATEMENT_LOADER, null, this);
        }else{
            //refresh current Month status onto month label
            monthLabel.setText(Utility.getMonth(ctx,Utility.getNavigationMonth(ctx)));
        }
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
        Log.v(LOG_TAG, "inside onCreateLoader");

        switch (id){
            case STATEMENT_LOADER:
                //Todo: make account selection
                //if(statement_uri!=null){
                //uri = DataContract.StatementEntry.buildStatementUri(statement_uri);
                String[] selectionArgs = {String.format("%02d", Utility.getNavigationMonth(getContext()))};
                //String selection = DataContract.StatementEntry.COLUMN_DATE


                return new CursorLoader(
                        getActivity(),
                        DataContract.StatementEntry.CONTENT_URI, //.buildStatsMonthUri(Utility.getNavigationMonth(getContext())),
                        DataContract.StatementEntry.STATEMENT_COLUMNS,
                        "substr(date,5,2) = ?",
                        selectionArgs,
                        null);
                    /*return new CursorLoader(
                            getActivity(),
                            DataContract.StatementEntry.CONTENT_URI, //.buildStatsMonthUri(Utility.getNavigationMonth(getContext())),
                            DataContract.StatementEntry.STATEMENT_COLUMNS,
                            "substr(date,5,2)*1 = ?",
                            selectionArgs,
                            null); */
                }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.v(LOG_TAG, "inside onLoadFinished data in cursor: " + data.getCount());

        switch (loader.getId()) {
            case STATEMENT_LOADER:
                Log.v(LOG_TAG, "there are items in cursor: " + data.getCount());
                if (data != null && data.moveToFirst() && data.getCount() > 0) {

                    statementAdapter.swapCursor(data);
                    updateView(1);
                }else updateView(0);

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

    private void updateView(int flag) {
        if(flag == 1){
            statement_details.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }else if(flag == 0){
            statement_details.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
    }
}
