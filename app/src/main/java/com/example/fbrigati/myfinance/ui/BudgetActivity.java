package com.example.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.Utility;
import com.example.fbrigati.myfinance.adapters.BudgetAdapter;
import com.example.fbrigati.myfinance.data.DataContract;
import com.example.fbrigati.myfinance.elements.Budget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class BudgetActivity extends AppCompatActivity implements BudgetFragment.Callback,
BudgetSetDialog.setBudgetGoalListener{

    final static String LOG_TAG = BudgetActivity.class.getSimpleName();

    public final static String ID_MESSAGE = "com.example.fbrigati.myfinance.ui.BudgetFragment.MESSAGE";

    public static final int BUDGET_LOADER = 5;

    private int lastPosition;

    private static final int NUM_PAGES = 12;
    private int startMonth;
    private ViewPager mPager;
    private BudgetActivity.BudgetPagerAdapter mPagerAdapter;

    private Toolbar toolbarView;
    private ImageButton bakBtn;
    private ImageButton fwdBtn;
    private TextView monthLabel;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //addDummyData();

        setContentView(R.layout.activity_budget);

        //Get reference to the listView and attach adapter to it
        toolbarView = (Toolbar) findViewById(R.id.toolbar);

        toolbarView.setTitle("");

        bakBtn = (ImageButton) findViewById(R.id.bkbtn);

        monthLabel = (TextView) findViewById(R.id.monthlbl);

        bakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem()-1);
            }
        });

        fwdBtn = (ImageButton) findViewById(R.id.fwdbtn);

        fwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem()+1);
            }
        });

        final Calendar c = Calendar.getInstance();
        startMonth = c.get(Calendar.MONTH) +1;

        mPager = (ViewPager) findViewById(R.id.budget_pager);
        mPagerAdapter = new BudgetActivity.BudgetPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(startMonth-1);
        lastPosition = startMonth;

        monthLabel.setText(Utility.getMonth(this,mPager.getCurrentItem()+1));
        Utility.setNavigationMonth(this, startMonth);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int selectedPosition){

                Log.v(LOG_TAG, "position: " + selectedPosition);
                /*if(selectedPosition == 0){
                    mPager.setCurrentItem(1);
                }else*/ if(lastPosition > selectedPosition){
                    //Subtract Month
                    Log.v(LOG_TAG, "Swiped left, Selected Position:" + selectedPosition + " last Position: " + lastPosition);
                    navigateMonth(0);
                } else if(selectedPosition > lastPosition) {
                    //Add month
                    Log.v(LOG_TAG, "Swiped right, Selected Position:" + selectedPosition + " last Position: " + lastPosition);
                    navigateMonth(1);
                }
                lastPosition = selectedPosition;
            }

            @Override
            public void onPageScrollStateChanged(int arg0){
            }
        });

    }

    private void navigateMonth(int i) {

        int currentSetMonth = Utility.getNavigationMonth(this);

        Log.v(LOG_TAG, "currentSetMonth " + currentSetMonth);

        //move month forward or backwards depending on passed parameter
        if(i==0 && currentSetMonth >= 2){
            //move a month bak
            Log.v(LOG_TAG, "subtracting month..");
            Utility.setNavigationMonth(this, currentSetMonth-1);
            mPagerAdapter.notifyDataSetChanged();
        }else if(i==1 && currentSetMonth <= 11){
            //move a month bak
            Log.v(LOG_TAG, "adding month..");
            Utility.setNavigationMonth(this, currentSetMonth+1);
            mPagerAdapter.notifyDataSetChanged();
        }else{
            //Do nothing?
        }
        monthLabel.setText(Utility.getMonth(this,mPager.getCurrentItem()+1));
    }

    @Override
    public void changeBudgetGoal(String title, long id) {
        DialogFragment editGoal = BudgetSetDialog.newInstance(title, id);
        editGoal.show(getSupportFragmentManager(), "editMonth");
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


    private class BudgetPagerAdapter extends FragmentStatePagerAdapter {

        public BudgetPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(LOG_TAG, "Pager Item position: " + position + "sending Position: " + position+1);
            return BudgetFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

}
