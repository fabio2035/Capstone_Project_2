package com.example.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.adapters.BudgetAdapter;
import com.example.fbrigati.myfinance.adapters.BudgetItemArrayAdapter;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class BudgetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String LOG_TAG = BudgetFragment.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.BudgetFragment.MESSAGE";

    public static final int BUDGET_LOADER = 5;

    private String[] temp_items = new String[]{"Leisure", "Bills", "Transportation"};
    private int[] temp_items_values = new int[]{20, 50, 55};
    private ArrayList<Budget> itemList;

    private TextView emptyView;
    private ListView budgetList;

    public Budget[] budgetItem = {
      new Budget("2017","05","Transport",15000f,123456,"Transport Desc",154.00f)
    };

    BudgetAdapter budgetAdapter;


    public BudgetFragment(){
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(BUDGET_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState == null || !savedInstanceState.containsKey(ID_MESSAGE)) {
            //Create new test Item for Budget
            itemList = new ArrayList<Budget>(Arrays.asList(budgetItem));
        }else{
            itemList = savedInstanceState.getParcelableArrayList(ID_MESSAGE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ID_MESSAGE, itemList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){

        //addDummyData();

        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        budgetAdapter = new BudgetAdapter(getActivity(), null, 0);

        //Get reference to the listView and attach adapter to it
        ListView budgetLV = (ListView) rootView.findViewById(R.id.budget_list);

        emptyView = (TextView) rootView.findViewById(R.id.empty_budget);

        budgetList = (ListView) rootView.findViewById(R.id.budget_list);

        budgetLV.setAdapter(budgetAdapter);

    return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case BUDGET_LOADER:
                Log.v(LOG_TAG, "budget cursor loader called");
                //Todo: make account selection
                //if(statement_uri!=null){
                //uri = DataContract.StatementEntry.buildStatementUri(statement_uri);
                Calendar c = Calendar.getInstance();

                int month = c.get(Calendar.MONTH)+1;

                Log.v(LOG_TAG, "getting budget info for month: " + month);
                return new CursorLoader(
                        getActivity(),
                        DataContract.BudgetEntry.buildBudgetMonth(month),
                        null,
                        null,
                        null,
                        null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case BUDGET_LOADER:

                Log.v(LOG_TAG, "onLoadFinish method called. data with: " + data.getCount());

                if (data != null && data.moveToFirst() && data.getCount() > 0) {
                    updateEmptyView(1);
                    budgetAdapter.swapCursor(data);
                }else{
                    updateEmptyView(0);
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Inside swapcursor...");
        switch (loader.getId()){
            case BUDGET_LOADER:
                if(budgetAdapter != null)
                    budgetAdapter.swapCursor(null);
                break;
        }
    }



    private void updateEmptyView(int flag) {
        if(flag == 1){
            budgetList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }else if(flag == 0){
            budgetList.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

}
