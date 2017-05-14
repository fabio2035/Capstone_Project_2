package com.example.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.fbrigati.myfinance.R;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class StatsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(StatsFragment.ID_MESSAGE, getIntent().getData());

            StatsFragment fragment = new StatsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_stats_detail, fragment)
                    .commit();
        }
    }

}
