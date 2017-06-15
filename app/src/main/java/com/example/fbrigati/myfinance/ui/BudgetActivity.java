package com.example.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.adapters.BudgetAdapter;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class BudgetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
BudgetSetDialog.setBudgetGoalListener{

    final static String LOG_TAG = BudgetActivity.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.BudgetFragment.MESSAGE";

    public static final int BUDGET_LOADER = 5;

    private ArrayList<Budget> itemList;

    private TextView emptyView;
    private ListView budgetList;

    public Budget[] budgetItem = {
      new Budget("2017","05","Transport",15000f,123456,"Transport Desc",154.00f)
    };

    BudgetAdapter budgetAdapter;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ID_MESSAGE, itemList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //addDummyData();

            setContentView(R.layout.activity_budget);

        if(savedInstanceState == null){
            //Create new test Item for Budget
            itemList = new ArrayList<Budget>(Arrays.asList(budgetItem));
        }else{
            itemList = savedInstanceState.getParcelableArrayList(ID_MESSAGE);
        }

        getSupportLoaderManager().initLoader(BUDGET_LOADER, null, this);

        //View rootView = inflater.inflate(R.layout.activity_budget, container, false);

        budgetAdapter = new BudgetAdapter(this, null, 0);

        //Get reference to the listView and attach adapter to it
        // ListView budgetLV = (ListView) findViewById(R.id.budget_list);

        emptyView = (TextView) findViewById(R.id.empty_budget);

        budgetList = (ListView) findViewById(R.id.budget_list);

        Toolbar toolbarView = (Toolbar) findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_budget_title);

        budgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view.findViewById(R.id.budgetTitle)).getText().toString();
                DialogFragment editGoal = BudgetSetDialog.newInstance(title, id);
                editGoal.show(getSupportFragmentManager(), "editMonth");
            }
        });

        budgetList.setAdapter(budgetAdapter);

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
                        this,
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

    @Override
    public void setBudget(Double amount, long id) {

        ContentValues cv = new ContentValues();

        cv.put(DataContract.BudgetEntry.COLUMN_AMOUNT, amount);

        //Update budget set for the given budget item
        getContentResolver().update(DataContract.BudgetEntry.CONTENT_URI, cv,
                DataContract.BudgetEntry._ID + "=" + id ,
                null);

    }
}
