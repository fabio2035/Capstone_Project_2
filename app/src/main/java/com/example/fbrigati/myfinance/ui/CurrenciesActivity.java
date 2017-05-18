package com.example.fbrigati.myfinance.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.fbrigati.myfinance.R;

/**
 * Created by FBrigati on 07/05/2017.
 */

public class CurrenciesActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currency);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(CurrenciesFragment.ID_MESSAGE, getIntent().getData());

            CurrenciesFragment fragment = new CurrenciesFragment();

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_currency_main,fragment)
                    .commit();

        }
    }
}
