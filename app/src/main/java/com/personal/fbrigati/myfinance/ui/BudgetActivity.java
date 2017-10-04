package com.personal.fbrigati.myfinance.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.personal.fbrigati.myfinance.R;
import com.personal.fbrigati.myfinance.Utility;
import com.personal.fbrigati.myfinance.data.DataContract;

import java.util.Calendar;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class BudgetActivity extends AppCompatActivity implements BudgetFragment.Callback,
BudgetSetDialog.setBudgetGoalListener{

    final static int INST_ID = 2;

    final static String LOG_TAG = BudgetActivity.class.getSimpleName();

    public final static String ID_MESSAGE = "com.personal.fbrigati.myfinance.ui.BudgetFragment.MESSAGE";
    public final static String ID_INSTRUCTIONS = "com.personal.fbrigati.myfinance.ui.BudgetActivity.INSTRUCTIONS";

    private boolean show_instrucions = false;

    public static final int BUDGET_LOADER = 5;

    private int lastPosition;

    private static final int NUM_PAGES = 12;
    private int startMonth;
    private int startYear;
    private ViewPager mPager;
    private BudgetActivity.BudgetPagerAdapter mPagerAdapter;

    private Toolbar toolbarView;
    private ImageButton bakBtn;
    private ImageButton fwdBtn;
    private TextView monthLabel;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
            if (extras != null) {
                show_instrucions = extras.getBoolean(this.ID_INSTRUCTIONS);
            }

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
        startYear = c.get(Calendar.YEAR);

        mPager = (ViewPager) findViewById(R.id.budget_pager);
        mPagerAdapter = new BudgetActivity.BudgetPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(startMonth-1);
        lastPosition = startMonth;

        monthLabel.setText(Utility.getMonth(this,mPager.getCurrentItem()+1));
        Utility.setNavigationMonth(this, startMonth);
        Utility.setNavigationYear(this, startYear);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int selectedPosition){
                if(lastPosition > selectedPosition){
                    //Subtract Month
                    navigateMonth(0);
                } else if(selectedPosition > lastPosition) {
                    //Add month
                    navigateMonth(1);
                }
                lastPosition = selectedPosition;
            }

            @Override
            public void onPageScrollStateChanged(int arg0){
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (show_instrucions){
        //check if instructions should be shown
        if (Utility.getInstructionStat(this, INST_ID)) {
            DialogFragment instruction = InstructionsDialog.newIstance(
                    getResources().getString(R.string.dialog_instruction_title_budget),
                    getResources().getString(R.string.dialog_instruction_text_budget),
                    INST_ID);
            instruction.show(getSupportFragmentManager(), "instruction");
        }
        }
    }


    private void navigateMonth(int i) {

        int currentSetMonth = Utility.getNavigationMonth(this);

        //move month forward or backwards depending on passed parameter
        if(i==0 && currentSetMonth >= 2){
            //move a month bak
            Utility.setNavigationMonth(this, currentSetMonth-1);
            mPagerAdapter.notifyDataSetChanged();
        }else if(i==1 && currentSetMonth <= 11){
            //move a month bak
            Utility.setNavigationMonth(this, currentSetMonth+1);
            mPagerAdapter.notifyDataSetChanged();
        }else{
            //Do nothing?
        }
        monthLabel.setText(Utility.getMonth(this,mPager.getCurrentItem()+1));
    }

    @Override
    public void changeBudgetGoal(String title, int month, int year, long id, boolean setYear) {
        DialogFragment editGoal = BudgetSetDialog.newInstance(title, month, year, id, setYear);
        editGoal.show(getSupportFragmentManager(), "editMonth");
    }

    @Override
    public void setBudget(Double amount,int month, int year,String category, long id, boolean setYear) {


        ContentValues cv = new ContentValues();

        cv.put(DataContract.BudgetEntry.COLUMN_AMOUNT, amount);
        cv.put(DataContract.BudgetEntry.COLUMN_MONTH, month);
        cv.put(DataContract.BudgetEntry.COLUMN_YEAR, year);
        cv.put(DataContract.BudgetEntry.COLUMN_CATEGORY, category);

        int updateQry = 0;

        //if setYear is true, update budget for whole year..
        if(setYear) {
            for (int i = 1; i <= 12; ++i) {
                cv.put(DataContract.BudgetEntry.COLUMN_MONTH, i);
                updateQry = getContentResolver().update(DataContract.BudgetEntry.CONTENT_URI, cv,
                        DataContract.BudgetEntry._ID + "=" + id,
                        null);

                //Log.v(LOG_TAG, "whole year update " + i );

                if (updateQry < 1) {
                    getContentResolver().insert(DataContract.BudgetEntry.CONTENT_URI, cv);
                }
                updateQry = 0;
            }
        }else{
        //Update budget only for the given budget item
        int UpdateNum = getContentResolver().update(DataContract.BudgetEntry.CONTENT_URI, cv,
                DataContract.BudgetEntry._ID + "=" + id ,
                null);
        //Log.v(LOG_TAG,"update count: " + UpdateNum);

        if (UpdateNum < 1){
            getContentResolver().insert(DataContract.BudgetEntry.CONTENT_URI, cv);
        }

    }

    }


    private class BudgetPagerAdapter extends FragmentStatePagerAdapter {

        public BudgetPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BudgetFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

}
