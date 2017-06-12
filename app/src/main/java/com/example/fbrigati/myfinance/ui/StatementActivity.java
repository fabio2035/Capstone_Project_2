package com.example.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.fbrigati.myfinance.R;

/**
 * Created by FBrigati on 30/04/2017.
 */

public class StatementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statement);


        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(StatementFragment.ID_MESSAGE, getIntent().getData());

            StatementFragment fragment = new StatementFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_statement_detail, fragment)
                    .commit();
        }
    }
}
