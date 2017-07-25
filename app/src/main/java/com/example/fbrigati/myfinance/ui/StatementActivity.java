package com.example.fbrigati.myfinance.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fbrigati.myfinance.R;
import com.example.fbrigati.myfinance.Utility;
import com.example.fbrigati.myfinance.data.StatementLoader;
import com.example.fbrigati.myfinance.data.UpdateableFragment;

import java.util.Calendar;

/**
 * Created by FBrigati on 30/04/2017.
 */

public class StatementActivity extends AppCompatActivity {

    final static String LOG_TAG = StatementActivity.class.getSimpleName();

    private static final int NUM_PAGES = 12;
    private int startMonth;
    private ViewPager mPager;
    private StatementPagerAdapter mPagerAdapter;

    private Cursor mCursor;
    private int lastPosition;

    private Toolbar toolbarView;
    private ImageButton bakBtn;
    private ImageButton fwdBtn;
    private TextView monthLabel;


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
            //refresh current Month status onto month label
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

        /*@Override
        public int getItemPosition(Object object){
            StatementFragment f = (StatementFragment) object;
            if()
        }*/

    }
}
