package com.example.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.adapters.BudgetAdapter;
import com.example.fbrigati.myfinance.data.BudgetLoader;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by FBrigati on 25/07/2017.
 */

public class BudgetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    final static String LOG_TAG = BudgetActivity.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.BudgetFragment.MESSAGE";

    public static final int BUDGET_LOADER = 5;

    private ArrayList<Budget> itemList;

    private View rootView;
    private TextView emptyView;
    private ListView budgetList;

    private int uMonth;
    private Cursor mCursor;


    BudgetAdapter budgetAdapter;

    public interface Callback {
        /*//**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void changeBudgetGoal(String title, long id);
    }


    static BudgetFragment newInstance(int month) {
        Bundle arguments = new Bundle();
        arguments.putInt(ID_MESSAGE, month);
        BudgetFragment fragment = new BudgetFragment();
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        Bundle arguments = getArguments();
        if (arguments != null){
            uMonth = arguments.getInt(ID_MESSAGE);
        }

        budgetAdapter = new BudgetAdapter(getActivity(), null, 0);

        emptyView = (TextView) rootView.findViewById(R.id.empty_budget);

        budgetList = (ListView) rootView.findViewById(R.id.budget_list);

        budgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view.findViewById(R.id.budgetTitle)).getText().toString();
                ((Callback) getActivity()).changeBudgetGoal(title, id);
                //editGoal.show(getFragmentManager(), "editMonth");
            }
        });

        budgetList.setAdapter(budgetAdapter);

        return rootView;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(BUDGET_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return BudgetLoader.newInstance(getContext(), uMonth);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        switch (loader.getId()) {
            case BUDGET_LOADER:

                Log.v(LOG_TAG, "onLoadFinish method called. data with: " + mCursor.getCount());

                if (mCursor != null && mCursor.moveToFirst() && mCursor.getCount() > 0) {
                    updateEmptyView(1);
                    budgetAdapter.swapCursor(mCursor);
                }else{
                    updateEmptyView(0);
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "Inside swapcursor...");
        budgetAdapter.swapCursor(null);
        mCursor = null;
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
