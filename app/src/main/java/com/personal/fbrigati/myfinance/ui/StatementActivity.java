package com.personal.fbrigati.myfinance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.personal.fbrigati.myfinance.R;
import com.personal.fbrigati.myfinance.Utility;

import java.util.Calendar;

/**
 * Created by FBrigati on 30/04/2017.
 */

public class StatementActivity extends AppCompatActivity {

    final static String LOG_TAG = StatementActivity.class.getSimpleName();

    public final static String ID_INSTRUCTIONS = "com.personal.fbrigati.myfinance.ui.StatementActivity.INSTRUCTIONS";
    private boolean show_instrucions = false;
    final static int INST_ID = 2;

    private static final int NUM_PAGES = 12;
    private int startMonth;
    private ViewPager mPager;
    private StatementPagerAdapter mPagerAdapter;

    private int lastPosition;

    private Toolbar toolbarView;
    private ImageButton bakBtn;
    private ImageButton fwdBtn;
    private TextView monthLabel;


    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statement);

        //Instantiate a ViewPager and a PagerAdapter
        mPager = (ViewPager) findViewById(R.id.statement_pager);

        toolbarView = (Toolbar) findViewById(R.id.toolbar);

        toolbarView.setTitle(R.string.toolbar_statement_title);

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

        mPagerAdapter = new StatementPagerAdapter(getSupportFragmentManager());
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionAddDialog(0L);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        if (show_instrucions){
            Log.v(LOG_TAG,"Show instructions is true...");
            //check if instructions should be shown
            if (Utility.getInstructionStat(this, INST_ID)) {
                DialogFragment instruction = InstructionsDialog.newIstance(
                        getResources().getString(R.string.dialog_instruction_title_statement),
                        getResources().getString(R.string.dialog_instruction_text_statement),
                        INST_ID);
                instruction.show(getSupportFragmentManager(), "instruction");
            }
        }
    }


    private void showTransactionAddDialog(Long id) {

        intent = new Intent(this, StatementActEditTrxDialog.class);

        switch(id.toString()) {
            case "0": {
                intent.putExtra(StatementActEditTrxDialog.ID_MESSAGE, 0);
                this.startActivity(intent);
                break;
            }
            default:{
                intent.putExtra(StatementActEditTrxDialog.ID_MESSAGE, id);
                this.startActivity(intent);
                break;
            }
        }
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


    private class StatementPagerAdapter extends FragmentStatePagerAdapter {

        public StatementPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(LOG_TAG, "Pager Item position: " + position + "sending Position: " + position+1);
            return StatementFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }
}
